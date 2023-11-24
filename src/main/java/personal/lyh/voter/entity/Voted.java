package personal.lyh.voter.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-08-25
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("voted")
public class Voted implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long postId;

    private Long optionId;

}
