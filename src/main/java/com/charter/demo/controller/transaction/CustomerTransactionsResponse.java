package com.charter.demo.controller.transaction;

import java.util.List;

import com.charter.demo.customer.CustomerTransaction;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomerTransactionsResponse {

    private final List<CustomerTransactionDto> customerTransactions;
}
