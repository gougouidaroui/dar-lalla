package com.darlalla.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String categorie;
    private String description;
    private double prix;
    private String taille;
    private String couleur;
    private int stock;
    private String image;

    private boolean active = true;

    public Product() {
    }

    public Product(String nom, String categorie, String description, double prix, String taille, String couleur, int stock, String image) {
        this.nom = nom;
        this.categorie = categorie;
        this.description = description;
        this.prix = prix;
        this.taille = taille;
        this.couleur = couleur;
        this.stock = stock;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getDescription() {
        return description;
    }

    public double getPrix() {
        return prix;
    }

    public String getTaille() {
        return taille;
    }

    public String getCouleur() {
        return couleur;
    }

    public int getStock() {
        return stock;
    }

    public String getImage() {
        return image;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}