package com.ecom.userservice.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.userservice.entity.BlackListToken;

@Repository
public interface BlackListTokenRepo extends JpaRepository<BlackListToken, Long> {

	Optional<BlackListToken> findByToken(String token);

	@Modifying
	@Transactional
	@Query("DELETE FROM BlackListToken b WHERE b.createdAt < :cutoff")
	int deleteAllOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
