package personal.lyh.voter.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginDto
{
    @NotBlank
    @Size(min=3,max=36)
    private String usernameOrEmail;

    @NotBlank
    @Size(min=4,max=36)
    private String password;

    @NotBlank
    @Size(max=8)
    private String captcha;
}
