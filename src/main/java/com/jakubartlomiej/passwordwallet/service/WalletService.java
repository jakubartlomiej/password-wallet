package com.jakubartlomiej.passwordwallet.service;

import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.model.Wallet;
import com.jakubartlomiej.passwordwallet.repository.WalletRepository;
import com.jakubartlomiej.passwordwallet.security.WalletSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletSecurityUtil walletSecurityUtil;

    public List<Wallet> findByUserId(long id) {
        return walletRepository.findByUserId(id);
    }

    public Wallet findById(long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + id));
    }

    public void save(String webAddress,
                     String description,
                     String passwordToWebAddress,
                     String loginToWebAddress, User user) throws Exception {
        Wallet wallet = new Wallet();
        Key key = calculatePasswordToMD5(walletSecurityUtil.passwordUser);
        wallet.setWebAddress(webAddress);
        wallet.setDescription(description);
        wallet.setPassword(walletSecurityUtil.encrypt(passwordToWebAddress, key));
        wallet.setLogin(loginToWebAddress);
        wallet.setUser(user);
        walletRepository.save(wallet);
    }

    public String decryptPasswordById(long id) throws Exception {
        Wallet wallet = findById(id);
        Key key = calculatePasswordToMD5(walletSecurityUtil.passwordUser);
        return walletSecurityUtil.decrypt(wallet.getPassword(), key);
    }

    public Key calculatePasswordToMD5(String rawPassword) {
        byte[] calculatePasswordToMD5 = walletSecurityUtil.calculateMD5(rawPassword);
        return walletSecurityUtil.generateKey(calculatePasswordToMD5);
    }

    private HashMap<Long, String> decryptAllPasswordByUser(long userId) throws Exception {
        HashMap<Long, String> decryptPassword = new HashMap<>();
        List<Wallet> userWallet = walletRepository.findByUserId(userId);
        for (Wallet wallet : userWallet) {
            decryptPassword.put(wallet.getId(), decryptPasswordById(wallet.getId()));
        }
        return decryptPassword;
    }

    private void encryptAllPasswordByUser(long userId, HashMap<Long, String> passwordsToEncode) throws Exception {
        List<Wallet> userWallet = walletRepository.findByUserId(userId);
        Key key = calculatePasswordToMD5(walletSecurityUtil.passwordUser);
        for (Wallet wallet : userWallet) {
            wallet.setPassword(walletSecurityUtil.encrypt(passwordsToEncode.get(wallet.getId()), key));
            walletRepository.save(wallet);
        }
    }

    public void editUserMainPasswordInWallet(long userId, String updatedPassword) throws Exception {
        HashMap<Long, String> allPasswordByUser = decryptAllPasswordByUser(userId);
        walletSecurityUtil.passwordUser = updatedPassword;
        encryptAllPasswordByUser(userId, allPasswordByUser);
    }
}
