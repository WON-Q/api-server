package com.fisa.wonq.global.security.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String userId;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    @Builder
    public UserDetailsImpl(
            Long id,
            String userId,
            String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.authorities = authorities != null ? authorities : Collections.emptyList();
    }

    public String getAuthority() {
        return authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
