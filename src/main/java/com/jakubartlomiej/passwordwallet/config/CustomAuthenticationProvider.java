package com.jakubartlomiej.passwordwallet.config;

import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.security.SecurityUtil;
import com.jakubartlomiej.passwordwallet.security.WalletSecurityUtil;
import com.jakubartlomiej.passwordwallet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final WalletSecurityUtil walletSecurityUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        if (userService.findByLogin(name) != null) {
            User user = userService.findByLogin(name);
            if (name.equals(user.getLogin()) && checkPassword(user, password)) {
                final List<GrantedAuthority> grantedAuths = new ArrayList<>();
                grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
                walletSecurityUtil.passwordUser = password;
                return new UsernamePasswordAuthenticationToken(
                        name, password, grantedAuths);
            } else {
                throw new BadCredentialsException("External system authentication failed");
            }
        } else {
            throw new RuntimeException("User not found: " + name);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean checkPassword(User user, String password) {
        if (user.getPasswordHash().startsWith("{sha512}")) {
            return securityUtil.getSecurePasswordBySHA512(password, user.getSalt()).equals(user.getPasswordHash());
        } else {
            return securityUtil.getSecuredPasswordByHMCA(password, user.getSalt()).equals(user.getPasswordHash());
        }
    }
}
