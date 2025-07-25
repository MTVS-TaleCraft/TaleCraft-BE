package com.talecraft.talecraftbe.user.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "authority_id")
    private Long authorityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authority_id", insertable = false, updatable = false)
    private UserAuthority authority;

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public UserAuthority getAuthority() { return authority; }
    public void setAuthority(UserAuthority authority) { this.authority = authority; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authority == null) return Collections.emptyList();
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        if (authority.isGeneral()) authorities.add(() -> "ROLE_GENERAL");
        if (authority.isPremium()) authorities.add(() -> "ROLE_PREMIUM");
        if (authority.isAdmin()) authorities.add(() -> "ROLE_ADMIN");
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}

