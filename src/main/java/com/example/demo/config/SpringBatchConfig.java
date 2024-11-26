package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.BindException;

import com.example.demo.entity.Customer;
import com.example.demo.listener.CustomSkipListener;
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
		lineTokenizer.setNames("Customer unique id", "first_Name", "last_Name", "email", "gender", "contact_No",
				"country", "dob");

//		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
//		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(new FieldSetMapper<Customer>() {
			@Override
			public Customer mapFieldSet(FieldSet fieldSet) throws BindException {

				Customer customer = Customer.builder().country(fieldSet.readString("country"))
						.customerUniqueId(fieldSet.readInt("Customer unique id"))
						.contactNo(fieldSet.readInt("contact_No")).dob(fieldSet.readString("dob"))
						.email(fieldSet.readString("email")).firstName(fieldSet.readString("first_Name"))
						.gender(fieldSet.readString("gender")).lastName(fieldSet.readString("last_Name")).build();

				return customer;
			}
		});

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

	Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	        StepChunkListener stepChunkListener, CustomSkipListener customSkipListener) {
	    return new StepBuilder("csv-step", jobRepository)
	        .<Customer, Customer>chunk(10, transactionManager)  // Increased chunk size
	        .listener(stepChunkListener)
	        .listener(customSkipListener)
	        .reader(reader())
	        .processor(processor())
	        .writer(writer())
	        .faultTolerant()
	        .skip(FlatFileParseException.class)    // Handle parsing errors
	        .skip(IllegalArgumentException.class)  // Handle validation errors
	        .skip(Exception.class)                 // Catch-all for other exceptions
	        .skipLimit(100)                        // Increased skip limit
	        .noSkip(RuntimeException.class)        // Prevent skipping runtime exceptions
	        .build();
	}

	@Bean
	public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			JobCompletionListener jobListener, StepChunkListener stepChunkListener,
			CustomSkipListener customSkipListener) {
		return new JobBuilder("importCustomers", jobRepository).listener(jobListener)
				.start(step1(jobRepository, transactionManager, stepChunkListener, customSkipListener)).build();
	}

//	@Bean
//	public TaskExecutor taskExecutor() {
//		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
//		asyncTaskExecutor.setConcurrencyLimit(10);
//		return asyncTaskExecutor;
//	}

}
