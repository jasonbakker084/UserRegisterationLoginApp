package com.example.demo.controller;

import com.example.demo.model.ConfirmationToken;
import com.example.demo.model.User;
import com.example.demo.repository.ConfirmationTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//Aparte controller voor het beheren van useraccounts (wachtwoord reset end dergelijke)
@Controller
@RequestMapping
public class UserAccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @RequestMapping(value = "/register2", method = RequestMethod.GET)
    public ModelAndView displayRegistration(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/register2", method = RequestMethod.POST)
    public ModelAndView registerUser(ModelAndView modelAndView, User user) {

        User existingUser = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (existingUser != null) {
            modelAndView.addObject("message", "This email already exists!");
            modelAndView.setViewName("error");
        } else {
            userRepository.save(user);

            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            confirmationTokenRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("flavourwheelsapp@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    + "http://localhost:8080/confirm-reset?token=" + confirmationToken.getConfirmationToken());

            emailSenderService.sendEmail(mailMessage);

            modelAndView.addObject("emailId", user.getEmail());

            modelAndView.setViewName("successfulRegisteration");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
            user.setEnabled(true);
            userRepository.save(user);
            modelAndView.setViewName("accountVerified");
        } else {
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ConfirmationTokenRepository getConfirmationTokenRepository() {
        return confirmationTokenRepository;
    }

    public void setConfirmationTokenRepository(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public EmailSenderService getEmailSenderService() {
        return emailSenderService;
    }

    public void setEmailSenderService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    // Display the form
    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("forgotPassword");
        return modelAndView;
    }

    // Receive the address and send an email
    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public ModelAndView forgotUserPassword(ModelAndView modelAndView, User user) {
        User existingUser = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (existingUser != null) {
            // Create token
            ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);

            // Save it
            confirmationTokenRepository.save(confirmationToken);

            // Create the email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(existingUser.getEmail());
            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setFrom("flavourwheelsapp@gmail.com");
            mailMessage.setText("To complete the password reset process, please click here: "
                    + "http://localhost:8080/confirm-reset?token=" + confirmationToken.getConfirmationToken());

            // Send the email
            emailSenderService.sendEmail(mailMessage);

            modelAndView.addObject("message", "Request to reset password received. Check your inbox for the reset link.");
            modelAndView.setViewName("successForgotPassword");

        } else {
            modelAndView.addObject("message", "This email address does not exist!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // Endpoint to confirm the token
    @RequestMapping(value = "/confirm-reset", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
            user.setEnabled(true);
            userRepository.save(user);
            modelAndView.addObject("user", user);
            modelAndView.addObject("email", user.getEmail());
            modelAndView.setViewName("resetPassword");
        } else {
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // Endpoint to update a user's password
    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {
        if (user.getEmail() != null) {
            // Use email to find user
            User tokenUser = userRepository.findByEmailIgnoreCase(user.getEmail());
            tokenUser.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(tokenUser);
            modelAndView.addObject("message", "Password successfully reset. You can now log in with the new credentials.");
            modelAndView.setViewName("successResetPassword");
        } else {
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
}