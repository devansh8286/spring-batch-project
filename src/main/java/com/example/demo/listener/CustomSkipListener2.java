package com.example.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerLogs;
import com.example.demo.repository.CustomerLogsRepository;

@Component
public class CustomSkipListener2 implements SkipListener<Customer, Customer> {

	private static final Logger logger = LoggerFactory.getLogger(CustomSkipListener2.class);

	@Autowired
	private CustomerLogsRepository customerLogsRepository;

	@Override
	public void onSkipInRead(Throwable t) {
		logger.error("Skip in read: ", t);

		if (t instanceof FlatFileParseException) {
			FlatFileParseException parseException = (FlatFileParseException) t;
			String input = parseException.getInput();
			String[] inputFields = input.split(",");

			CustomerLogs errorLog = createErrorLogFromInput(inputFields, parseException);
			customerLogsRepository.save(errorLog);
		}
	}

	@Override
	public void onSkipInProcess(Customer item, Throwable t) {
		logger.error("Skip in process for item: " + item, t);

		if (item != null) {
			CustomerLogs errorLog = CustomerLogs.builder().customerUniqueId(item.getCustomerUniqueId())
					.firstName(item.getFirstName()).lastName(item.getLastName()).email(item.getEmail())
					.gender(item.getGender())
					.contactNo(item.getContactNo() == 0 ? null : String.valueOf(item.getContactNo()))
					.country(item.getCountry()).dob(item.getDob()).reason("Processing error: " + t.getMessage())
					.csvRowId(String.valueOf(item.getCustomerUniqueId())).build();

			customerLogsRepository.save(errorLog);
		}
	}

	@Override
	public void onSkipInWrite(Customer item, Throwable t) {
		logger.error("Skip in write for item: " + item, t);

		CustomerLogs errorLog = CustomerLogs.builder().customerUniqueId(item.getCustomerUniqueId())
				.firstName(item.getFirstName()).lastName(item.getLastName()).email(item.getEmail())
				.gender(item.getGender())
				.contactNo(item.getContactNo() == 0 ? null : String.valueOf(item.getContactNo()))
				.country(item.getCountry()).dob(item.getDob()).reason("Write error: " + t.getMessage())
				.csvRowId(String.valueOf(item.getCustomerUniqueId())).build();

		customerLogsRepository.save(errorLog);
	}

	private CustomerLogs createErrorLogFromInput(String[] inputFields, FlatFileParseException parseException) {
		return CustomerLogs.builder().customerUniqueId(inputFields.length > 0 ? parseInputSafely(inputFields[0], 0) : 0)
				.firstName(inputFields.length > 1 ? inputFields[1].trim() : null)
				.lastName(inputFields.length > 2 ? inputFields[2].trim() : null)
				.email(inputFields.length > 3 ? inputFields[3].trim() : null)
				.gender(inputFields.length > 4 ? inputFields[4].trim() : null)
				.contactNo(inputFields.length > 5 ? inputFields[5].trim() : null)
				.country(inputFields.length > 6 ? inputFields[6].trim() : null)
				.dob(inputFields.length > 7 ? inputFields[7].trim() : null)
				.reason("Parsing error: " + parseException.getMessage())
				.csvRowId(String.valueOf(parseException.getLineNumber())).build();
	}

	private int parseInputSafely(String input, int defaultValue) {
		try {
			return Integer.parseInt(input.trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private String getValueFromRow(String[] rowData, int index) {
		if (rowData != null && index < rowData.length) {
			return rowData[index].trim();
		}
		return "N/A"; // Default value if the column is missing
	}

}
