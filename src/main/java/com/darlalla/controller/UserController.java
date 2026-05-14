package com.darlalla.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.darlalla.dto.UserRegistrationDTO;
import com.darlalla.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegistrationDTO user, BindingResult result) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Cet email est déjà utilisé");
            return "register";
        }

        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}