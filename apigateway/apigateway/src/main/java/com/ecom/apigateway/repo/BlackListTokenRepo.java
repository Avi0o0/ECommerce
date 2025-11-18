package com.ecom.apigateway.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.apigateway.entity.BlackListToken;

@Repository
public interface BlackListTokenRepo extends JpaRepository<BlackListToken, Long> {

	Optional<BlackListToken> findByToken(String token);
}
