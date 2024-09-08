package ru.frolov.TestTaskSpringBoot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.frolov.TestTaskSpringBoot.dto.PaymentDTO;
import ru.frolov.TestTaskSpringBoot.dto.UserDTO;
import ru.frolov.TestTaskSpringBoot.model.Payment;
import ru.frolov.TestTaskSpringBoot.model.User;
import ru.frolov.TestTaskSpringBoot.repository.PaymentRepository;
import ru.frolov.TestTaskSpringBoot.repository.UserRepository;
import ru.frolov.TestTaskSpringBoot.security.UsersDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegister() {
        User user = new User();
        user.setPassword("plainPassword");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.register(user);

        assertEquals(1000.0, user.getBalance());
        assertEquals("encodedPassword", user.getPassword());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindUserByPhoneNumber() {
        User user = new User();
        user.setPhoneNumber("1234567890");

        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUserByPhoneNumber("1234567890");

        assertTrue(foundUser.isPresent());
        assertEquals("1234567890", foundUser.get().getPhoneNumber());
    }

    @Test
    void testMakePaymentSuccess() {
        User user = new User();
        user.setBalance(2000.0);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setRecipientPhoneNumber("0987654321");
        paymentDTO.setValue(500.0);

        String result = userService.makePayment(user, paymentDTO);

        assertEquals("Операция выполнена.", result);
        assertEquals(1500.0, user.getBalance());

        verify(userRepository, times(1)).save(user);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testMakePaymentFailure() {
        User user = new User();
        user.setBalance(200.0);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setRecipientPhoneNumber("0987654321");
        paymentDTO.setValue(500.0);

        String result = userService.makePayment(user, paymentDTO);

        assertEquals("Недостаточно средств.", result);
        assertEquals(200.0, user.getBalance());

        verify(userRepository, times(0)).save(user); // Проверка, что сохранение не произошло
        verify(paymentRepository, times(0)).save(any(Payment.class));
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User();
        existingUser.setPhoneNumber("1234567890");

        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(existingUser));

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newemail@example.com");
        userDTO.setFullName("John Doe");
        userDTO.setGender("Male");
        userDTO.setBirthDate(LocalDateTime.of(1990, 2, 2, 0, 0, 0));

        UsersDetails userDetails = mock(UsersDetails.class);
        when(userDetails.getUsername()).thenReturn("1234567890");

        userService.updateUser(userDTO, userDetails);

        assertEquals("newemail@example.com", existingUser.getEmail());
        assertEquals("John Doe", existingUser.getFullName());
        assertEquals("Male", existingUser.getGender());
        assertEquals(LocalDateTime.of(1990, 2, 2, 0, 0, 0), existingUser.getBirthDate());

        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testGetPayments() {
        User user = new User();
        List<Payment> payments = Arrays.asList(new Payment(), new Payment());

        Page<Payment> paymentPage = new PageImpl<>(payments);

        when(paymentRepository.findAllByUser(any(User.class), any(PageRequest.class)))
                .thenReturn(paymentPage);

        List<Payment> result = userService.getPayments(user, 0, 2);

        assertEquals(2, result.size());
        verify(paymentRepository, times(1)).findAllByUser(user, PageRequest.of(0, 2));
    }

}
