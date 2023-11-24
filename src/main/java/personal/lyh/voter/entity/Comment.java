package personal.lyh.voter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

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
    @TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long postId;

    @TableField("`remark`")
    private String remark;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date remarkTime;

    private Integer likeCount;

    private Long replyId;

    @TableField(exist = false)
    private  User user;//该评论的发出者

    @TableField(exist = false)
    private  Comment repliedComment;//该评论回复的评论，仅递归一次

    @TableField(exist = false)
    private  boolean liked;//当前用户是否已经对该评论点过赞

    @TableField(exist = false)
    private  String postNumber;//所属post的编号，在个人历史评论中有用到
}
