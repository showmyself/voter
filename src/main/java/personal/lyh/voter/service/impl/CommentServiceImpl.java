package personal.lyh.voter.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import personal.lyh.voter.entity.*;
import personal.lyh.voter.entity.dto.CommentDto;
import personal.lyh.voter.mapper.CommentMapper;
import personal.lyh.voter.mapper.LikedMapper;
import personal.lyh.voter.mapper.PostMapper;
import personal.lyh.voter.mapper.UserMapper;
import personal.lyh.voter.other.MyUtil;
import personal.lyh.voter.service.ICommentService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-25
 */
@Service
public class CommentServiceImpl implements ICommentService
{
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    LikedMapper likedMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PostMapper postMapper;
    @Autowired
    CacheManager cacheManager;

    @Override
    public Result findPostComments(Integer currentPage, Integer pageSize, Long postId, String timeOrHot)
    {
        Long userId = MyUtil.getCurrentUserId();

        //筛选目标页的第一层Comment
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        if (timeOrHot.equals("hot"))
            queryWrapper.orderByDesc("like_count");
        else queryWrapper.orderByDesc("remark_time");

        Page page = new Page(currentPage, pageSize);
        IPage pageData = commentMapper.selectPage(page, queryWrapper);

        List<Comment> comments = pageData.getRecords();

        //为每个Comment设置User、RepliedComment、liked
        for (Comment comment : comments)
            getLinkedDetail(comment, userId);

        return Result.success("", MapUtil.builder().put("comments", comments)
                .put("total", pageData.getTotal()).map());
    }

    @Override
    public Result findPresentUserComments(Integer currentPage, Integer pageSize)
    {
        //获取当前用户的id
        Long userId = MyUtil.getCurrentUserId();

        //先找到自身的所有评论id
        List<Comment> myComments = commentMapper.selectList(new QueryWrapper<Comment>()
                .eq("user_id", userId).select("id"));
        List<Long> myCommentIds = new ArrayList<>();
        for (Comment myComment : myComments)
            myCommentIds.add(myComment.getId());

        //再找到回复自己评论的所有评论的id
        List<Comment> otherComments = new ArrayList<>();
        if (myCommentIds.size() > 0)
            otherComments=commentMapper.selectList(new QueryWrapper<Comment>().in("reply_id", myCommentIds).select("id"));
        List<Long> otherCommentIds = new ArrayList<>();
            for (Comment otherComment : otherComments)
                otherCommentIds.add(otherComment.getId());

        //根据上文的所有id，按时间倒序查找出最终结果
        List<Long> commentIds = new ArrayList<>();
        for (Long id : myCommentIds)
            commentIds.add(id);
        for (Long id : otherCommentIds)
            commentIds.add(id);

        //个数为零时单独返回，不然报错
        if (commentIds.size() < 1)
        {
            return Result.success("", MapUtil.builder()
                    .put("comments", new ArrayList<Comment>())
                    .put("total", 0).map());
        }

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", commentIds).orderByDesc("remark_time");

        Page page = new Page(currentPage, pageSize);
        IPage pageData = commentMapper.selectPage(page, queryWrapper);

        List<Comment> comments = pageData.getRecords();

        //为每个Comment设置User、RepliedComment、liked
        for (Comment comment : comments)
            getLinkedDetail(comment, userId);

        //为个人页中的Comment设置所属Post的编号
        for (Comment comment : comments)
        {
            comment.setPostNumber(postMapper.selectOne(new QueryWrapper<Post>()
            .eq("id",comment.getPostId()).select("number")).getNumber());
        }

        return Result.success("", MapUtil.builder()
                .put("comments", comments)
                .put("total", pageData.getTotal()).map());
    }

