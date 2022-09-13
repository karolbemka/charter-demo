package com.charter.demo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.charter.demo.controller.transaction.CustomerTransactionDto;
import com.charter.demo.customer.CustomerTransaction;

public class CustomerTransactionFactory {

    public static CustomerTransaction createTransaction(final int points, final OffsetDateTime date) {
        final CustomerTransaction transaction = new CustomerTransaction();
        transaction.setPoints(points);
        transaction.setCreateDate(date);

        return transaction;
    }

    public static CustomerTransactionDto createTransactionDto(final long id, final int points, final BigDecimal amount) {
        final CustomerTransactionDto dto = new CustomerTransactionDto();
        dto.setId(id);
        dto.setPoints(points);
        dto.setAmount(amount);
        dto.setCreateDate(OffsetDateTime.now(ZoneOffset.UTC));
        return dto;
    }
}
