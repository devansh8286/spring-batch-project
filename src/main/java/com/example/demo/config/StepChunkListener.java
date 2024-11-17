package com.example.demo.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.entity.JobAudit;
import com.example.demo.repository.JobAuditRepository;

@Component
public class StepChunkListener implements ChunkListener {

	@Autowired
	private JobAuditRepository jobAuditRepository;

	private Map<Long, Long> chunkCommitCounts = new HashMap<>();

	@Override
	public void beforeChunk(ChunkContext context) {
		System.out.println("Starting new chunk...");
	}

	@Override
	public void afterChunk(ChunkContext context) {
		StepExecution stepExecution = context.getStepContext().getStepExecution();

		long commitCount = stepExecution.getCommitCount();
		long stepExecutionId = stepExecution.getId(); // Track the step ID to ensure uniqueness

		if (commitCount > 0 && (chunkCommitCounts.get(stepExecutionId) == null
				|| chunkCommitCounts.get(stepExecutionId) < commitCount)) {
			chunkCommitCounts.put(stepExecutionId, commitCount);
			long readCount = stepExecution.getReadCount();
			long writeCount = stepExecution.getWriteCount();
			long skipCount = stepExecution.getWriteSkipCount();

			JobAudit chunkAudit = new JobAudit();
			chunkAudit.setJobName(stepExecution.getJobExecution().getJobInstance().getJobName());
			chunkAudit.setTotalRead(readCount);
			chunkAudit.setTotalWritten(writeCount);
			chunkAudit.setTotalFailed(skipCount);
			chunkAudit.setStartTime(stepExecution.getStartTime());
			chunkAudit.setEndTime(LocalDateTime.now());

			jobAuditRepository.save(chunkAudit);

			System.out.println("Audit saved for commit count: " + commitCount);
			System.out.println("--------------------------");
			System.out.println("readCount : " + readCount);
			System.out.println("writeCount : " + writeCount);
			System.out.println("skipCount : " + skipCount);
			System.out.println("--------------------------");

		}

		System.out.println("commitCount : " + commitCount);

	}

	@Override
	public void afterChunkError(ChunkContext context) {
		System.out.println(
				"Error in chunk processing: " + context.getStepContext().getStepExecution().getFailureExceptions());
	}

}
