package personal.lyh.voter.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import personal.lyh.voter.auth.JwtUtil;
import personal.lyh.voter.entity.*;
import personal.lyh.voter.entity.dto.ForgetPwdDto;
import personal.lyh.voter.entity.dto.PasswordDto;
import personal.lyh.voter.entity.dto.RegisterDto;
import personal.lyh.voter.mapper.CollectMapper;
import personal.lyh.voter.mapper.PostMapper;
import personal.lyh.voter.mapper.UserMapper;
import personal.lyh.voter.mapper.VotedMapper;
import personal.lyh.voter.other.MailTool;
import personal.lyh.voter.other.MyConstant;
import personal.lyh.voter.other.MyUtil;
import personal.lyh.voter.other.UploadUtil;
import personal.lyh.voter.service.IUserService;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-25
 */
@Service
public class UserServiceImpl implements IUserService
{
    @Autowired
    UserMapper userMapper;
    @Autowired
    PostMapper postMapper;
    @Autowired
    VotedMapper votedMapper;
    @Autowired
    CollectMapper collectMapper;
    @Autowired
    MailTool mailTool;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    UploadUtil uploadUtil;
    @Value("${server.port}")
    String port;

    //实现springSecurity提供的接口，根据用户名获取用户信息，需要从数据库查表
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException
    {
        if(usernameOrEmail==null) return null;

        User userDetails;
        if(MyUtil.isEmail(usernameOrEmail))
            userDetails = userMapper.selectOne(new QueryWrapper<User>().eq("email", usernameOrEmail));
        else
            userDetails = userMapper.selectOne(new QueryWrapper<User>().eq("username", usernameOrEmail));

        return userDetails;
    }

    //注册或忘密码时通用的邮箱验证
    @Transactional
    public Result sendValidCodeToEmail(String usernameOrEmail)
    {
        String targetEmail;

        if(MyUtil.isEmail(usernameOrEmail))
            targetEmail=usernameOrEmail;
        else{
            User user= userMapper.selectOne(new QueryWrapper<User>().eq("username",usernameOrEmail).select("email"));
            if(user==null)return Result.fail("不存在该用户");
            targetEmail=user.getEmail();
        }

        String validCode = MyUtil.generateValidCode();

        try{
            //以最新验证码为准
            cacheManager.getCache(MyConstant.CacheType_EmailCode).put(targetEmail,validCode);
            System.out.println("邮箱验证码："+validCode);
            mailTool.SendText("【Voter】 Confirm Your Valid Code",validCode+"  （1分钟内有效）",new String[]{targetEmail});
        }catch (Exception e) {
            e.printStackTrace();
            return Result.fail("发送时发生异常");
        }

        return Result.success("已成功发送",null);
    }

    //检查邮箱验证码
    public String verifyEmailValidCode(String email,String inputCode){
//        暂时屏蔽邮箱验证
        return null;

//        var cache=cacheManager.getCache(MyConstant.CacheType_EmailCode).get(email);
//        if(cache==null)
//            return "未检测到有效的验证码，需重新发送";
//
//        String correctValidCode=(String)cache.get();
//        if(!inputCode.equals(correctValidCode))
//            return "验证码不正确";
//
//        return null;
    }

    @Transactional
    public Result register(RegisterDto registerDto) throws UnknownHostException {
        User user=userMapper.selectOne(new QueryWrapper<User>()
                .eq("username",registerDto.getUsername()).select("id"));
        if(user!=null){
            return Result.fail("此用户名已被占用");
        }

        user=userMapper.selectOne(new QueryWrapper<User>()
                .eq("email",registerDto.getEmail()).select("id"));
        if(user!=null){
            return Result.fail("此邮箱已被占用");
        }

        String reason=verifyEmailValidCode(registerDto.getEmail(),registerDto.getValidCode());
        if(reason!=null)return Result.fail(reason);

        user=new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(MyUtil.encrypt(registerDto.getPassword(), MyConstant.passwordSalt));
        user.setEmail(registerDto.getEmail());

        user.setIconSrc("http://" + MyUtil.getIpAndPort() +"/static/avatars/" + MyConstant.defaultIconName);
        user.setMark(MyConstant.registerAddMark);
        user.setCreateTime(new Date());

        try{
            userMapper.insert(user);
            user.setPassword("");
            String jwtToken= JwtUtil.generateJwt(user);
            return Result.success("注册成功", MapUtil.builder()
                    .put("user",user)
                    .put("token",jwtToken)
                    .map());
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            throw new ServiceException(ServiceExceptionType.SqlFail);
            return Result.fail("发生异常");
        }
    }

