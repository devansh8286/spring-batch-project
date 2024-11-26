package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMERS_INFO_LOGS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CUSTOMER_ID")
	private int id;

	@Column(name = "Customer_unique_id")
	private int customerUniqueId;

	@Column(name = "FIRST_NAME")
	private String firstName;
	@Column(name = "LAST_NAME")
	private String lastName;
	@Column(name = "EMAIL")
	private String email;
	@Column(name = "GENDER")
	private String gender;
	@Column(name = "CONTACT")
	private String contactNo;
	@Column(name = "COUNTRY")
	private String country;
	@Column(name = "DOB")
	private String dob;
	@Column(name = "reason")
	private String reason;
	@Column(name = "row_Id")
	private String csvRowId;
}
