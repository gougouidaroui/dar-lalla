package com.darlalla.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.darlalla.dto.ProductFormDTO;
import com.darlalla.service.AdminService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("products", adminService.getAllProducts());
        model.addAttribute("totalUsers", adminService.getTotalUsers());
        model.addAttribute("totalOrders", adminService.getTotalOrders());
        model.addAttribute("totalRevenue", adminService.getTotalRevenue());
        return "admin/dashboard";
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductFormDTO());
        model.addAttribute("isEdit", false);
        return "admin/product-form";
    }

    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("product") ProductFormDTO product,
                                 BindingResult result, Model model) {
        model.addAttribute("isEdit", false);
        if (result.hasErrors()) {
            return "admin/product-form";
        }

        MultipartFile imageFile = product.getImageFile();
        if (imageFile == null || imageFile.isEmpty()) {
            result.rejectValue("imageFile", "error.product", "L'image est obligatoire");
            return "admin/product-form";
        }

        adminService.saveProduct(product);
        return "redirect:/admin?success";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var product = adminService.getProductById(id);

        ProductFormDTO form = new ProductFormDTO();
        form.setId(product.getId());
        form.setNom(product.getNom());
        form.setCategorie(product.getCategorie());
        form.setDescription(product.getDescription());
        form.setPrix(product.getPrix());
        form.setTaille(product.getTaille());
        form.setCouleur(product.getCouleur());
        form.setStock(product.getStock());
        form.setImage(product.getImage());

        model.addAttribute("product", form);
        model.addAttribute("isEdit", true);
        return "admin/product-form";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") ProductFormDTO product,
                                BindingResult result, Model model) {
        model.addAttribute("isEdit", true);
        if (result.hasErrors()) {
            return "admin/product-form";
        }

        adminService.updateProduct(id, product);
        return "redirect:/admin?success";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return "redirect:/admin?deleted";
    }

    // Users
    @GetMapping("/users")
    public String usersPage(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role) {
        adminService.updateUserRole(id, role);
        return "redirect:/admin/users?success";
    }

    // Orders
    @GetMapping("/orders")
    public String ordersPage(Model model) {
        model.addAttribute("orders", adminService.getAllOrders());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        var orderStatus = com.darlalla.entity.Order.OrderStatus.valueOf(status);
        adminService.updateOrderStatus(id, orderStatus);
        return "redirect:/admin/orders?success";
    }
}