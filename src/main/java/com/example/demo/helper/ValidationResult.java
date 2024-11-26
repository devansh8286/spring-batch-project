package com.example.demo.helper;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ValidationResult {

	private final boolean valid;
	private final List<String> errors;

}
