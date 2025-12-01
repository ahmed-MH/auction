package com.jdk.encher.dto;

public class UserCreditDTO {
    private Long id;
    private String nom;
    private String email;
    private int soldeCredit;

    public UserCreditDTO() {}

    public UserCreditDTO(Long id, String nom, String email, int soldeCredit) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.soldeCredit = soldeCredit;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getSoldeCredit() { return soldeCredit; }
    public void setSoldeCredit(int soldeCredit) { this.soldeCredit = soldeCredit; }
}
