package com.example.demo.config;

import org.springframework.batch.item.ItemProcessor;

import com.example.demo.entity.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer item) throws Exception {

		return item;
	}

}
