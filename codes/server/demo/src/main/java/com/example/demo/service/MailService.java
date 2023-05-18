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
        message.setSubject("Wscalendar 계정 이메일 주소 인증"); // Subject 제목
        message.setText(userEntity.getEmailAuthCode()); // Text: 본문

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