    @Override
    @Transactional
    public Result add(CommentDto commentDto)
    {
        //获取当前用户的id
        Long userId = MyUtil.getCurrentUserId();

        Comment comment = new Comment();
        comment.setPostId(commentDto.getPostId());
        comment.setUserId(userId);
        comment.setRemark(commentDto.getRemark());
        comment.setRemarkTime(new Date());
        comment.setReplyId(null);
        comment.setLikeCount(0);

        try
        {
            commentMapper.insert(comment);
            return Result.success("", MapUtil.builder().put("comment", comment).map());
        }
        catch (Exception e)
        {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    @Override
    @Transactional
    public Result reply(CommentDto commentDto)
    {
        //获取当前用户的id
        Long userId = MyUtil.getCurrentUserId();

        Comment origin = commentMapper.selectOne(new QueryWrapper<Comment>()
                .eq("id", commentDto.getId()).select("post_id"));
        if(origin==null)return Result.fail("原评论不存在");

        Comment comment = new Comment();
        comment.setPostId(origin.getPostId());
        comment.setUserId(userId);
        comment.setRemark(commentDto.getRemark());
        comment.setRemarkTime(new Date());
        comment.setReplyId(commentDto.getId());
        comment.setLikeCount(0);

        try
        {
            commentMapper.insert(comment);
            return Result.success("", MapUtil.builder().put("comment", comment).map());
        } catch (Exception e)
        {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    @Override
    @Transactional
    public Result delete(Long id)
    {
        //先删除关联，再删除自身：
        try {
            likedMapper.delete(new QueryWrapper<Liked>().eq("comment_id", id));
            commentMapper.deleteById(id);
            return Result.success("", null);
        }
        catch (Exception e)
        {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    @Override
    @Transactional
    public Result like(Long id)
    {
        //获取当前用户的id
        Long userId = MyUtil.getCurrentUserId();

        //判断是否存在该id的评论
        Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>()
                .eq("id", id).select("id", "like_count"));
        if (comment == null)
            return Result.fail("点赞对象不存在");

        //判定是否已经点过赞了
        Liked liked = likedMapper.selectOne(new QueryWrapper<Liked>()
                .eq("comment_id", id).eq("user_id", userId));
        if (liked != null)
            return Result.fail("已经点过赞，不能再点赞");

        //没有点赞的话点赞
        liked = new Liked();
        liked.setCommentId(id);
        liked.setUserId(userId);

        try
        {
            likedMapper.insert(liked);
            commentMapper.update(null, new UpdateWrapper<Comment>()
                    .eq("id", id).set("like_count", comment.getLikeCount() + 1));
            return Result.success("", null);
        }
        catch (Exception e)
        {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    @Override
    @Transactional
    public Result cancelLike(Long id)
    {
        //获取当前用户的id
        Long userId = MyUtil.getCurrentUserId();

        //判断是否存在该id的评论
        Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>()
                .eq("id", id).select("id", "like_count"));
        if (comment == null)
            return Result.fail("点赞对象不存在");

        //判定是否已经点过赞了
        Liked liked = likedMapper.selectOne(new QueryWrapper<Liked>()
                .eq("comment_id", id).eq("user_id", userId));
        if (liked == null)
            return Result.fail("没有点过赞，不能取消点赞");

        //点了赞的话取消点赞
        try
        {
            likedMapper.delete(new QueryWrapper<Liked>()
                    .eq("comment_id", id).eq("user_id", userId));

            commentMapper.update(null, new UpdateWrapper<Comment>()
                    .eq("id", id).set("like_count", comment.getLikeCount() - 1));
            return Result.success("", null);
        }
        catch (Exception e)
        {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }


    //获得单条评论的联系属性user、repliedComment、liked
    public void getLinkedDetail(Comment comment, Long selfUserId)
    {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id", comment.getUserId()).select("id", "icon_src", "username"));
        comment.setUser(user);

        if (comment.getReplyId() != null)
        {
            Comment repliedComment = commentMapper.selectById(comment.getReplyId());
            comment.setRepliedComment(repliedComment);//可能为空

            if (repliedComment != null)
            {
                user = userMapper.selectOne(new QueryWrapper<User>()
                        .eq("id", repliedComment.getUserId()).select("id", "icon_src", "username"));

                repliedComment.setUser(user);

                if (selfUserId == null)
                    repliedComment.setLiked(false);
                else
                {
                    Liked liked = likedMapper.selectOne(new QueryWrapper<Liked>()
                            .eq("user_id", selfUserId).eq("comment_id", repliedComment.getId()));
                    if (liked == null)
                        repliedComment.setLiked(false);
                    else repliedComment.setLiked(true);
                }
            }
        }

        if (selfUserId == null)
            comment.setLiked(false);
        else
        {
            Liked liked = likedMapper.selectOne(new QueryWrapper<Liked>()
                    .eq("user_id", selfUserId).eq("comment_id", comment.getId()));
            if (liked == null)
                comment.setLiked(false);
            else comment.setLiked(true);
        }
    }

}
