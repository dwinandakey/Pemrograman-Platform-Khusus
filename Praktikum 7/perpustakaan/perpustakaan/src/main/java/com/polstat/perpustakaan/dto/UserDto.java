package com.polstat.perpustakaan.dto;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements UserDetails{
    private Long id;
    private String name;
    private String email;
    private String password;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
