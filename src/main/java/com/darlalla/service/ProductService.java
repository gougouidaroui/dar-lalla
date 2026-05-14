package com.darlalla.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.darlalla.entity.Product;
import com.darlalla.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        logger.debug("Fetching all products");
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategorie(String categorie) {
        logger.debug("Fetching products by category: {}", categorie);
        return productRepository.findByCategorieIgnoreCase(categorie);
    }

    @Transactional
    public Product saveProduct(Product product) {
        logger.info("Saving product: {}", product.getNom());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        logger.info("Deleting product with id: {}", id);
        productRepository.deleteById(id);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}