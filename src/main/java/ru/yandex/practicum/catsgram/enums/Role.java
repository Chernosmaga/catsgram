package ru.yandex.practicum.catsgram.enums;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.catsgram.enums.Permission.*;

@Getter
public enum Role {
    USER(Set.of(USER_CREATE ,USER_UPDATE, USER_GET, USER_DELETE)),
    ADMIN(Set.of(USER_CREATE, USER_UPDATE, USER_GET, USER_DELETE));


    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> authorities() {
        return getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
