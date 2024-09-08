package ru.frolov.TestTaskSpringBoot.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.frolov.TestTaskSpringBoot.model.User;

import java.util.Collection;
import java.util.Collections;

public class UsersDetails implements UserDetails {
    public final User user;

    public UsersDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    public int getId(){
        return this.user.getUserId();
    }

    @Override
    public String getUsername() {
        return this.user.getPhoneNumber();
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

    //получение данных аутентифицированного пользователя
    public User getUser(){
        return this.user;
    }
}
