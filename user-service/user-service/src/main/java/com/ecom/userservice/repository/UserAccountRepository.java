package com.ecom.userservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.userservice.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
	Optional<UserAccount> findByUsername(String username);
	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}


