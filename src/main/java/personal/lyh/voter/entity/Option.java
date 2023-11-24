package personal.lyh.voter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
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
@TableName("`option`")
public class Option implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    //所属post
    private Long postId;

    @TableField("`desc`")
    @Size(max=36)
    private String desc;

    private String picSrc;//图片链接

    private String link;//参考外链

      /**
     * 票数
     */
      private Integer count;//该选项已投票总数


}
