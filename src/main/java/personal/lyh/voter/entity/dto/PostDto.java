package personal.lyh.voter.entity.dto;

import lombok.Data;
import personal.lyh.voter.entity.Option;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PostDto
{
    /*
- `@NotNull` 不能为null
- `@NotEmpty` 不能为null、空字符串、空集合
- `@NotBlank` 不能为null、空字符串、纯空格的字符串
- `@Min` 数字最小值不能小于x
- `@Max` 数字最大值不能大于x
- `@Email` 字符串为邮件格式
- `@Max` 数字最大值不能大于x
- `@Size` 字符串长度最小为x、集合长度最小为x
- `@Pattern` 正则表达式
*/

    private Long id;//如果是编辑，则有id

    @NotBlank
    @Size(min=1,max=30)
    private String title;

    @Size(max=256)
    private String content;//说明内容

    @Size(max=1024)
    private String picSrc;//可配一图片

    @Size(max=20)
    private String category;//类别

    @NotBlank
    @Size(max=16)
    private String number;//编号

    private String key;//秘钥

    private String endTime;

    @NotNull
    private Boolean commentAble;//是否允许评论

    public Integer maxVoteCount;

    public String series;

    private List<Option> options;
}
