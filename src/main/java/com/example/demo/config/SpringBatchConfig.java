package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;

@Configuration
public class SpringBatchConfig {

	@Autowired
	private CustomerRepository customerRepository;

	@Bean
	FlatFileItemReader<Customer> reader() {

		FlatFileItemReader<Customer> fileItemReader = new FlatFileItemReader<>();

		fileItemReader.setResource(new FileSystemResource("src/main/resources/random_data_10.csv"));
		fileItemReader.setName("csv-reader");
		fileItemReader.setLinesToSkip(1);
		fileItemReader.setLineMapper(lineMapper());

		return fileItemReader;
	}

	private LineMapper<Customer> lineMapper() {

		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

	@Bean
	ItemProcessor<Customer, Customer> processor() {
		return new CustomerProcessor();
	}

	@Bean
	RepositoryItemWriter<Customer> writer() {
		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			StepChunkListener stepChunkListener) {
		return new StepBuilder("csv-step", jobRepository).<Customer, Customer>chunk(2, transactionManager)
				.listener(stepChunkListener).reader(reader()).processor(processor()).writer(writer()).build();
	}

	@Bean
	public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			JobCompletionListener jobListener, StepChunkListener stepChunkListener) {
		return new JobBuilder("importCustomers", jobRepository).listener(jobListener)
				.start(step1(jobRepository, transactionManager, stepChunkListener)).build();
	}

//	@Bean
//	public TaskExecutor taskExecutor() {
//		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
//		asyncTaskExecutor.setConcurrencyLimit(10);
//		return asyncTaskExecutor;
//	}

}
