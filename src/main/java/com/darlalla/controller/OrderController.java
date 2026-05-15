package com.darlalla.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.darlalla.entity.Order;
import com.darlalla.entity.User;
import com.darlalla.repository.UserRepository;
import com.darlalla.service.OrderService;
import com.darlalla.service.PdfService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final UserRepository userRepository;
    private final OrderService orderService;
    private final PdfService pdfService;

    public OrderController(UserRepository userRepository, OrderService orderService, PdfService pdfService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.pdfService = pdfService;
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

    @GetMapping("/receipt/{id}/download")
    public void downloadReceipt(@PathVariable Long id, Authentication auth, HttpServletResponse response) {
        User user = getUserFromAuth(auth);
        if (user == null) {
            return;
        }

        Order order = orderService.getOrderById(id, user);
        if (order == null) {
            return;
        }

        try {
            byte[] pdf = pdfService.generateReceipt(order);

            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recu-commande-" + id + ".pdf");
            response.setContentLength(pdf.length);
            response.getOutputStream().write(pdf);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}