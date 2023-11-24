package personal.lyh.voter.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @TableName("liked")
public class Liked implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long commentId;

    private Long userId;


}
