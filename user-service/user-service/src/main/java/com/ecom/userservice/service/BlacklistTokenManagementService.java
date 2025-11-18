package com.ecom.userservice.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.userservice.repository.BlackListTokenRepo;

@Service
public class BlacklistTokenManagementService {

	private static final Logger logger = LoggerFactory.getLogger(BlacklistTokenManagementService.class);

	private BlackListTokenRepo tokenRepo;

	public BlacklistTokenManagementService(BlackListTokenRepo tokenRepo) {
		super();
		this.tokenRepo = tokenRepo;
	}

	@Scheduled(cron = "0 0 */2 * * *")
	@Transactional
	public void cleanExpiredTokens() {

		logger.info("Running Scheduled Method...");

		LocalDateTime cutoff = LocalDateTime.now().minusHours(2);
		int deleted = tokenRepo.deleteAllOlderThan(cutoff);

		logger.info("Deleted old tokens: {}", deleted);
	}

}
