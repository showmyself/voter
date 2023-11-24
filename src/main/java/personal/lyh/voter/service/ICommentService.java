package personal.lyh.voter.service;

import personal.lyh.voter.entity.Result;
import personal.lyh.voter.entity.dto.CommentDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-25
 */
public interface ICommentService {

    Result findPostComments(Integer currentPage, Integer pageSize,Long postId,  String timeOrHot);

    Result findPresentUserComments(Integer currentPage, Integer pageSize);

    Result add(CommentDto commentDto);

    Result reply(CommentDto commentDto);

    Result delete(Long id);

    Result like(Long id);

    Result cancelLike(Long id);
}
