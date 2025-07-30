package com.talecraft.talecraftbe.inquiry.repository;

import com.talecraft.talecraftbe.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
} 