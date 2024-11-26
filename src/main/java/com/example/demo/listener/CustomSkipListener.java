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
public class CustomSkipListener implements SkipListener<Customer, Customer> {

    private static final Logger logger = LoggerFactory.getLogger(CustomSkipListener.class);

    @Autowired
    private CustomerLogsRepository customerLogsRepository;

    @Override
    public void onSkipInRead(Throwable t) {
        logSkippedRecord(null, t, "READ");
    }

    @Override
    public void onSkipInProcess(Customer item, Throwable t) {
        logSkippedRecord(item, t, "PROCESS");
    }

    @Override
    public void onSkipInWrite(Customer item, Throwable t) {
        logSkippedRecord(item, t, "WRITE");
    }

    private void logSkippedRecord(Customer item, Throwable t, String stage) {
        try {
            // Create error log entry
            CustomerLogs errorLog = createErrorLog(item, t, stage);
            
            // Save error log
            customerLogsRepository.save(errorLog);
            
            // Log the skip details
            logger.error("Skipped record in {} stage. Error: {}", stage, t.getMessage());
        } catch (Exception e) {
            logger.error("Error logging skipped record", e);
        }
    }

    private CustomerLogs createErrorLog(Customer item, Throwable t, String stage) {
        // If item is null, create a minimal error log
        if (item == null) {
            return CustomerLogs.builder()
                .reason("Skip in " + stage + " stage: " + t.getMessage())
                .build();
        }

        // Create detailed error log
        return CustomerLogs.builder()
            .customerUniqueId(item.getCustomerUniqueId())
            .firstName(item.getFirstName())
            .lastName(item.getLastName())
            .email(item.getEmail())
            .gender(item.getGender())
            .contactNo(item.getContactNo() == 0 ? null : String.valueOf(item.getContactNo()))
            .country(item.getCountry())
            .dob(item.getDob())
            .reason("Skip in " + stage + " stage: " + t.getMessage())
            .csvRowId(String.valueOf(item.getCustomerUniqueId()))
            .build();
    }
}