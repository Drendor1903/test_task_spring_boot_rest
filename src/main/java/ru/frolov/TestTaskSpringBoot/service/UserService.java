package ru.frolov.TestTaskSpringBoot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.frolov.TestTaskSpringBoot.dto.PaymentDTO;
import ru.frolov.TestTaskSpringBoot.dto.UserDTO;
import ru.frolov.TestTaskSpringBoot.model.Payment;
import ru.frolov.TestTaskSpringBoot.model.User;
import ru.frolov.TestTaskSpringBoot.repository.PaymentRepository;
import ru.frolov.TestTaskSpringBoot.repository.UserRepository;
import ru.frolov.TestTaskSpringBoot.security.UsersDetails;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PaymentRepository paymentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setBalance(1000.0);
        userRepository.save(user);
    }

    public Optional<User> findUserByPhoneNumber(String phoneNumber){
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public String makePayment(User user, PaymentDTO paymentDTO){

        if (user.getBalance() < 0 || user.getBalance() < paymentDTO.getValue()) {
            return "Недостаточно средств.";
        }

        user.setBalance(user.getBalance() - paymentDTO.getValue());
        userRepository.save(user);

        Payment payment = new Payment();

        payment.setUser(user);
        payment.setRecipientPhoneNumber(paymentDTO.getRecipientPhoneNumber());
        payment.setValue(paymentDTO.getValue());
        payment.setDate(LocalDateTime.now());

        paymentRepository.save(payment);

        return "Операция выполнена.";

    }

    @Transactional
    public void updateUser(UserDTO userDTO, UsersDetails userDetails){
        User updateUser = userRepository.findByPhoneNumber(userDetails.getUsername()).get();

        updateUser.setEmail(userDTO.getEmail());
        updateUser.setGender(userDTO.getGender());
        updateUser.setBirthDate(userDTO.getBirthDate());
        updateUser.setFullName(userDTO.getFullName());

        userRepository.save(updateUser);
    }

    public List<Payment> getPayments(User user, int page, int itemPerPage){
        return paymentRepository.findAllByUser(user, PageRequest.of(page, itemPerPage)).getContent();
    }

}
