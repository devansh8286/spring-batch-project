package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "JOB_AUDIT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobAudit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "JOB_NAME")
	private String jobName;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "TOTAL_READ")
	private Long totalRead;

	@Column(name = "TOTAL_WRITTEN")
	private Long totalWritten;

	@Column(name = "TOTAL_SKIPPED")
	private Long totalSkipped;

	@Column(name = "TOTAL_FAILED")
	private Long totalFailed;

	@Column(name = "TOTAL_CUSTOMERS_PROCESSED")
	private Long totalCustomersProcessed;

	@Column(name = "TOTAL_ERROR_LOGS")
	private Long totalErrorLogs;

	@Column(name = "START_TIME")
	private LocalDateTime startTime;

	@Column(name = "END_TIME")
	private LocalDateTime endTime;
}