package com.example.integration.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "user")
@Entity
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name = "enabled")
    private boolean enabled;

    @OneToMany(mappedBy = "user")
    private List<SentenceSet> sentenceSetList = new ArrayList<>();

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleType.name()));
    }

    @Builder(toBuilder = true)
    public User(String email, String password, RoleType roleType, boolean enabled) {
        this.email = email;
        this.password = password;
        this.roleType = roleType;
        this.enabled = enabled;
    }

    public void updateRole(RoleType roleType) {
        this.roleType = roleType;
    }

}
