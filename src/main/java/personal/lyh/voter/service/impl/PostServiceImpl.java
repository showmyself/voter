package personal.lyh.voter.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import personal.lyh.voter.entity.*;
import personal.lyh.voter.entity.dto.PostDto;
import personal.lyh.voter.mapper.*;
import personal.lyh.voter.other.MyConstant;
import personal.lyh.voter.other.MyUtil;
import personal.lyh.voter.other.UploadUtil;
import personal.lyh.voter.service.IPostService;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//服务实现类
@Service
public class PostServiceImpl implements IPostService
{
    @Autowired
    PostMapper postMapper;
    @Autowired
    OptionMapper optionMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    VotedMapper votedMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    CollectMapper collectMapper;
    @Autowired
    LikedMapper likedMapper;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    UploadUtil uploadUtil;

    @Override
    public Result findPosts(Integer currentPage, Integer pageSize,
                            String keyword, String category, String timeOrHot){

        QueryWrapper<Post> queryWrapper=new QueryWrapper<>();

        queryWrapper.eq("`key`","");//只能查找没有秘钥的公开的帖子

        boolean noKeywordOrCategory= true;

        if(category!=null&&!category.equals("")){

            queryWrapper.and((wrapper)->{ wrapper.like("category",category); });
            noKeywordOrCategory=false;
        }

        if(keyword!=null&&!keyword.equals("")){
            queryWrapper.and((wrapper)->{//lambda形式是为了在sql where条件语句中产生括号
                wrapper.or().like("title",keyword)
                        .or().like("content",keyword);
            });
            noKeywordOrCategory=false;
        }

        //没有关键字或者类别，则搜查最近一周的数据
        if(noKeywordOrCategory)
            queryWrapper.ge("start_time", MyUtil.getPreviousSeventhDate());

        //排序方式
        if(timeOrHot.equals("hot"))
            queryWrapper.orderByDesc("view_count");
        else
            queryWrapper.orderByDesc("start_time");//搜索默认按时间降序

        //仅查询部分
        queryWrapper.select("id","title","content","view_count","end_time","max_vote_count","vote_count","number");

        //分页处理
        Page page = new Page(currentPage, pageSize);
        IPage pageData = postMapper.selectPage(page,queryWrapper);

        //获取是否截止
        List<Post> posts=pageData.getRecords();
        posts.forEach((post)->{
            if(checkFinished(post)==null)
                post.setFinished(false);
            else post.setFinished(true);
        });

        return Result.success("",MapUtil.builder()
                .put("posts",posts)
                .put("total",pageData.getTotal())
                .map());
    }

    @Override
    public Result findCurrentUserPosts(Integer currentPage, Integer pageSize)
    {
        //检查登录
        Long userId = MyUtil.getCurrentUserId();

        QueryWrapper<Post> queryWrapper=new QueryWrapper<>();

        //自己的帖子直接可获取key
        queryWrapper.eq("user_id",userId)
                .select("id","title","content","pic_src","view_count","end_time","max_vote_count","vote_count","`key`","number","comment_able")
                .orderByDesc("start_time");//按时间降序

        //分页处理
        Page page = new Page(currentPage, pageSize);
        IPage pageData = postMapper.selectPage(page,queryWrapper);

        //获取是否截止
        List<Post> posts=pageData.getRecords();
        posts.forEach((post)->{
            //获取用户自己的帖子就不用隐藏秘钥了
//            if(post.getKey()!=null&&!post.getKey().equals(""))//如果存在秘钥，则将秘钥设置为1再传给前端
//                post.setKey("1");

            if(checkFinished(post)==null)
                post.setFinished(false);
            else post.setFinished(true);
        });

        return Result.success("",MapUtil.builder()
                .put("posts",posts)
                .put("total",pageData.getTotal())
                .map());
    }

