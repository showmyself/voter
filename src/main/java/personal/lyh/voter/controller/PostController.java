package personal.lyh.voter.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import personal.lyh.voter.entity.Option;
import personal.lyh.voter.entity.Result;
import personal.lyh.voter.entity.dto.PostDto;
import personal.lyh.voter.other.MyValidator;
import personal.lyh.voter.service.IPostService;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/post")
public class PostController {

    //基本数据可以在controller层中校验，涉及重复等需要查询数据库的，则在service层中校验
    //key和maxVoteCount和endTime可以为空

    @Autowired
    IPostService postService;

    //若获取到Post则一并返回选项信息，由前端来决定是否显示结果

    //根据关键字查找，不能查找到含key的帖子。如果关键字为空或空串，那么自动搜索最近1周的帖子
    @GetMapping("findPosts")
    public Result findPosts(Integer currentPage, Integer pageSize, String keyword, String category, String timeOrHot){
        if(currentPage==null||pageSize==null)
            return Result.fail();
        if(timeOrHot==null)
            return Result.fail();

        return postService.findPosts(currentPage,pageSize,keyword,category,timeOrHot);
    }

    //查找当前用户发布的投票帖
    @GetMapping("findCurrentUserPosts")
    public Result findCurrentUserPosts(Integer currentPage, Integer pageSize){
        if(currentPage==null||pageSize==null)
            return Result.fail();

        return postService.findCurrentUserPosts(currentPage,pageSize);
    }

    //查找用户收藏过的帖子
    @GetMapping("findPostsInCollects")
    public Result findPostsInCollects(Integer currentPage, Integer pageSize){
        if(currentPage==null||pageSize==null)
            return Result.fail();

        return postService.findPostsInCollects(currentPage,pageSize);
    }

    //查找属于同一个系列（属于同一个用户）的所有帖子
    @GetMapping("findAllPostsInSameSeries")
    public Result findAllPostsInSameSeries(Long userId,String series){
        return postService.findAllPostsInSameSeries(userId,series);
    }

    //获取自己所有帖子的series集合
    @GetMapping("getAllSelfSeries")
    public Result getAllSelfSeries(){
        return postService.getAllSelfSeries();
    }

    //根据编号和秘钥，看是否能够获取目标帖子，返回值的flag为true时，会返回目标帖子的id
    @GetMapping("tryByNumber")
    public Result tryByNumber(String number,String key){

        if(!MyValidator.verifyString(number,1,16))
            return Result.fail();

        return postService.tryByNumber(number,key);
    }

    //根据id获取目标投票帖
    @GetMapping("getById")
    public Result getById(Long id,String key){
        if(id==null)
            return Result.fail();

        return postService.getById(id,key);
    }

    //添加投票帖
    @PostMapping("add")
    public Result add(@Valid @RequestBody PostDto postDto){

        if(postDto.getContent()!=null){
            if(postDto.getContent().split("\\R").length>3)
                return Result.fail();
        }

        if(postDto.getOptions().size()==0)return Result.fail();
        if(postDto.getOptions().size()>16)return Result.fail();
        for (Option option : postDto.getOptions())
        {
            if (StringUtils.isBlank(option.getDesc()))
                return Result.fail();
        }

        return postService.add(postDto);
    }

    //修改投票帖，为了信息准确，仅能修改部分信息
    @PostMapping("update")
    public Result update(@Valid @RequestBody PostDto postDto){

        return postService.update(postDto);
    }

    //删除投票帖
    @PostMapping("delete")
    public Result delete(Long id){
        if(id==null) return Result.fail();

        return postService.delete(id);
    }

    //是否已经投票，如果已经投了票，返回投过的选项名，没有则返回空串
    @GetMapping("hasVoted")
    public Result hasVoted(Long id){
        if(id==null) return Result.fail();

        return postService.hasVoted(id);
    }

    //投票
    @PostMapping("vote")
    public Result vote(Long id,Long optionId){
        if(id==null)return Result.fail();
        if(optionId==null)return Result.fail();

        return postService.vote(id,optionId);
    }

    //查看个人帖子的秘钥
    @GetMapping("getKey")
    public Result getKey(Long id){
        if(id==null)return Result.fail();

        return postService.getKey(id);
    }

    //获取一投票帖中，每个投票包含的用户信息（仅包含 年龄、性别）
    @GetMapping("getUserInfosOfVotesInPost")
    public Result getUserInfosOfVotesInPost(Long id){
        if(id==null)return Result.fail();

        return postService.getUserInfosOfVotesInPost(id);
    }

    //单独修改个人帖子的评论开闭
    @PostMapping("changeCommentAble")
    public Result changeCommentAble(Long id,Boolean isAble){
        if(id==null)return Result.fail();
        if(isAble==null)return Result.fail();

        return postService.changeCommentAble(id,isAble);
    }


    //上传picture图片，返回最终路径
    @PostMapping("uploadPicture")
    @ResponseBody
    public Result uploadPicture(MultipartFile file) throws IOException {
        return postService.uploadPicture(file);
    }

    //删除目标名字的picture图片
    @GetMapping("deletePicture")
    public Result deletePicture(String fileName){
        return postService.deletePicture(fileName);
    }

    //查看目标帖子中所有已投票的用户
    @GetMapping("getUsernamesOfPost")
    public Result getUsernamesOfPost(Long id){
        if(id==null)return Result.fail();

        return postService.getUsernamesOfPost(id);
    }

    //当前用户是否可以新增帖子（主要是判断积分）
    @GetMapping("canCurrentUserAddPost")
    public Result canCurrentUserAddPost(){
        return postService.canCurrentUserAddPost();
    }
}

