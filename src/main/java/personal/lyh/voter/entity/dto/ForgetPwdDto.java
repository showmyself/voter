package personal.lyh.voter.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ForgetPwdDto
{
    @NotBlank
    @Size(min=3,max=36)
    private String usernameOrEmail;

    @NotBlank
    @Size(min=4,max=36)
    private String newPassword;

    @NotBlank
    @Size(max=8)
    private String validCode;
}
