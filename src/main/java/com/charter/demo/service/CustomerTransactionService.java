package com.charter.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.charter.demo.controller.transaction.CustomerTransactionDto;
import com.charter.demo.controller.transaction.EditTransactionRequest;
import com.charter.demo.customer.Customer;
import com.charter.demo.customer.CustomerTransaction;
import com.charter.demo.exception.DemoNotFoundException;
import com.charter.demo.repository.CustomerTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerTransactionService {

    @Value("${demoapp.points.firstLimit:50}")
    private int firstLimit;

    @Value("${demoapp.points.secondLimit:100}")
    private int secondLimit;

    @Value("${demoapp.points.firstLimitPoints:1}")
    private int firstLimitPoints;

    @Value("${demoapp.points.secondLimitPoints:2}")
    private int secondLimitPoints;

    private final CustomerTransactionRepository customerTransactionRepository;

    public void createTransaction(final Customer customer, final BigDecimal transactionAmount) {
        final int grantedPoints = calculatePointsForTransaction(transactionAmount);

        final CustomerTransaction transaction = new CustomerTransaction();
        transaction.setCustomer(customer);
        transaction.setAmount(transactionAmount);
        transaction.setPoints(grantedPoints);

        customerTransactionRepository.save(transaction);
    }

    public List<CustomerTransactionDto> findAllTransactionsByCustomer(final Customer customer) {
        return customerTransactionRepository.findAllByCustomer(customer).stream()
            .map(CustomerTransactionDto::fromCustomerTransactionEntity)
            .collect(Collectors.toList());
    }

    public List<CustomerTransaction> findAllTransactionsByCustomerWithPointsGreaterThanZero(final Customer customer) {
        return customerTransactionRepository.findAllByCustomerAndPointsGreaterThan(customer, 0);
    }

    public void editExistingTransaction(final EditTransactionRequest request) {
        final CustomerTransaction existingTransaction = findTransactionById(request.getTransactionId());

        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setPoints(calculatePointsForTransaction(request.getAmount()));
        existingTransaction.setCreateDate(request.getCreateDate());

        customerTransactionRepository.save(existingTransaction);
    }

    private CustomerTransaction findTransactionById(final long transactionId) {
        return customerTransactionRepository.findById(transactionId)
            .orElseThrow(() -> new DemoNotFoundException(String.format("Transaction with id {%s} not found!", transactionId)));
    }

private int calculatePointsForTransaction(final BigDecimal transactionAmount) {
    final int amountAboveFirstLimit = calculateAmountAboveFirstLimit(transactionAmount);

    final int limitsDifference = secondLimit - firstLimit;

    if (transactionAmount.intValue() > secondLimit) {
        final int amountAboveSecondLimit = amountAboveFirstLimit - limitsDifference;
        return limitsDifference * firstLimitPoints + amountAboveSecondLimit * secondLimitPoints;
    }
    return amountAboveFirstLimit * firstLimitPoints;
}

    private int calculateAmountAboveFirstLimit(final BigDecimal transactionAmount) {
        final int amountAboveLimit = transactionAmount.intValue() - firstLimit;
        return Math.max(amountAboveLimit, 0);
    }
}