    @Override
    public Result findPostsInCollects(Integer currentPage, Integer pageSize){
        //检查登录
        Long userId = MyUtil.getCurrentUserId();

        //获得个人收藏的帖子id列表
        List<Collect> collects=collectMapper.selectList(new QueryWrapper<Collect>()
        .eq("user_id",userId));
        List<Long> postIds=new ArrayList<>();
        for (Collect collect : collects) {
            postIds.add(collect.getPostId());
        }
        //收藏为零时，需要单独写，不然报错
        if(postIds.size()<1){
            return Result.success("",MapUtil.builder()
                    .put("posts",new ArrayList<Post>())
                    .put("total",0)
                    .map());
        }

        QueryWrapper<Post> queryWrapper=new QueryWrapper<>();

        queryWrapper.in("id",postIds)
                .select("id","title","content","view_count","end_time","max_vote_count","vote_count","`key`","number")
                .orderByDesc("start_time");//按帖子的时间降序

        //分页处理
        Page page = new Page(currentPage, pageSize);
        IPage pageData = postMapper.selectPage(page,queryWrapper);

        //获取是否截止
        List<Post> posts=pageData.getRecords();
        posts.forEach((post)->{
            if(post.getKey()!=null&&!post.getKey().equals(""))//如果存在秘钥，则将秘钥设置为1再传给前端
                post.setKey("1");

            if(checkFinished(post)==null)
                post.setFinished(false);
            else post.setFinished(true);
        });

        return Result.success("",MapUtil.builder()
                .put("posts",posts)
                .put("total",pageData.getTotal())
                .map());
    }

    @Override
    public Result findAllPostsInSameSeries(Long userId,String series) {

        var postList = postMapper.selectList(new QueryWrapper<Post>()
                .eq("user_id",userId).eq("series",series)
                .select("id","title","content","view_count","end_time","max_vote_count","vote_count","`key`","number")
                .orderByAsc("start_time"));//按帖子的时间升序)


        //获取是否截止
        postList.forEach((post)->{
            if(post.getKey()!=null&&!post.getKey().equals(""))//如果存在秘钥，则将秘钥设置为1再传给前端
                post.setKey("1");
            if(checkFinished(post)==null)
                post.setFinished(false);
            else post.setFinished(true);
        });

        return Result.success("",MapUtil.builder()
                .put("posts",postList)
                .map());
    }

    @Override
    public Result getAllSelfSeries() {
        var userId = MyUtil.getCurrentUserId();

        var postList = postMapper.selectList(new QueryWrapper<Post>()
                .eq("user_id",userId)
                .select("series")
                .orderByAsc("series"));

        var seriesSet = new HashSet<String>();
        postList.forEach((post)->{
            var series = post.getSeries();
            if(series!=null&&!series.equals(""))
                seriesSet.add(post.getSeries());
        });

        return Result.success("",MapUtil.builder()
                .put("seriesList",seriesSet.toArray())
                .map());
    }

    @Override
    public Result tryByNumber(String number,String key){
        Post post=postMapper.selectOne(new QueryWrapper<Post>()
                .eq("number",number).select("id","`key`"));

        if(post==null){
            return Result.fail("该编码对应的帖子不存在");
        }

        //秘钥为空串时，不需要秘钥验证，否则
        if(post.getKey()!=null&&!post.getKey().equals(""))
        {
            if(key==null||!post.getKey().equals(key))
                return Result.fail("秘钥不正确");
        }

        return Result.success("",MapUtil.builder().put("id",post.getId()).map());
    }

    @Override
    @Transactional
    public Result getById(Long id, String key) {
        var caches = cacheManager.getCache(MyConstant.CacheType_Post);
        var cache = caches.get(id + key);

        Result result = null;
        if (cache != null) {
            result = (Result) cache.get();
        }

        Post post;

        if (result != null) {
            post = (Post) result.getData();
        } else {
            post = postMapper.selectById(id);
            if (post == null) {
                return Result.fail("目标帖子不存在，可能已被删除");
            }

            //源秘钥为空串时，不需要秘钥验证，否则需要验证：
            if (post.getKey() != null && !post.getKey().equals("")) {
                if (key == null)
                    return Result.fail("秘钥不正确");
                if (!post.getKey().equals(key))
                    return Result.fail("秘钥不正确");
            }

            //获取帖子的选项
            List<Option> options = optionMapper.selectList(new QueryWrapper<Option>()
                    .eq("post_id", post.getId()));
            post.setOptions(options);

            //获得评论数量
            if (post.getCommentAble())
                post.setCommentCount(commentMapper.selectCount(new QueryWrapper<Comment>()
                        .eq("post_id", id)));

            //截止检测
            String reason = checkFinished(post);
            post.setFinished(reason != null);

            result = Result.success("", post);
            caches.put(id + key, result);
        }

        try {

            //浏览量加1（设置当登录用户浏览自己的帖子也会增加浏览量，因为未登录用户也能频繁点击自己发的帖子，所以设置限制也没用了）
            postMapper.update(null, new UpdateWrapper<Post>()
                    .eq("id", post.getId()).set("view_count", post.getViewCount()+1));

            //重新赋值一下
            post.setViewCount(post.getViewCount()+1);
            result.setData(post);
            caches.put(id+key,result);

            return result;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }

    }

