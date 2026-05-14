package com.darlalla.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.darlalla.entity.Product;
import com.darlalla.entity.User;
import com.darlalla.repository.ProductRepository;
import com.darlalla.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                          ProductRepository productRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdmin();
        initProducts();
    }

    private void initAdmin() {
        String adminEmail = "admin@dar-lalla.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setNom("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            logger.info("Admin user created: admin@dar-lalla.com / admin");
        } else {
            userRepository.findByEmail(adminEmail).ifPresent(user -> {
                if (user.getRole() == null || !user.getRole().equals("ADMIN")) {
                    user.setRole("ADMIN");
                    userRepository.save(user);
                    logger.info("Admin role updated for existing admin user");
                }
            });
        }

        userRepository.findAll().forEach(user -> {
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
                userRepository.save(user);
                logger.info("Fixed role for user: {}", user.getEmail());
            }
        });
    }

    private void initProducts() {
        if (productRepository.count() > 0) {
            logger.info("Products already exist, skipping init");
            return;
        }

        productRepository.save(new Product(
            "Takchita Royale",
            "Takchita",
            "Magnifique takchita traditionnelle en satin enrichie de broderies dorées et de stones. Parfaite pour les mariages et grandes occasions.",
            2500,
            "M",
            "Rouge bordeaux",
            5,
            "takchita.jpeg"
        ));

        productRepository.save(new Product(
            "Caftan Elegance",
            "Caftan",
            "Caftan moderne en velours avec découpe élégante et finitions dorées. Pour un look sophistiqué.",
            1800,
            "L",
            "Bleu royal",
            8,
            "caftan.jpeg"
        ));

        productRepository.save(new Product(
            "Mdamma Traditionnelle",
            "Mdamma",
            "Mdamma artisanale en tissu noble avec broderies marocaines traditionnelles. Idéale pour les cérémonies.",
            1200,
            "S",
            "Vert émeraude",
            3,
            "mdamma.jpeg"
        ));

        productRepository.save(new Product(
            "Djellaba Moderne",
            "Djellaba",
            "Djellaba contemporaine en cotton premium avec capuche. Confortable et élégante pour le quotidien.",
            850,
            "XL",
            "Noir",
            15,
            "djellaba.jpeg"
        ));

        logger.info("Sample products created: 1 per category");
    }
}