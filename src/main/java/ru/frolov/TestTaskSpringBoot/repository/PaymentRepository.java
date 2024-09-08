package ru.frolov.TestTaskSpringBoot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.frolov.TestTaskSpringBoot.model.Payment;
import ru.frolov.TestTaskSpringBoot.model.User;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findAllByUser(User user, Pageable pageable);
}
