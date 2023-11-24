package personal.lyh.voter.other;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class FakeMailSender {

    public void send(SimpleMailMessage simpleMessage) throws MailException{

    }

    public void send(MimeMessage mimeMessage) throws MailException{

    }

    public MimeMessage createMimeMessage(){
        return null;
    }
}
