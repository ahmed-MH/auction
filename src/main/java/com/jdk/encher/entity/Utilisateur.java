package com.jdk.encher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nom;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 6)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private int soldeCredit;

    private boolean etatCompte = true; // true = actif, false = désactivé

    @OneToMany(mappedBy = "createur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Encher> enchersDefinies;

    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    private List<Encher> encheresParticipees;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Paiement> paiementsEffectues;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notificationRecues;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransactionCredit> transactionsEffectuees;

    // Implémentation de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.motDePasse;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.etatCompte;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.etatCompte;
    }

    // Getters et Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getSoldeCredit() {
        return soldeCredit;
    }

    public void setSoldeCredit(int soldeCredit) {
        this.soldeCredit = soldeCredit;
    }

    public boolean isEtatCompte() {
        return etatCompte;
    }

    public void setEtatCompte(boolean etatCompte) {
        this.etatCompte = etatCompte;
    }

    public List<Encher> getEnchersDefinies() {
        return enchersDefinies;
    }

    public void setEnchersDefinies(List<Encher> enchersDefinies) {
        this.enchersDefinies = enchersDefinies;
    }

    public List<Encher> getEncheresParticipees() {
        return encheresParticipees;
    }

    public void setEncheresParticipees(List<Encher> encheresParticipees) {
        this.encheresParticipees = encheresParticipees;
    }

    public List<Paiement> getPaiementsEffectues() {
        return paiementsEffectues;
    }

    public void setPaiementsEffectues(List<Paiement> paiementsEffectues) {
        this.paiementsEffectues = paiementsEffectues;
    }

    public List<Notification> getNotificationRecues() {
        return notificationRecues;
    }

    public void setNotificationRecues(List<Notification> notificationRecues) {
        this.notificationRecues = notificationRecues;
    }

    public List<TransactionCredit> getTransactionsEffectuees() {
        return transactionsEffectuees;
    }

    public void setTransactionsEffectuees(List<TransactionCredit> transactionsEffectuees) {
        this.transactionsEffectuees = transactionsEffectuees;
    }
}