package com.example.demo.config;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.entity.JobAudit;
import com.example.demo.repository.JobAuditRepository;

@Component
public class JobCompletionListener implements JobExecutionListener {

	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private JobAuditRepository jobAuditRepository;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		JobExecutionListener.super.beforeJob(jobExecution);
		System.out.println("Job started with parameters: " + jobExecution.getJobParameters());

	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		System.out.println("***********************");

		jobExecution.getStartTime();

		JobAudit audit = new JobAudit();
		audit.setJobName(jobExecution.getJobInstance().getJobName());
		audit.setStatus(jobExecution.getStatus().toString());
		audit.setTotalRead(jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getReadCount).sum());
		audit.setTotalWritten(jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum());
		audit.setTotalFailed(
				jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteSkipCount).sum());
		if (jobExecution.getStartTime() != null) {
			audit.setStartTime(jobExecution.getStartTime());
		}
		if (jobExecution.getEndTime() != null) {
			audit.setEndTime(jobExecution.getEndTime());
		}

		//to save after job completed
//		jobAuditRepository.save(audit);
		System.out.println("********SAVE EXECUETD ***************");

		BatchStatus status = jobExecution.getStatus();
		BatchStatus completed = BatchStatus.COMPLETED;
		System.out.println("status : " + status + " completed : " + completed);

		if (status == completed) {
			long totalRead = jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getReadCount).sum();
			long totalWrite = jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum();
			long totalFailed = jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteSkipCount)
					.sum();

			System.out.println("Total Records Read: " + totalRead);
			System.out.println("Total Records Written: " + totalWrite);
			System.out.println("Total Records Failed: " + totalFailed);
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			System.out.println("Job failed with exceptions: " + jobExecution.getAllFailureExceptions());
		}
		System.out.println("***********************");

	}
}
