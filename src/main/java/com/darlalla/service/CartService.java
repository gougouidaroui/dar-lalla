package com.darlalla.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.darlalla.entity.Cart;
import com.darlalla.entity.CartItem;
import com.darlalla.entity.Product;
import com.darlalla.entity.User;
import com.darlalla.repository.CartRepository;
import com.darlalla.repository.ProductRepository;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart getCartForUser(User user) {
        logger.info("Getting cart for user: {}", user.getId());
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    logger.info("Creating new cart for user: {}", user.getId());
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addToCart(User user, Long productId, int quantite) {
        logger.info("Adding product {} to cart for user {}", productId, user.getId());

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            logger.warn("Product not found: {}", productId);
            return getCartForUser(user);
        }

        Cart cart = getCartForUser(user);
        if (cart == null) {
            cart = new Cart(user);
            cart = cartRepository.save(cart);
        }

        for (CartItem item : cart.getItems()) {
            if (item.getProduct() != null && item.getProduct().getId().equals(productId)) {
                item.setQuantite(item.getQuantite() + quantite);
                return cartRepository.save(cart);
            }
        }

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantite(quantite);
        item.setPrixUnitaire(product.getPrix());
        item.setCart(cart);
        cart.getItems().add(item);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(User user, Long productId, int quantite) {
        Cart cart = getCartForUser(user);

        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                if (quantite <= 0) {
                    cart.removeItem(productId);
                } else {
                    item.setQuantite(quantite);
                }
                break;
            }
        }
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(User user, Long productId) {
        Cart cart = getCartForUser(user);
        cart.removeItem(productId);
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getCartForUser(user);
        cart.clear();
        cartRepository.save(cart);
    }
}