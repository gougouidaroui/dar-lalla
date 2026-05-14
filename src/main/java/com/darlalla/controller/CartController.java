package com.darlalla.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.darlalla.entity.User;
import com.darlalla.repository.UserRepository;
import com.darlalla.service.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    private User getUserFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @GetMapping
    public String showCart(Model model, Authentication auth) {
        User user = getUserFromAuth(auth);
        if (user != null) {
            var cart = cartService.getCartForUser(user);
            model.addAttribute("cart", cart);
        } else {
            model.addAttribute("cart", null);
        }
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantite,
                            Authentication auth) {
        User user = getUserFromAuth(auth);
        if (user == null) {
            return "redirect:/login";
        }
        try {
            cartService.addToCart(user, productId, quantite);
        } catch (Exception e) {
            System.err.println("Error adding to cart: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam Long productId,
                              @RequestParam int quantite,
                              Authentication auth) {
        User user = getUserFromAuth(auth);
        if (user == null) {
            return "redirect:/login";
        }
        if (quantite <= 0) {
            cartService.removeFromCart(user, productId);
        } else {
            cartService.updateQuantity(user, productId, quantite);
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                  Authentication auth) {
        User user = getUserFromAuth(auth);
        if (user == null) {
            return "redirect:/login";
        }
        cartService.removeFromCart(user, productId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Authentication auth) {
        User user = getUserFromAuth(auth);
        if (user == null) {
            return "redirect:/login";
        }
        cartService.clearCart(user);
        return "redirect:/cart";
    }
}