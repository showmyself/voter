package personal.lyh.voter.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import personal.lyh.voter.entity.User;
import personal.lyh.voter.other.MyValidator;

import java.util.Date;

//个人使用jwt来装userId，避免每次查找费时
public class JwtUtil
{
    private static final String SUBJECT = "voter";
    //有效时长：
    private static final long EFFECTIVE_TIME = 1000 * 24 * 60 * 60 * 7;//此为一周有效时间
    //签名秘钥：
    private static final String SECRET_KEY = "voter";//注意不能少于4个字符

    //根据user生成jwt串
    public static String generateJwt(User user) {

        String token = Jwts
                .builder()
                .setSubject(SUBJECT)
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EFFECTIVE_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();

        return token;
    }

    //根据jwt串获取用户id
    public static String getUserId(String jwt) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwt).getBody();
        return claims.get("userId").toString();
    }

    //检查是否为合法的jwt串，包括过期检测
    public static boolean verify(String jwt)
    {
        if(!MyValidator.verifyString(jwt))
            return false;
        try
        {
            //先检查合法性，可能抛出相关异常
            Jws jws = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwt);
            //检查过期
            Claims claims = (Claims) (jws.getBody());
            return !claims.getExpiration().before(new Date());//此处取反才正确
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
