package com.talecraft.talecraftbe.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_authorities")
public class UserAuthority {
    @Id
    private Long id;

    @Column(name = "is_general")
    private boolean isGeneral;

    @Column(name = "is_premium")
    private boolean isPremium;

    @Column(name = "is_admin")
    private boolean isAdmin;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isGeneral() { return isGeneral; }
    public void setGeneral(boolean general) { isGeneral = general; }

    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
} 