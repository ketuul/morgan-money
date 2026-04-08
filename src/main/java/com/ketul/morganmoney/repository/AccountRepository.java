package com.ketul.morganmoney.repository;

import com.ketul.morganmoney.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<Account, Long> means:
// - we're storing Account objects
// - the primary key type is Long (the id field)
// Spring automatically generates all the database operations for you
// You get save(), findById(), findAll(), delete() and more for free
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
