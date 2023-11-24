package personal.lyh.voter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("personal.lyh.voter.mapper")
@SpringBootApplication
public class VoterApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(VoterApplication.class, args);
    }
}
