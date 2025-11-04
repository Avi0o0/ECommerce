package com.ecom.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.userservice.entity.ChatInquiry;

public interface ChatInquiryRepository extends JpaRepository<ChatInquiry, Long> {
}
