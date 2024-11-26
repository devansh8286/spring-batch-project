package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerLogs;
import com.example.demo.helper.ValidationResult;
import com.example.demo.repository.CustomerLogsRepository;
import com.example.demo.service.CustomerValidationService;

public class CustomerProcessor2 implements ItemProcessor<Customer, Customer> {
	private static final Logger logger = LoggerFactory.getLogger(CustomerProcessor2.class);

	@Autowired
	private CustomerValidationService validationService;
	@Autowired
	private CustomerLogsRepository customerLogsRepository;

	@Override
	public Customer process(Customer item) throws Exception {

//		ValidationResult validationResult = validationService.validate(item);
//		if (!validationResult.isValid()) {
//			// Log validation errors
//			CustomerLogs errorLog = CustomerLogs.builder().customerUniqueId(item.getCustomerUniqueId())
//					.firstName(item.getFirstName()).reason(String.join("; ", validationResult.getErrors())).build();
//
//			customerLogsRepository.save(errorLog);
//
//			return null; // Skip this record
//		}
//
//		return item;

		try {
			// Validate and handle DOB
			if (item.getDob() == null || item.getDob().trim().isEmpty()) {
				throw new IllegalArgumentException("DOB is missing for Customer ID: " + item.getCustomerUniqueId());
			}

			// Validate and handle Contact Number
			if (item.getContactNo() == 0) {
				throw new IllegalArgumentException(
						"Contact number is missing for Customer ID: " + item.getCustomerUniqueId());
			}

			return item;
		} catch (Exception e) {
			// Log error details
			CustomerLogs log = CustomerLogs.builder().customerUniqueId(item.getCustomerUniqueId())
					.firstName(item.getFirstName()).lastName(item.getLastName()).email(item.getEmail())
					.gender(item.getGender())
					.contactNo(item.getContactNo() == 0 ? null : String.valueOf(item.getContactNo()))
					.country(item.getCountry()).dob(item.getDob()).reason(e.getMessage())
					.csvRowId(String.valueOf(item.getCustomerUniqueId())).build();

			// Log the error
			logger.error("Error processing customer: {}", log, e);

			// Save the error log
			customerLogsRepository.save(log);

			// Return null to filter out the item
			return null;
		}
	}

}
