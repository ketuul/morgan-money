package com.ketul.morganmoney.repository;

import com.ketul.morganmoney.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA automatically generates this query from the method name
    // It translates to: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}
