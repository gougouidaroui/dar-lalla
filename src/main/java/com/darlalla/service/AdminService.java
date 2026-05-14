package com.darlalla.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.darlalla.dto.ProductFormDTO;
import com.darlalla.entity.Order;
import com.darlalla.entity.OrderItem;
import com.darlalla.entity.Product;
import com.darlalla.entity.User;
import com.darlalla.repository.OrderItemRepository;
import com.darlalla.repository.OrderRepository;
import com.darlalla.repository.ProductRepository;
import com.darlalla.repository.UserRepository;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public AdminService(ProductRepository productRepository,
                        UserRepository userRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // User methods
    public List<User> getAllUsers() {
        logger.debug("Fetching all users for admin");
        return userRepository.findAll();
    }

    @Transactional
    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setRole(newRole);
        userRepository.save(user);
        logger.info("Updated role for user {} to {}", userId, newRole);
    }

    // Order methods
    public List<Order> getAllOrders() {
        logger.debug("Fetching all orders for admin");
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        logger.info("Updated order {} status to {}", orderId, status);
        return orderRepository.save(order);
    }

    // Dashboard stats
    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalOrders() {
        return orderRepository.count();
    }

    public double getTotalRevenue() {
        return orderRepository.findAll().stream()
                .mapToDouble(Order::getTotal)
                .sum();
    }

    public List<Product> getAllProducts() {
        logger.debug("Fetching all products for admin");
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec id: " + id));
    }

    @Transactional
    public Product saveProduct(ProductFormDTO form) {
        logger.info("Saving new product: {}", form.getNom());

        Product product = new Product();
        product.setNom(form.getNom());
        product.setCategorie(form.getCategorie());
        product.setDescription(form.getDescription());
        product.setPrix(form.getPrix());
        product.setTaille(form.getTaille());
        product.setCouleur(form.getCouleur());
        product.setStock(form.getStock());

        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            String filename = saveImage(form.getImageFile());
            product.setImage(filename);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductFormDTO form) {
        logger.info("Updating product with id: {}", id);

        Product product = getProductById(id);
        product.setNom(form.getNom());
        product.setCategorie(form.getCategorie());
        product.setDescription(form.getDescription());
        product.setPrix(form.getPrix());
        product.setTaille(form.getTaille());
        product.setCouleur(form.getCouleur());
        product.setStock(form.getStock());

        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            if (product.getImage() != null) {
                deleteImage(product.getImage());
            }
            String filename = saveImage(form.getImageFile());
            product.setImage(filename);
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        logger.info("Deleting product with id: {}", id);

        entityManager.createNativeQuery("DELETE FROM order_items WHERE product_id = ?")
                .setParameter(1, id)
                .executeUpdate();

        Product product = getProductById(id);

        if (product.getImage() != null && !product.getImage().startsWith("http")) {
            deleteImage(product.getImage());
        }

        productRepository.delete(product);
        logger.info("Product {} deleted with cascade", id);
    }

    private String saveImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            logger.info("Image saved: {}", filename);
            return filename;
        } catch (IOException e) {
            logger.error("Failed to save image", e);
            throw new RuntimeException("Échec de l'enregistrement de l'image", e);
        }
    }

    private void deleteImage(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);
            logger.info("Image deleted: {}", filename);
        } catch (IOException e) {
            logger.error("Failed to delete image: {}", filename, e);
        }
    }
}