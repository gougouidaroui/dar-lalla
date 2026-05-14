package com.darlalla.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.darlalla.dto.CheckoutDTO;
import com.darlalla.entity.User;
import com.darlalla.repository.UserRepository;
import com.darlalla.service.CartService;
import com.darlalla.service.OrderService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public CheckoutController(CartService cartService, OrderService orderService, UserRepository userRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private User getUserFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @GetMapping
    public String showCheckout(Model model, Authentication auth) {
        try {
            User user = getUserFromAuth(auth);
            if (user == null) {
                return "redirect:/login?redirect=/checkout";
            }

            var cart = cartService.getCartForUser(user);
            logger.info("Cart for user {}: cart={}, items={}", user.getId(), cart != null, cart != null ? cart.getItems() : "null");

            boolean isEmpty = cart == null || cart.getItems() == null || cart.getItems().isEmpty();
            logger.info("Cart is empty: {}", isEmpty);

            model.addAttribute("emptyCart", isEmpty);

            if (isEmpty) {
                return "checkout";
            }

            model.addAttribute("cart", cart);
            model.addAttribute("checkout", new CheckoutDTO());
            logger.info("Added cart and checkout to model, returning checkout view");
            return "checkout";
        } catch (Exception e) {
            logger.error("Error in showCheckout", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("emptyCart", true);
            return "checkout";
        }
    }

    @PostMapping
    public String processCheckout(@Valid @ModelAttribute CheckoutDTO checkout,
                                   BindingResult result,
                                   Model model,
                                   Authentication auth) {
        User user = getUserFromAuth(auth);
        if (user == null) {
            return "redirect:/login?redirect=/checkout";
        }

        var cart = cartService.getCartForUser(user);
        boolean isEmpty = cart == null || cart.getItems() == null || cart.getItems().isEmpty();
        model.addAttribute("emptyCart", isEmpty);
        model.addAttribute("cart", cart);

        if (result.hasErrors() || isEmpty) {
            model.addAttribute("checkout", checkout);
            return "checkout";
        }

        try {
            var order = orderService.createOrder(user, checkout);
            return "redirect:/order/confirmation/" + order.getId();
        } catch (Exception e) {
            logger.error("Error creating order", e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("emptyCart", false);
            model.addAttribute("checkout", checkout);
            return "checkout";
        }
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