package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.JobAudit;

public interface JobAuditRepository extends JpaRepository<JobAudit, Long> {

}
