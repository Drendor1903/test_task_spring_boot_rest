package ru.frolov.TestTaskSpringBoot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.frolov.TestTaskSpringBoot.model.User;
import ru.frolov.TestTaskSpringBoot.repository.UserRepository;
import ru.frolov.TestTaskSpringBoot.security.UsersDetails;

import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if(user.isEmpty())
            throw new UsernameNotFoundException("user not found");

        return new UsersDetails(user.get());
    }
}
