package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.demo.entity.Customer;
import com.example.demo.helper.ValidationResult;

@Service
public class CustomerValidationService {

	public ValidationResult validate(Customer customer) {
		List<String> errors = new ArrayList<>();

		if (customer.getCustomerUniqueId() == null || customer.getCustomerUniqueId() <= 0) {
			errors.add("Invalid Customer Unique ID");
		}

		if (StringUtils.isEmpty(customer.getFirstName())) {
			errors.add("First Name is required");
		}

		if (customer.getDob() == null) {
			errors.add("Date of Birth is required");
		}

		return new ValidationResult(errors.isEmpty(), errors);
	}

}
