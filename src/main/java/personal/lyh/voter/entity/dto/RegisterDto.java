package personal.lyh.voter.entity.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterDto
{
    @NotBlank
    @Size(min=3,max=20)
    private String username;

    @NotBlank
    @Size(min=4,max=36)
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(max=8)
    private String validCode;//这里是针对邮箱的验证码
}
