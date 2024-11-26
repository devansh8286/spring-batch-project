package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.CustomerLogs;

public interface CustomerLogsRepository extends JpaRepository<CustomerLogs, Integer> {

}
