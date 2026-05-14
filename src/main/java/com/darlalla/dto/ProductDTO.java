package com.darlalla.dto;

public class ProductDTO {

    private Long id;
    private String nom;
    private String categorie;
    private String description;
    private double prix;
    private String taille;
    private String couleur;
    private int stock;
    private String image;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String nom, String categorie, String description,
                      double prix, String taille, String couleur, int stock, String image) {
        this.id = id;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}