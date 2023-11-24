package personal.lyh.voter.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("collect")
public class Collect implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long postId;

    private Long userId;
}
