package com.jakubartlomiej.passwordwallet.service;

import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.repository.UserRepository;
import com.jakubartlomiej.passwordwallet.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    public void register(String login, String password, String hashType) {
        User user = new User();
        byte[] salt = securityUtil.getSalt();
        user.setLogin(login);
        user.setSalt(Arrays.toString(salt));
        if (hashType.equals("SHA512")) {
            user.setPasswordHash(securityUtil.getSecurePasswordBySHA512(password, Arrays.toString(salt)));
        } else {
            user.setPasswordHash(securityUtil.getSecuredPasswordByHMCA(password, Arrays.toString(salt)));
        }
        user.setPasswordKeptAsHash(true);
        userRepository.save(user);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }

    public boolean checkIfLoginAvailable(String login) {
        return userRepository.findByLogin(login).isEmpty();
    }

    public boolean editPassword(String password, String username) {
        User user = findByLogin(username);
        byte[] newSalt = securityUtil.getSalt();
        user.setSalt(Arrays.toString(newSalt));
        if (user.getPasswordHash().startsWith("{sha512}")) {
            user.setPasswordHash(securityUtil.getSecurePasswordBySHA512(password, Arrays.toString(newSalt)));
        } else {
            user.setPasswordHash(securityUtil.getSecuredPasswordByHMCA(password, Arrays.toString(newSalt)));
        }
        userRepository.save(user);
        return true;
    }
}
