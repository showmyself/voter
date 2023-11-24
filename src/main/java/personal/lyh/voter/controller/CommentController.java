package personal.lyh.voter.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import personal.lyh.voter.entity.Result;
import personal.lyh.voter.entity.dto.CommentDto;
import personal.lyh.voter.service.ICommentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/comment")
@JsonInclude(JsonInclude.Include.NON_NULL)//过滤返回json中的空字段，另外配置spring.jackson.default-property-inclusion: non_null
public class CommentController {

    //基本数据可以在controller层中校验，涉及重复等需要查询数据库的，则在service层中校验

    @Autowired
    ICommentService commentService;

    //查看评论，顺便也取得被回复的评论
    @GetMapping("findPostComments")
    public Result findPostComments(Integer currentPage, Integer pageSize, Long postId, String timeOrHot){
        if(currentPage==null)return Result.fail();
        if(pageSize==null)return Result.fail();
        if(postId==null)return Result.fail();
        if(timeOrHot==null)return Result.fail();

        return commentService.findPostComments(currentPage, pageSize,postId, timeOrHot);
    }

    //查找当前用户的评论
    @GetMapping("findPresentUserComments")
    public Result findPresentUserComments(Integer currentPage, Integer pageSize){
        if(currentPage==null)return Result.fail();
        if(pageSize==null)return Result.fail();

        return commentService.findPresentUserComments(currentPage, pageSize);
    }

    //新增评论
    @PostMapping("add")
    public Result add(@Valid @RequestBody CommentDto commentDto){
        if(commentDto.getRemark().split("\\\\n").length>3)return Result.fail();

        return commentService.add(commentDto);
    }

    //回复评论
    @PostMapping("reply")
    public Result reply(@Valid @RequestBody CommentDto commentDto){
        if(commentDto.getRemark().split("\\\\n").length>3)return Result.fail();

        return commentService.reply(commentDto);
    }

    //评论无修改，复制后删除新建可代替修改

    //删除评论
    @PostMapping("delete")
    public Result delete(Long id){
        if(id==null)return Result.fail();

        return commentService.delete(id);
    }

    //点赞评论
    @PostMapping("like")
    public Result like(Long id){
        if(id==null)return Result.fail();

        return commentService.like(id);
    }

    //取消点赞
    @PostMapping("cancelLike")
    public Result cancelLike(Long id){
        if(id==null)return Result.fail();

        return commentService.cancelLike(id);
    }

}

