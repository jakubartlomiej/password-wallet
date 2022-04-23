package com.jakubartlomiej.passwordwallet.service;

import com.jakubartlomiej.passwordwallet.exception.WalletNotFoundException;
import com.jakubartlomiej.passwordwallet.model.*;
import com.jakubartlomiej.passwordwallet.model.enums.ActionType;
import com.jakubartlomiej.passwordwallet.model.enums.FunctionName;
import com.jakubartlomiej.passwordwallet.repository.WalletRepository;
import com.jakubartlomiej.passwordwallet.security.WalletSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletSecurityUtil walletSecurityUtil;
    private final FunctionService functionService;
    private final FunctionRunService functionRunService;
    private final DataChangeService dataChangeService;
    private final ActionService actionService;

    public List<Wallet> findByUserId(long id) {
        return walletRepository.findByUserId(id)
                .stream()
                .filter(wallet -> !wallet.isDeleted())
                .collect(Collectors.toList());
    }

    public Wallet findById(long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + id));
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
        wallet.setDeleted(false);
        walletRepository.save(wallet);

        FunctionRun functionRunCreatePassword = createFunctionRunToUserAndFunctionName(user, FunctionName.CREATE_PASSWORD);
        functionRunService.save(functionRunCreatePassword);

        DataChange dataChange = createDataChangeWithoutValue(user, wallet, ActionType.CREATE);
        dataChange.setPresentValue(wallet.getPassword());
        dataChangeService.save(dataChange);
    }

    public void edit(Wallet wallet) throws Exception {
        Wallet oldWallet = findById(wallet.getId());
        String oldPassword = oldWallet.getPassword();
        Key key = calculatePasswordToMD5(walletSecurityUtil.passwordUser);
        wallet.setPassword(walletSecurityUtil.encrypt(wallet.getPassword(), key));
        walletRepository.save(wallet);

        FunctionRun functionRunCreatePassword = createFunctionRunToUserAndFunctionName(wallet.getUser(), FunctionName.EDIT_PASSWORD);
        functionRunService.save(functionRunCreatePassword);

        DataChange dataChange = createDataChangeWithoutValue(wallet.getUser(), wallet, ActionType.UPDATE);
        dataChange.setPresentValue(wallet.getPassword());
        dataChange.setPreviousValue(oldPassword);
        dataChangeService.save(dataChange);
    }

    public String decryptPasswordById(long id, User user) throws Exception {
        Wallet wallet = findById(id);
        Key key = calculatePasswordToMD5(walletSecurityUtil.passwordUser);
        FunctionRun functionRunViewPassword = createFunctionRunToUserAndFunctionName(user, FunctionName.VIEW_PASSWORD);
        functionRunService.save(functionRunViewPassword);
        return walletSecurityUtil.decrypt(wallet.getPassword(), key);
    }

    public Key calculatePasswordToMD5(String rawPassword) {
        byte[] calculatePasswordToMD5 = walletSecurityUtil.calculateMD5(rawPassword);
        return walletSecurityUtil.generateKey(calculatePasswordToMD5);
    }

    private HashMap<Long, String> decryptAllPasswordByUser(User user) throws Exception {
        HashMap<Long, String> decryptPassword = new HashMap<>();
        List<Wallet> userWallet = walletRepository.findByUserId(user.getId());
        for (Wallet wallet : userWallet) {
            decryptPassword.put(wallet.getId(), decryptPasswordById(wallet.getId(), user));
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

    public void editUserMainPasswordInWallet(User user, String updatedPassword) throws Exception {
        HashMap<Long, String> allPasswordByUser = decryptAllPasswordByUser(user);
        walletSecurityUtil.passwordUser = updatedPassword;
        encryptAllPasswordByUser(user.getId(), allPasswordByUser);
    }

    public void deleteById(long id, User user) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Not found wallet: " + id));

        wallet.setDeleted(true);
        walletRepository.save(wallet);

        FunctionRun functionRunViewPassword = createFunctionRunToUserAndFunctionName(user, FunctionName.DELETE_PASSWORD);
        functionRunService.save(functionRunViewPassword);

        DataChange dataChange = createDataChangeWithoutValue(user, wallet, ActionType.DELETE);
        dataChange.setPreviousValue(wallet.getPassword());
        dataChangeService.save(dataChange);
    }

    public void retrievePassword(Wallet wallet, DataChange dataChange) {
        DataChange dataChangeWithoutValue = createDataChangeWithoutValue(wallet.getUser(), wallet, ActionType.RETRIEVE);

        if (wallet.isDeleted()) {
            wallet.setDeleted(false);
        } else {
            wallet.setPassword(dataChange.getPreviousValue());
        }
        walletRepository.save(wallet);

        dataChangeWithoutValue.setPreviousValue(dataChange.getPresentValue());
        dataChangeWithoutValue.setPresentValue(dataChange.getPreviousValue());
        dataChangeService.save(dataChangeWithoutValue);
    }

    private FunctionRun createFunctionRunToUserAndFunctionName(User user, FunctionName name) {
        Function function = functionService.findByName(name);
        FunctionRun functionRun = new FunctionRun();
        functionRun.setFunction(function);
        functionRun.setUser(user);
        functionRun.setTime(LocalDateTime.now());
        return functionRun;
    }

    private DataChange createDataChangeWithoutValue(User user, Wallet wallet, ActionType actionType) {
        Action action = actionService.findByName(actionType);
        DataChange dataChange = new DataChange();
        dataChange.setAction(action);
        dataChange.setUser(user);
        dataChange.setTime(LocalDateTime.now());
        dataChange.setPasswordIdModified(wallet.getId());
        return dataChange;
    }
}
