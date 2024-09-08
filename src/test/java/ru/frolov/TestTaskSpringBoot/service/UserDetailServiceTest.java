package ru.frolov.TestTaskSpringBoot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.frolov.TestTaskSpringBoot.model.User;
import ru.frolov.TestTaskSpringBoot.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailService userDetailService;

    @Test
    void testLoadUserByUsername(){
        User user = new User();
        user.setPhoneNumber("1234567890");
        user.setPassword("encodedPassword");

        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailService.loadUserByUsername("1234567890");

        assertNotNull(userDetails);
        assertEquals("1234567890", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByPhoneNumber("0987654321")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailService.loadUserByUsername("0987654321");
        });
    }
}
