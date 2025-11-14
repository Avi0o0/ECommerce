package com.ecom.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.userservice.entity.BlackListToken;

@Repository
public interface BlackListTokenRepo extends JpaRepository<BlackListToken, Long> {

	Optional<BlackListToken> findByToken(String token);
}
