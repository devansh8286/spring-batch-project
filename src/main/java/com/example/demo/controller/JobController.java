package com.example.demo.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/job")

public class JobController {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@PostMapping("/importCustomer")
	public void importCsv() {

		JobParameters jobParameters = new JobParametersBuilder().addString("fileName", "test")
				.addLong("startedAt", System.currentTimeMillis()).toJobParameters();

		try {
			jobLauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}

	}

}