    @Override
    @Transactional
    public Result add(PostDto postDto)
    {
        //检查登录
        Long userId = MyUtil.getCurrentUserId();

        //检查编号合法性
        if(postMapper.selectOne(new QueryWrapper<Post>().eq("number",postDto.getNumber()))!=null)
            return Result.fail("编号重复");

        //截止日期格式检查
        //String转换Date
        Date endTime = null;
        if(postDto.getEndTime()!=null&&!postDto.getEndTime().equals(""))//截止时间可以为null，表示不限时
        {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                endTime = dateformat.parse(postDto.getEndTime());
            }
            catch (ParseException e) {
                e.printStackTrace();
                return Result.fail("添加失败，截止时间不正确");
            }
        }
        Date now=new Date();
        if(endTime!=null&&endTime.before(now)){
            return Result.fail("添加失败，截止时间不能在当前时间之前");
        }

        //积分检查
        User user=userMapper.selectOne(new QueryWrapper<User>().eq("id",userId)
        .select("id","mark"));
        if(user==null)return Result.fail("用户不存在");
        if(user.getMark()<MyConstant.newPostCostMark)
            return Result.fail("积分不足");

        //将null设置为""
        if(postDto.getKey()==null)postDto.setKey("");
        if(postDto.getContent()==null)postDto.setContent("");
        if(postDto.getMaxVoteCount()==null)postDto.setMaxVoteCount(0);//最大票数为零时表示不限票
        //截止时间为null时表示不限时

        Post post=new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPicSrc(postDto.getPicSrc());
        post.setCategory(postDto.getCategory());
        post.setNumber(postDto.getNumber());
        post.setKey(postDto.getKey());//帖子的秘钥暂不做加密，因为即使加密了，从数据库里也能看得到选票结果
        post.setStartTime(now);
        post.setEndTime(endTime);//可以为null，表示不限时
        post.setCommentAble(postDto.getCommentAble());
        post.setMaxVoteCount(postDto.getMaxVoteCount());//0或null则不限票
        post.setSeries(postDto.getSeries());
        post.setVoteCount(0);
        post.setViewCount(0L);
        post.setUserId(userId);

