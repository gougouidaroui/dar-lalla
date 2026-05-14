package com.darlalla.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.darlalla.dto.CheckoutDTO;
import com.darlalla.entity.*;
import com.darlalla.repository.CartRepository;
import com.darlalla.repository.OrderRepository;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    public List<Order> getOrdersByUser(User user) {
        logger.info("Getting orders for user: {}", user.getId());
        return orderRepository.findByUserId(user.getId());
    }

    public Order getOrderById(Long orderId, User user) {
        logger.info("Getting order {} for user: {}", orderId, user.getId());
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .orElse(null);
    }

    @Transactional
    public Order createOrder(User user, CheckoutDTO checkout) {
        logger.info("Creating order for user: {}", user.getId());

        Cart cart = cartService.getCartForUser(user);
        logger.info("Cart found, items count: {}", cart.getItems() != null ? cart.getItems().size() : 0);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Le panier est vide");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAdresseLivraison(checkout.getAdresseLivraison());
        order.setTelephone(checkout.getTelephone());
        order.setPaiement(checkout.getPaiement() != null ? checkout.getPaiement() : "CARTE_BANCAIRE");
        order.setStatus(Order.OrderStatus.EN_ATTENTE);

        double total = 0;
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct() == null) {
                logger.warn("CartItem has null product, skipping");
                continue;
            }

            Product product = cartItem.getProduct();
            logger.info("Processing product: {} with quantity: {}", product.getNom(), cartItem.getQuantite());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantite(cartItem.getQuantite());
            orderItem.setPrixUnitaire(cartItem.getPrixUnitaire());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);

            total += cartItem.getQuantite() * cartItem.getPrixUnitaire();

            product.setStock(product.getStock() - cartItem.getQuantite());
        }

        if (order.getItems().isEmpty()) {
            throw new RuntimeException("Aucun produit valide dans le panier");
        }

        double shippingCost = total >= 100 ? 0 : 100;
        order.setTotal(total + shippingCost);

        logger.info("Saving order with total: {}", order.getTotal());
        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved with id: {}", savedOrder.getId());

        // Clear cart after order
        cartService.clearCart(user);
        logger.info("Cart cleared for user: {}", user.getId());

        return savedOrder;
    }
}