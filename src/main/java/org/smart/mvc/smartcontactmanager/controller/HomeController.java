package org.smart.mvc.smartcontactmanager.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.smart.mvc.smartcontactmanager.dao.UserRepository;
import org.smart.mvc.smartcontactmanager.entities.User;
import org.smart.mvc.smartcontactmanager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

   /* Yeh sb Mapping kay liye use kiye gaye hain aur akhir main
    unka HTML page return kar dia gaya hai  */

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

//handler for registering user

    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session) {

        try {
            if (!agreement) {
                System.out.println("You have not agreed the terms and condition");
                throw new Exception("You have not agreed the terms and condition");
            }

            if (result1.hasErrors()){
                System.out.println("ERROR "+result1.toString());
                model.addAttribute("user",user);
                return "singup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Aggrement" + agreement);
            System.out.println("User" + user);

            User result = this.userRepository.save(user);

            model.addAttribute("user", new User());

            session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));

            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something Went Wrong !!" + e.getMessage(), "alert-danger"));
            return "signup";
        }
    }

    //handler for custom login

    @GetMapping("/signin")
    public String customLogin(Model model) {
        model.addAttribute("title", "Login Page");
        return "login";
    }
}
