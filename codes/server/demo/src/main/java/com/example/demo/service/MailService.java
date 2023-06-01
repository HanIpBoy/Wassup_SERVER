package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.UserEntity;
import com.example.demo.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
        message.setText("이때 모해? 계정 인증을 위해 아래의 인증 코드를 사용해주세요:\n\n인증 코드: " +userEntity.getEmailAuthCode()+" \n\n 감사합니다.");
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
