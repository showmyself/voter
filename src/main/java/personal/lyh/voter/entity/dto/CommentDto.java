package personal.lyh.voter.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
public class CommentDto
{
    private Long id;//要回复的评论的id

    @NotNull
    private Long postId;

    @NotBlank
    @Size(min=3,max=256)
    public String remark;
}
