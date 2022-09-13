package com.charter.demo.controller.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.charter.demo.customer.CustomerTransaction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerTransactionDto {

    private Long id;
    private BigDecimal amount;
    private int points;
    private OffsetDateTime createDate;

    public static CustomerTransactionDto fromCustomerTransactionEntity(final CustomerTransaction customerTransaction) {
        final CustomerTransactionDto dto = new CustomerTransactionDto();
        dto.setId(customerTransaction.getId());
        dto.setAmount(customerTransaction.getAmount());
        dto.setPoints(customerTransaction.getPoints());
        dto.setCreateDate(customerTransaction.getCreateDate());
        return dto;
    }
}
