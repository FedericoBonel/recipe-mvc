package com.fedebonel.recipemvc.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final Boolean active;
    private final Collection<GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.active = user.getActive();
        authorities = new HashSet<>();
        user.getUserRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRole())));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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
        return active;
    }
}
