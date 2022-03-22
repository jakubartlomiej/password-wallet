package com.jakubartlomiej.passwordwallet.service;

import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.repository.UserRepository;
import com.jakubartlomiej.passwordwallet.security.SecurityUtil;
import com.jakubartlomiej.passwordwallet.security.WalletSecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SecurityUtil securityUtil;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, securityUtil);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserWithHashPasswordSha512() {
        //given
        User user = new User();
        byte[] salt = securityUtil.getSalt();
        user.setId(1L);
        user.setLogin("Tomasz");
        user.setPasswordHash(securityUtil.getSecurePasswordBySHA512("password", Arrays.toString(salt)));
        user.setPasswordKeptAsHash(true);
        underTest.register(user.getLogin(), user.getPasswordHash(), "SHA512");
        //when
        Mockito.when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
        User userGetByLogin = underTest.findByLogin(user.getLogin());
        //then
        assertTrue(userGetByLogin.getPasswordHash().startsWith("{sha512}"));
        assertTrue(userGetByLogin.getPasswordHash().length() > "{sha512}".length());
    }

    @Test
    void shouldRegisterUserWithHashPasswordByHMCA() {
        //given
        User user = new User();
        byte[] salt = securityUtil.getSalt();
        user.setId(1L);
        user.setLogin("Tomasz");
        user.setPasswordHash(securityUtil.getSecuredPasswordByHMCA("password", Arrays.toString(salt)));
        user.setPasswordKeptAsHash(true);
        underTest.register(user.getLogin(), user.getPasswordHash(), "HMAC");
        Mockito.when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
        //when
        User userGetByLogin = underTest.findByLogin(user.getLogin());
        //then
        assertTrue(userGetByLogin.getPasswordHash().startsWith("{hmac}"));
        assertTrue(userGetByLogin.getPasswordHash().length() > "{hmac}".length());
    }

    @Test
    void shouldReturnFalseWhenLoginNotAvailable() {
        //given
        User user = new User();
        byte[] salt = securityUtil.getSalt();
        user.setId(1L);
        user.setLogin("Tomasz");
        user.setPasswordHash(securityUtil.getSecurePasswordBySHA512("password", Arrays.toString(salt)));
        user.setPasswordKeptAsHash(true);
        underTest.register(user.getLogin(), user.getPasswordHash(), "SHA512");
        Mockito.when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
        //when
        boolean result = underTest.checkIfLoginAvailable(user.getLogin());
        //then
        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenLoginAvailable() {
        //given
        String login = "Tomasz";
        Mockito.when(userRepository.findByLogin(login)).thenReturn(Optional.empty());
        //when
        boolean result = underTest.checkIfLoginAvailable(login);
        //then
        assertTrue(result);
    }

    @Test
    void shouldFoundUserByLogin() {
        //given
        User user = new User();
        byte[] salt = securityUtil.getSalt();
        user.setId(1L);
        user.setLogin("Tomasz");
        user.setPasswordHash(securityUtil.getSecurePasswordBySHA512("password", Arrays.toString(salt)));
        user.setPasswordKeptAsHash(true);
        underTest.register(user.getLogin(), user.getPasswordHash(), "SHA512");
        Mockito.when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
        //when
        User foundUser = underTest.findByLogin(user.getLogin());
        //then
        assertNotNull(user);
        assertEquals(foundUser.getLogin(), user.getLogin());
        assertEquals(foundUser.getPasswordHash(), user.getPasswordHash());
        assertEquals(foundUser.getId(), user.getId());
        assertEquals(foundUser.getSalt(), user.getSalt());
    }

    @Test
    @ExceptionHandler(UsernameNotFoundException.class)
    void shouldReturnExceptionWhenUserNotFound() {
        //given
        String login = "Tomasz";
        //when
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> underTest.findByLogin(login));
        //then
        assertEquals("User not found: " + login, exception.getMessage());
    }

    @Test
    void canChangePasswordForUserByHMCA() {
        //given
        User user = new User();
        byte[] salt = securityUtil.getSalt();
        user.setId(1L);
        user.setLogin("Tomasz");
        user.setPasswordHash(securityUtil.getSecuredPasswordByHMCA("password", Arrays.toString(salt)));
        user.setPasswordKeptAsHash(true);
        underTest.register(user.getLogin(), user.getPasswordHash(), "HMAC");
        Mockito.when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
        //when
        String updatePassword = "update-password";
        underTest.editPassword(updatePassword, user.getLogin());
        User foundUser = underTest.findByLogin(user.getLogin());
        //then
        assertEquals(securityUtil.getSecuredPasswordByHMCA(updatePassword, foundUser.getSalt()), foundUser.getPasswordHash());
    }

    @Test
    void canChangePasswordForUserBySHA512() {
        //given
        User user = new User();
        byte[] salt = securityUtil.getSalt();
        user.setId(1L);
        user.setLogin("Tomasz");
        user.setPasswordHash(securityUtil.getSecurePasswordBySHA512("password", Arrays.toString(salt)));
        user.setPasswordKeptAsHash(true);
        underTest.register(user.getLogin(), user.getPasswordHash(), "SHA512");
        Mockito.when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
        //when
        String updatePassword = "update-password";
        underTest.editPassword(updatePassword, user.getLogin());
        User foundUser = underTest.findByLogin(user.getLogin());
        //then
        assertEquals(securityUtil.getSecurePasswordBySHA512(updatePassword, foundUser.getSalt()), foundUser.getPasswordHash());
    }
}