        try{
            postMapper.insert(post);

            postDto.getOptions().forEach((option)->{
                option.setPostId(post.getId());
                option.setCount(0);
                optionMapper.insert(option);
            });

            //只更新指定字段
            userMapper.update(null,new UpdateWrapper<User>()
            .eq("id",userId).set("mark",user.getMark()-MyConstant.newPostCostMark));

            return Result.success("添加成功",MapUtil.builder()
                    .put("id",post.getId()).map());
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常，添加失败");
        }

    }

    @Override
    @Transactional
    public Result update(PostDto postDto)
    {
        //检查登录
        Long userId = MyUtil.getCurrentUserId();

        //检查所有权
        Post post = postMapper.selectById(postDto.getId());
        if(!post.getUserId().equals(userId)){
            return Result.fail("无权修改他人的帖子");
        }

        //检查编号合法性
        if(!post.getNumber().equals(postDto.getNumber())){
            if(postMapper.selectOne(new QueryWrapper<Post>().eq("number",postDto.getNumber()))!=null)
                return Result.fail("编号重复");
        }

        //不能修改下面这些，因为可能修改原意，比如原本说喜欢什么，后面改成讨厌什么，就会导致投票结果文不对题
        //另外投票具有时效性，如果能任意修改验收时间会造成结果不准确
//        post.setTitle(postDto.getTitle());
//        post.setContent(postDto.getContent());
//        post.setPicSrc(postDto.getPicSrc());
//        post.setEndTime(endTime);
//        post.setMaxVoteCount(postDto.getMaxVoteCount());


        if(postDto.getKey()==null)postDto.setKey("");//将null设置为""
        post.setCategory(postDto.getCategory());
        post.setNumber(postDto.getNumber());
        post.setKey(postDto.getKey());
        post.setCommentAble(postDto.getCommentAble());
        post.setSeries(postDto.getSeries());

        try{
            postMapper.updateById(post);

            //更新不会修改选项

            //清除缓存
            var caches = cacheManager.getCache(MyConstant.CacheType_Post);
            caches.evictIfPresent(post.getId() + post.getKey());

            return Result.success("更新成功",null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常，更新失败");
        }

    }

    @Override
    public Result hasVoted(Long id)
    {
        //检查登录
        Long userId = MyUtil.getCurrentUserId();

        Voted voted = votedMapper.selectOne(new QueryWrapper<Voted>()
                .eq("user_id", userId).eq("post_id", id).select("option_id"));

        Long optionId = null;
        if (voted != null)
            optionId = voted.getOptionId();

        return Result.success("", MapUtil.builder().put("optionId", optionId).map());
    }

    @Override
    @Transactional
    public Result vote(Long id,Long optionId)
    {
        //检查登录
        Long userId = MyUtil.getCurrentUserId();

        Voted voted = votedMapper.selectOne(new QueryWrapper<Voted>()
                .eq("user_id", userId).eq("post_id", id));

        //设定可以给自己的发帖投票
        if (voted != null)
            return Result.fail("已投过票");

        //检查截止限制
        Post post=postMapper.selectOne(new QueryWrapper<Post>().select("id","end_time","max_vote_count","vote_count","`key`")
        .eq("id",id));
        String result=checkFinished(post);
        if(result!=null){
            return Result.fail(result);//result装载了失败的原因
        }

        Option option = optionMapper.selectOne(new QueryWrapper<Option>().eq("id",optionId).select("count"));
        if(option==null)
            return Result.fail("没有对应选项");

        voted=new Voted();
        voted.setPostId(id);
        voted.setUserId(userId);
        voted.setOptionId(optionId);

        try{
            votedMapper.insert(voted);

            optionMapper.update(null,new UpdateWrapper<Option>().eq("id",optionId).set("count",option.getCount()+1));

            User user=userMapper.selectOne(new QueryWrapper<User>().eq("id",userId).select("mark"));
            userMapper.update(null,new UpdateWrapper<User>().eq("id",userId)
            .set("mark",user.getMark()+ MyConstant.voteAddMark));

            postMapper.update(null,new UpdateWrapper<Post>().eq("id",id).set("vote_count",post.getVoteCount()+1));

            //清除缓存
            var caches=cacheManager.getCache(MyConstant.CacheType_Post);
            caches.evictIfPresent(id+post.getKey());

            return Result.success("",null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    @Override
    @Transactional
    public Result delete(Long id)
    {
        //检查登录
        Long userId = MyUtil.getCurrentUserId();

        Post post=postMapper.selectOne(new QueryWrapper<Post>().eq("id",id).select("user_id","pic_src","`key`"));
        if(post==null){
            return Result.fail("帖子不存在");
        }
        if(!post.getUserId().equals(userId)){
            return Result.fail("不是个人帖，无法删除");
        }

        //先删除关联的评论
        //先找相关评论id
        List<Comment> comments=commentMapper.selectList(new QueryWrapper<Comment>()
        .eq("post_id",id).select("id"));
        List<Long> commentIds=new ArrayList<>();
        for (Comment comment : comments)
            commentIds.add(comment.getId());

        try{
            //删除相关点赞记录：
            if(commentIds.size()>0)
                likedMapper.delete(new QueryWrapper<Liked>().in("comment_id",commentIds));
            //删除评论
            commentMapper.delete(new QueryWrapper<Comment>().eq("post_id",id));
            //删除收藏
            collectMapper.delete(new QueryWrapper<Collect>().eq("post_id",id));
            //删除投票记录
            votedMapper.delete(new QueryWrapper<Voted>().eq("post_id",id));

            //删除选项
            var optionList = optionMapper.selectList(new QueryWrapper<Option>().eq("post_id",id).select("pic_src"));
            optionList.forEach((op)-> uploadUtil.deleteFile(MyUtil.getLastStrFromPath(op.getPicSrc()),"pictures"));
            optionMapper.delete(new QueryWrapper<Option>().eq("post_id",id));

            //删除自身
            uploadUtil.deleteFile(MyUtil.getLastStrFromPath(post.getPicSrc()),"pictures");
            postMapper.deleteById(id);

            //清除缓存
            var caches = cacheManager.getCache(MyConstant.CacheType_Post);
            caches.evictIfPresent(id + post.getKey());

            return Result.success("",null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("");
        }
    }

    @Override
    public Result getKey(Long id)
    {
        //检查登录
        Long userId = MyUtil.getCurrentUserId();

        //先找到对应帖子
        Post post=postMapper.selectOne(new QueryWrapper<Post>()
        .eq("id",id).select("user_id","`key`"));

        if(post==null)
            return Result.fail("帖子不存在");

        if(!post.getUserId().equals(userId))
            return Result.fail("该帖不是个人帖，无法查看秘钥");

        return Result.success("",MapUtil.builder().put("key",post.getKey()).map());
    }

    @Override
    @Transactional
    public Result changeCommentAble(Long id, boolean isAble)
    {
        //检查登录
        Long userId = MyUtil.getCurrentUserId();

        //先找到对应帖子
        Post post=postMapper.selectOne(new QueryWrapper<Post>()
                .eq("id",id).select("id","user_id"));

        if(post==null){
            return Result.fail("帖子不存在");
        }

        if(!post.getUserId().equals(userId)){
            return Result.fail("不是个人帖，无法更改");
        }

        try{
            postMapper.update(null,new UpdateWrapper<Post>().eq("id",id)
                    .set("comment_able",isAble));
            return Result.success("",null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("");
        }
    }

    @Override
    public Result getUserInfosOfVotesInPost(Long id) {

        //统计一个post的每个选择对应的全体用户信息（仅性别、年龄）

        List<Voted> voteds=votedMapper.selectList(new QueryWrapper<Voted>().eq("post_id",id).select("user_id","option_id"));

        var mapBuilder=MapUtil.builder();

        //获取选项名列表
        var postIds= new ArrayList<Long>();
        for (var voted: voteds) {
            postIds.add(voted.getPostId());
        }

        //每个选项包含的用户信息列表
        postIds.forEach((postId)->{

            //把user_id筛选出来
            List<Long> userIds=new ArrayList<>();
            voteds.forEach((voted)->{
                if(voted.getOptionId().equals(postId))
                    userIds.add(voted.getUserId());
            });

            //根据id查user
            List<User> users=userMapper.selectList(new QueryWrapper<User>().in("user_id",userIds).select("sex","age"));
            mapBuilder.put(postId,users);
        });

        return Result.success("",mapBuilder.build());
    }

    @Override
    public Result uploadPicture(MultipartFile file) throws UnknownHostException {

        if(!MyUtil.checkFileSize(file.getSize(),1000,"K")){
            return Result.fail("不支持上传 大小超过1000KB的图片");
        }

        return uploadUtil.uploadFile(file, "pictures");
    }

    @Override
    public Result deletePicture(String avatarFileName) {
        return uploadUtil.deleteFile(avatarFileName,"pictures");
    }

    @Override
    public Result getUsernamesOfPost(Long id) {
        var votedList = votedMapper.selectList(new QueryWrapper<Voted>().eq("post_id",id).select("user_id"));
        var usernames = new LinkedList<String>();
        votedList.forEach(voted -> {
            var user = userMapper.selectOne(new QueryWrapper<User>().eq("id",voted.getUserId()).select("username"));
            usernames.add(user.getUsername());
        });

        return Result.success("",usernames);
    }

    @Override
    public Result canCurrentUserAddPost(){
        Long userId = MyUtil.getCurrentUserId();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("id",userId).select("mark"));
        if(user.getMark()>=MyConstant.newPostCostMark)
            return Result.success("",true);
        else return Result.success("",false);
    }

    //检查是否截止，如果返回null，表示还未截止，否则已截止并返回原因
    String checkFinished(Post post){

        //检查票数限制
        if(post.getMaxVoteCount()!=null&&post.getMaxVoteCount()>0){//大于零则限票

            if(post.getVoteCount()>=post.getMaxVoteCount())
                return "票数已用完，无法再投票";

            //已把已投票数暂存起来，避免每次查询耗费性能
//            List<Option> options=optionMapper.selectList(new QueryWrapper<Option>().eq("post_id",post.getId()));
//            int votedCount=0;
//            for(int i=0;i<options.size();++i){
//                votedCount+=options.get(i).getCount();
//            }
//            if(votedCount>=post.getMaxVoteCount())
//                return "票数已用完，无法再投票";
        }

        //检查时间限制
        if(post.getEndTime()!=null){//非null则限时
            if(new Date().after(post.getEndTime()))
                return "日期已截止，无法再投票";
        }

        return null;
    }

}
