package com.darlalla.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public Cart() {
    }

    public Cart(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return items.stream()
                .mapToDouble(CartItem::getSousTotal)
                .sum();
    }

    public int getItemCount() {
        return items.stream()
                .mapToInt(CartItem::getQuantite)
                .sum();
    }

    public void addItem(Product product, int quantite) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantite(item.getQuantite() + quantite);
                return;
            }
        }
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantite(quantite);
        item.setPrixUnitaire(product.getPrix());
        item.setCart(this);
        items.add(item);
    }

    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clear() {
        items.clear();
    }
}