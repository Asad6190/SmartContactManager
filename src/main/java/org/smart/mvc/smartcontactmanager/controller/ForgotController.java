package org.smart.mvc.smartcontactmanager.controller;

import jakarta.servlet.http.HttpSession;
import org.smart.mvc.smartcontactmanager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotController {

    // We wanted to send OTP to Email Address but due to Gmail's new policy we can not access the less secure apps.

    Random random = new Random(1000);

    @Autowired
    private EmailService emailService;

    //email id form open handler

    @RequestMapping("/forgot")
    public String openEmailForm() {
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session) {
        System.out.println("Email: " + email);

        //generating OTP of 4 digits


        int otp = random.nextInt(99999);

        System.out.println("OTP: " + otp);

        //write code for send otp to emailgit
        String subject = "OTP From SCM";
        String message = "<h1> OTP = " + otp + " </h1> ";
        String to = email;


        boolean flag = this.emailService.sendEmail(subject, message, to);

        if (flag) {

            return "verify_otp";
        } else {

            session.setAttribute("message", "check your email id !!");
            return "forgot_email_form";
        }
    }
}
