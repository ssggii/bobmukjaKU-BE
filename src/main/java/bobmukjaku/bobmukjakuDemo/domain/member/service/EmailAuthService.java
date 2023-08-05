package bobmukjaku.bobmukjakuDemo.domain.member.service;

import bobmukjaku.bobmukjakuDemo.domain.member.dto.HashedAuthCodeDto;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final JavaMailSender mailSender;

    //메일로 보낼 메시지 생성
    private MimeMessage createMessage(String code, String email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("인증코드메일입니다", "UTF-8");
        message.setText("이메일 인증코드 " + code, "UTF-8");
        message.setFrom(new InternetAddress("bobmukjaku@naver.com"));

        return message;
    }

    //메일전송
    @Async
    public void sendMail(String code, String email) throws Exception{

        try{
            MimeMessage mimeMessage = createMessage(code, email);
            mailSender.send(mimeMessage);
            System.out.println("send email to " + email);
        }catch (MailException mailException){
            mailException.printStackTrace();
            System.out.println("유효하지 않은 이메일");
            throw new IllegalAccessException();
        }
    }

    //메일로 보낼 인증코드 생성
    public String createAuthCode(){

        Random r = new Random();
        String authCode = "";
        r.setSeed(System.currentTimeMillis());

        //랜덤한 6자리 정수로 이루어진 인증코드 생성
        for(int i = 0; i < 6; i++){
            authCode = authCode.concat(String.valueOf(r.nextInt(10)));//0~9범위의 랜덤한 숫자하나를 생성하여 authCode에 붙인다.
        }
        System.out.println(authCode);

        return authCode;
    }

    //인증코드 해시값 반환
    public HashedAuthCodeDto hashAuthCode(String authCode) throws NoSuchAlgorithmException {
        return new HashedAuthCodeDto(encrypt(authCode));
    }

    //해시함수
    public String encrypt(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());

        return bytesToHex(md.digest());
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}