    public Result changeSelfInfo(User user)
    {
        Long userId = MyUtil.getCurrentUserId();

        //可修改的为 头像链接、用户名、性别、年龄，其中用户名不能重复
        User same=userMapper.selectOne(new QueryWrapper<User>().eq("username",user.getUsername()).select("id"));

        if(same!=null&&!same.getId().equals(userId)) {
            return Result.fail("用户名已被占用，修改失败");
        }
        if(!userId.equals(user.getId())){
            return Result.fail("无权修改他人的用户名");
        }

        try{
            userMapper.update(null,new UpdateWrapper<User>()
                    .eq("id",userId)
                    .set("username",user.getUsername())
                    .set("sex",user.getSex())
                    .set("age",user.getAge())
                    .set("icon_src",user.getIconSrc()));

            var caches=cacheManager.getCache(MyConstant.CacheType_EmailCode);
            caches.evictIfPresent(userId);

            return Result.success("",null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    @Transactional
    public Result changePassword(PasswordDto passwordDto)
    {
        //先检查登录
        Long userId = MyUtil.getCurrentUserId();

        //修改密码不需要邮箱验证，只有忘记密码才需要。即使密码被别人改了，自己也能通过邮箱找回

        User user=userMapper.selectOne(new QueryWrapper<User>()
                .eq("id",userId).select("username","password"));
        if(user==null) return Result.fail("用户不存在");

        String oldPwd=MyUtil.encrypt(passwordDto.getOldPassword(),MyConstant.passwordSalt);
        String originPwd=user.getPassword();

        if(!oldPwd.equals(originPwd))
            return Result.fail("原密码不正确");

        try{
            userMapper.update(null,new UpdateWrapper<User>().eq("id",userId)
                    .set("password",MyUtil.encrypt(passwordDto.getNewPassword(),MyConstant.passwordSalt)));

            return Result.success("",null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    @Transactional
    public Result forgetPassword(ForgetPwdDto forgetPwdDto)
    {
        User user;

        if(MyUtil.isEmail(forgetPwdDto.getUsernameOrEmail())){
            user=userMapper.selectOne(new QueryWrapper<User>().eq("email",forgetPwdDto.getUsernameOrEmail()).select("id","email"));
        }else{
            user=userMapper.selectOne(new QueryWrapper<User>().eq("username",forgetPwdDto.getUsernameOrEmail()).select("id","email"));
        }

        if(user==null)
            return Result.fail("目标用户不存在");

        String reason=verifyEmailValidCode(user.getEmail(),forgetPwdDto.getValidCode());
        if(reason!=null)return Result.fail(reason);

        try{
            userMapper.update(null,new UpdateWrapper<User>().eq("id",user.getId())
                    .set("password",MyUtil.encrypt(forgetPwdDto.getNewPassword(),MyConstant.passwordSalt)));

            user=userMapper.selectById(user.getId());
            user.setPassword("");
            String jwtToken= JwtUtil.generateJwt(user);
            return Result.success("重置成功", MapUtil.builder()
                    .put("user",user)
                    .put("token",jwtToken)
                    .map());

        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    public Result getSelf()
    {
        Long userId = MyUtil.getCurrentUserId();

        return getById(userId);
    }

    @Override
    public Result getById(Long id){

        var caches=cacheManager.getCache(MyConstant.CacheType_EmailCode);
        var cache = caches.get(id);
        if (cache != null) return (Result) cache.get();

        User user=userMapper.selectById(id);
        if(user==null)return Result.fail("用户不存在");
        user.setPassword(null);

        var result = Result.success("",user);
        caches.put(id,result);

        return result;
    }

    @Override
    public Result uploadAvatar(MultipartFile file) throws UnknownHostException {

        if(!MyUtil.checkFileSize(file.getSize(),512,"K")){
            return Result.fail("不支持上传 大小超过512KB的图片");
        }

        return uploadUtil.uploadFile(file, "avatars");

    }

    @Override
    public Result deleteAvatar(String avatarFileName) {
        return uploadUtil.deleteFile(avatarFileName,"avatars");
    }

    @Override
    public Result hasVoted(Long postId)
    {
        Long userId = MyUtil.getCurrentUserId();

        Voted voted = votedMapper.selectOne(new QueryWrapper<Voted>()
                .eq("user_id", userId).eq("post_id", postId));

        return Result.success("", MapUtil.builder().put("hasVoted", voted != null).map());
    }

    @Override
    public Result hasCollected(Long postId)
    {
        Long userId = MyUtil.getCurrentUserId();

        Collect collect=collectMapper.selectOne(new QueryWrapper<Collect>()
        .eq("post_id",postId).eq("user_id",userId));

        return Result.success("",collect!=null);
    }

    @Override
    @Transactional
    public Result addCollect(Long postId){

        Long userId = MyUtil.getCurrentUserId();

        Post targetPost = postMapper.selectOne(new QueryWrapper<Post>().eq("id",postId).select("id"));
        if(targetPost==null)
            return Result.fail("帖子不存在");

        Collect collect=collectMapper.selectOne(new QueryWrapper<Collect>()
                .eq("post_id",postId).eq("user_id",userId));

        if(collect!=null) {
            return Result.fail("不能重复收藏");
        }

        collect=new Collect();
        collect.setUserId(userId);
        collect.setPostId(postId);

        try {
            collectMapper.insert(collect);
            return Result.success("",null);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    @Override
    @Transactional
    public Result removeCollect(Long postId){
        Long userId = MyUtil.getCurrentUserId();

        try
        {
            collectMapper.delete(new QueryWrapper<Collect>()
                    .eq("post_id",postId)
                    .eq("user_id",userId));

            return Result.success("",null);
        }
        catch (Exception e)
        {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("发生异常");
        }
    }

    /*===============================================================*/

}
