package com.personal.projectforum.dto.security;

import com.personal.projectforum.dto.UserAccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record ForumPrincipal(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String email,
        String nickname,
        String memo
) implements UserDetails {

    public static ForumPrincipal of(String username, String password, String email, String nickname, String memo) {

        // Right now only doing authentication and not dealing with authority, so I set it like this.
        Set<RoleTyle> roleTypes = Set.of(RoleTyle.USER);

        return new ForumPrincipal(
                username,
                password,
                roleTypes.stream()
                    .map(RoleTyle::getName)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toUnmodifiableSet()),
                email,
                nickname,
                memo
        );
    }

    public static ForumPrincipal from(UserAccountDto dto) {
        return ForumPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.email(),
                dto.nickname(),
                dto.memo()
        );
    }

    public UserAccountDto toDto() {
        return UserAccountDto.of(
                username,
                password,
                email,
                nickname,
                memo
        );
    }

    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return username; }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    public enum RoleTyle {
        USER("ROLE_USER");

        @Getter
        private final String name;

        RoleTyle(String name) {
            this.name = name;
        }
    }

}
