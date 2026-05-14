package com.darlalla.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.darlalla.service.ProductService;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String productsPage(@RequestParam(required = false) String categorie,
                                Model model,
                                HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (categorie != null && !categorie.trim().isEmpty()) {
            model.addAttribute("products", productService.getProductsByCategorie(categorie));
            model.addAttribute("titre", categorie);
        } else {
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("titre", "Tous les Produits");
        }

        return "products";
    }
}