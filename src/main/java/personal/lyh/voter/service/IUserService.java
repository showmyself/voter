package personal.lyh.voter.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import personal.lyh.voter.entity.Result;
import personal.lyh.voter.entity.User;
import personal.lyh.voter.entity.dto.ForgetPwdDto;
import personal.lyh.voter.entity.dto.PasswordDto;
import personal.lyh.voter.entity.dto.RegisterDto;

import java.net.UnknownHostException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-25
 */
public interface IUserService extends UserDetailsService
{

    UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException;

    Result sendValidCodeToEmail(String usernameOrEmail);

    String verifyEmailValidCode(String email,String inputCode);

    Result register(RegisterDto registerDto) throws UnknownHostException;

    Result changeSelfInfo(User user);

    Result changePassword(PasswordDto passwordDto);

    Result forgetPassword(ForgetPwdDto forgetPwdDto);

    Result getSelf();

    Result getById(Long id);

    Result uploadAvatar(MultipartFile file) throws UnknownHostException;

    Result deleteAvatar(String avatarFileName);

    Result hasVoted(Long postId);

    Result hasCollected(Long postId);

    Result addCollect(Long postId);

    Result removeCollect(Long postId);


}
