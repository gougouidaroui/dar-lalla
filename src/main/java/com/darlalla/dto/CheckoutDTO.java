package com.darlalla.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutDTO {

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresseLivraison;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    private String paiement; // CARTE_BANCAIRE, PAYPAL, VIREMENT

    public CheckoutDTO() {
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPaiement() {
        return paiement;
    }

    public void setPaiement(String paiement) {
        this.paiement = paiement;
    }
}