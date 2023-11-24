package personal.lyh.voter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import personal.lyh.voter.entity.Result;
import personal.lyh.voter.entity.User;
import personal.lyh.voter.entity.dto.ForgetPwdDto;
import personal.lyh.voter.entity.dto.PasswordDto;
import personal.lyh.voter.entity.dto.RegisterDto;
import personal.lyh.voter.other.MyUtil;
import personal.lyh.voter.other.MyValidator;
import personal.lyh.voter.service.IUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.UnknownHostException;

//@CrossOrigin(value = "http://localhost:8080",allowedHeaders ="*")//没用了
@RestController
@RequestMapping("/user")
public class UserController {

    //基本数据可以在controller层中校验，涉及重复等需要查询数据库的，则在service层中校验

    @Autowired
    IUserService userService;

    //测试
    @GetMapping("test")
    public Result test(){
        return Result.success("成功",null);
    }

    //发送注册验证码
    @GetMapping("sendValidCodeToEmail")
    public Result sendValidCodeToEmail(@RequestParam String usernameOrEmail){
        if (MyValidator.verifyString(usernameOrEmail))
            return userService.sendValidCodeToEmail(usernameOrEmail);

        return Result.fail();
    }

    //获取登录验证码
    @GetMapping("getGraphicCaptcha")
    public void getGraphicCaptcha(HttpServletResponse response, HttpServletRequest request, HttpSession session)
    {
        MyUtil.returnGraphicCaptcha(response, request, session);
    }

    //注册
    @PostMapping("register")
    public Result register(@Valid @RequestBody RegisterDto registerDto) throws UnknownHostException {
        return userService.register(registerDto);
    }

    //修改密码
    @PostMapping("changePassword")
    public Result changePassword(@Valid @RequestBody PasswordDto passwordDto){
        return userService.changePassword(passwordDto);
    }

    //因忘记密码，而需要重置密码
    @PostMapping("forgetPassword")
    public Result forgetPassword(@Valid @RequestBody ForgetPwdDto forgetPwdDto){
        return userService.forgetPassword(forgetPwdDto);
    }

    //修改用户信息
    @PostMapping("changeSelfInfo")
    public Result changeSelfInfo(@RequestBody User user){

        if(MyValidator.verifyString(user.getUsername(),3,20))
            return userService.changeSelfInfo(user);

        return Result.fail();
    }

    @GetMapping("getSelf")
    public Result getSelf(){
        return userService.getSelf();
    }

    //文件上传，返回最终路径
    @PostMapping("/uploadAvatar")
    @ResponseBody
    public Result uploadAvatar(MultipartFile file) throws IOException {
        return userService.uploadAvatar(file);
    }

    @GetMapping("/deleteAvatar")
    public Result deleteAvatar(String fileName){
        return userService.deleteAvatar(fileName);
    }

    //是否已经对某个投票帖投过票
    @GetMapping("hasVoted")
    public Result hasVoted(Long postId) {
        if (postId == null)
            return Result.fail();

        return userService.hasVoted(postId);
    }

    @GetMapping("getById")
    public Result getById(Long id){
        if(id==null)
            return Result.fail();

        return userService.getById(id);
    }

    @GetMapping("hasCollected")
    public Result hasCollected(Long postId){
        if(postId==null)
            return Result.fail();

        return userService.hasCollected(postId);
    }
    @PostMapping("addCollect")
    public Result addCollect(Long postId){
        if(postId==null)
            return Result.fail();

        return userService.addCollect(postId);
    }
    @PostMapping("removeCollect")
    public Result removeCollect(Long postId){
        if(postId==null)
            return Result.fail();

        return userService.removeCollect(postId);
    }
}

