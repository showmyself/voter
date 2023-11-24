package personal.lyh.voter.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PasswordDto
{
    @NotBlank
    @Size(min=4,max=36)
    private String oldPassword;

    @NotBlank
    @Size(min=4,max=36)
    private String newPassword;
}
