package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.UserEntity;
import com.example.demo.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    public void send(UserEntity userEntity){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEntity.getUserId()); // To: 수신자
        message.setSubject("이때 모해? 이메일 계정 인증"); // Subject 제목
        message.setText("안녕하세요,\n" +
                "\n" +
                "계정 인증을 위해 아래의 인증 코드를 사용해주세요:\n" +
                "\n" +
                "==============================\n" +
                "        인증 코드: " + userEntity.getEmailAuthCode() + "\n" +
                "==============================\n" +
                "\n" +
                "계정 보안을 위해 인증 코드를 타인과 공유하지 마세요. 저희는 고객의 개인 정보를 중요하게 여기며, 인증 과정을 통해 안전한 서비스를 제공하고 있습니다.\n" +
                "\n" +
                "궁금한 사항이 있으시면 언제든지 문의해주세요.\n" +
                "\n" +
                "감사합니다.");
        mailSender.send(message);
    }



    public Boolean verifyEmailCode(UserEntity userEntity, String userVerificationCode) {
        if(userEntity.getEmailAuthCode().equals(userVerificationCode)) {
            return true;
        }
        else {
            userRepository.delete(userEntity);
            return false;
        }
    }
}
