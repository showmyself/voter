package personal.lyh.voter.service;

import org.springframework.web.multipart.MultipartFile;
import personal.lyh.voter.entity.Result;
import personal.lyh.voter.entity.dto.PostDto;

import java.net.UnknownHostException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-25
 */
public interface IPostService {

    Result findPosts(Integer currentPage, Integer pageSize,
                     String keyword, String category, String timeOrHot);

    Result findCurrentUserPosts(Integer currentPage, Integer pageSize);

    Result findPostsInCollects(Integer currentPage, Integer pageSize);

    Result findAllPostsInSameSeries(Long userId,String series);

    Result getAllSelfSeries();

    Result tryByNumber(String number,String key);

    Result getById(Long id,String key);

    Result add(PostDto postDto);

    Result update(PostDto postDto);

    Result delete(Long id);

    Result hasVoted(Long id);

    Result vote(Long id,Long optionId);

    Result getKey(Long id);

    Result changeCommentAble(Long id,boolean isAble);

    Result getUserInfosOfVotesInPost(Long id);

    Result uploadPicture(MultipartFile file) throws UnknownHostException;

    Result deletePicture(String avatarFileName);

    Result getUsernamesOfPost(Long id);

    Result canCurrentUserAddPost();
}
