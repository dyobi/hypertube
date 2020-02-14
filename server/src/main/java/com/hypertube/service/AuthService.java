package com.hypertube.service;

import com.hypertube.model.Response;
import com.hypertube.model.User;
import com.hypertube.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    JavaMailSender emailSender;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public Response signUp(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return new Response(200);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(400);
        }
    }

    public Response signIn(User user) {
        try {
            User valid = userRepository.findByUserName(user.getUserName());
            if (valid == null) return new Response(411);
            else
                return passwordEncoder.matches(user.getPassword(), valid.getPassword()) ?
                        new Response(200, tokenService.createToken(valid)) : new Response(412);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(400);
        }
    }

    public Response oAuth(User user) {
        try {
            User valid = userRepository.findByEmail(user.getEmail());
            if (valid == null) userRepository.save(user);
            else if (valid.getSocialType().equals(user.getSocialType())) return new Response(400);
            return new Response(200, tokenService.createToken(userRepository.findByEmail(user.getEmail())));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(400);
        }
    }

    public Response getUserName(String userName) {
        try {
            User valid = userRepository.findByUserName(userName);
            return valid == null ? new Response(200) : new Response(400);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(400);
        }
    }

    public Response getEmail(String email) {
        try {
            User valid = userRepository.findByEmail(email);
            return valid == null ? new Response(200) : new Response(400);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(400);
        }
    }

    public Response recovery(String email) {
        try {
            if (userRepository.findByEmail(email) == null) return new Response(400);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("subject");
            message.setText("text");
            emailSender.send(message);
            return new Response(200);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(400);
        }
    }

}