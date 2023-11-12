package CafeSystem.Cafe.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailsUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;


    public void sendSimpleMessage(String to, String subject, String body, List<String> list) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        simpleMailMessage.setCc(getCCList(list));

        javaMailSender.send(simpleMailMessage);
    }

    public String[] getCCList(List<String> list) {
        //converting to array of string , from list of string as per requirement by setCC method
        int n = list.size();
        String[] cclist = new String[n];
        for (int i = 0; i < n; i++) {
            cclist[i] = list.get(i);
        }
        return cclist;
    }

    public void forgotEmail(String to, String subject, String password) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(sender);
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlMessage = "String htmlMsg = \"<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> \" + to + \" <br><b>Password: </b> \" + password + \"<br><a href=\\\"http://localhost:4200/\\\">Click here to login</a></p>\";\n";

        message.setContent(htmlMessage, "text/html");
        javaMailSender.send(message);

    }
}
