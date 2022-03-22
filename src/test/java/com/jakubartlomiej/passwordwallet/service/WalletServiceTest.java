package com.jakubartlomiej.passwordwallet.service;

import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.model.Wallet;
import com.jakubartlomiej.passwordwallet.repository.WalletRepository;
import com.jakubartlomiej.passwordwallet.security.WalletSecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private WalletSecurityUtil walletSecurityUtil;
    private WalletService underTest;
    private User user;

    @BeforeEach
    void setUp() {
        underTest = new WalletService(walletRepository, walletSecurityUtil);
        user = new User();
        user.setId(1L);
        user.setPasswordHash("test");
        user.setSalt("test");
        user.setLogin("test");
        user.setPasswordKeptAsHash(true);
        walletSecurityUtil.passwordUser = user.getPasswordHash();
    }

    @AfterEach
    void tearDown() {
        walletRepository.deleteAll();
    }

    @Test
    void shouldReturnWalletByUserId() {
        //given
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.setWebAddress("www.test.pl");
        wallet.setPassword("ww.test");
        Wallet wallet2 = new Wallet();
        wallet2.setId(2L);
        wallet2.setUser(user);
        wallet2.setWebAddress("www.test2222.pl");
        wallet2.setPassword("ww.test222");
        List<Wallet> wallets = new ArrayList<>();
        wallets.add(wallet);
        wallets.add(wallet2);
        //when
        Mockito.when(walletRepository.findByUserId(user.getId())).thenReturn(wallets);
        List<Wallet> walletByUserId = underTest.findByUserId(user.getId());
        //then
        assertEquals(2, walletByUserId.size());
    }

    @Test
    void shouldReturnEmptyWalletByUserId() {
        //given
        List<Wallet> wallets = new ArrayList<>();
        //when
        Mockito.when(walletRepository.findByUserId(user.getId())).thenReturn(wallets);
        List<Wallet> walletByUserId = underTest.findByUserId(user.getId());
        //then
        assertTrue(walletByUserId.isEmpty());
    }

    @Test
    void shouldReturnWalletById() {
        //given
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.setWebAddress("www.test.pl");
        wallet.setPassword("ww.test");
        //when
        Mockito.when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        Wallet foundWallet = underTest.findById(wallet.getId());
        //then
        assertEquals(foundWallet.getId(), wallet.getId());
        assertEquals(foundWallet.getUser(), wallet.getUser());
        assertEquals(foundWallet.getWebAddress(), wallet.getWebAddress());
        assertEquals(foundWallet.getPassword(), wallet.getPassword());
    }

    @Test
    @ExceptionHandler(RuntimeException.class)
    void shouldReturnExceptionWhenWalletNotFound() {
        //given
        long walletId = 1000L;
        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> underTest.findById(walletId));
        //then
        assertEquals("Wallet not found: " + walletId, exception.getMessage());
    }


    @Test
    void canSavePasswordInWallet() throws Exception {
        //given
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setDescription("Test");
        wallet.setWebAddress("www.test.pl");
        wallet.setPassword("passwordToWebAddress");
        wallet.setLogin("testowy");
        Key key = underTest.calculatePasswordToMD5(walletSecurityUtil.passwordUser);
        //when
        underTest.save(wallet.getWebAddress(), wallet.getDescription(), wallet.getPassword(), wallet.getLogin(), wallet.getUser());
        //then
        ArgumentCaptor<Wallet> walletArgumentCaptor = ArgumentCaptor.forClass(Wallet.class);
        Mockito.verify(walletRepository).save(walletArgumentCaptor.capture());
        Wallet capturedWallet = walletArgumentCaptor.getValue();

        assertNotNull(capturedWallet.getPassword());
        assertEquals(capturedWallet.getId(), wallet.getId());
        assertEquals(capturedWallet.getLogin(), wallet.getLogin());
        assertEquals(capturedWallet.getWebAddress(), wallet.getWebAddress());
        assertEquals(walletSecurityUtil.decrypt(capturedWallet.getPassword(), key), wallet.getPassword());
    }
}