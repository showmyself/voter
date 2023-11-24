package personal.lyh.voter.other;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

//https://blog.csdn.net/qq_37269881/article/details/120065322?spm=1001.2101.3001.6650.2&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-2.pc_relevant_default&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-2.pc_relevant_default&utm_relevant_index=4

@Component
public class MailTool
{
    //使用邮箱会导致项目启动很慢，所以开发时临时关闭邮箱，做如下操作
    //。yml中注释掉邮箱相关配置
    //。下文注释掉JavaMailSender和@Value，开启FakeMailSender
    //。verifyEmailValidCode方法中直接返回null

//    @Autowired
//    private JavaMailSender mailSender;

    //发送方邮箱
//    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private FakeMailSender mailSender;

    public void SendText(String title,String content,String[] sendTos){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(sendTos);
        message.setSubject(title);
        message.setText(content);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    public void sendHtml(String title,String htmlText,String[] sendTos) throws MessagingException
    {
        //创建一个MINE消息
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mineHelper = new MimeMessageHelper(message, true);

            mineHelper.setFrom(from);
            mineHelper.setTo(sendTos);
            mineHelper.setSubject(title);

            //邮件内容   true 表示带有附件或html
            mineHelper.setText(htmlText, true);
            try {
                mailSender.send(message);
            } catch (MailException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
