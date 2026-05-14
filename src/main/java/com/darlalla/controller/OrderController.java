package com.darlalla.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.darlalla.entity.User;
import com.darlalla.repository.UserRepository;
import com.darlalla.service.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final UserRepository userRepository;
    private final OrderService orderService;

    public OrderController(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    private User getUserFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @GetMapping
    public String myOrders(Model model, Authentication auth) {
        User user = getUserFromAuth(auth);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderService.getOrdersByUser(user));
        return "orders";
    }

    @GetMapping("/confirmation/{id}")
    public String confirmation(@PathVariable Long id, Model model, Authentication auth) {
        User user = getUserFromAuth(auth);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("orderId", id);
        return "order-confirmation";
    }
}