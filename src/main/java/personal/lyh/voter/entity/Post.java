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
import java.util.List;

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
@TableName("post")
public class Post implements Serializable
{

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    //为了简化，暂时不设置类别，通过标题和内容搜索足够

    private String title;

    private String content;//说明内容

    private String picSrc;//配参考图

    private String category;//类别

    private String number;//编号，可自定义，为空则无法根据编号进入

    @TableField("`key`")
    private String key;//秘钥

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private Long viewCount;//查看次数记录

    private Long userId;

    private Boolean commentAble;//是否允许评论

    private Integer maxVoteCount;

    private Integer voteCount;

    private String series;

    @TableField(exist = false)
    private List<Option> options;
    @TableField(exist = false)
    private Boolean finished;//是否已截止
    @TableField(exist = false)
    private Integer commentCount;//评论数量


}
