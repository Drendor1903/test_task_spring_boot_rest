package ru.frolov.TestTaskSpringBoot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.frolov.TestTaskSpringBoot.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhoneNumber(String phoneNumber);
}
