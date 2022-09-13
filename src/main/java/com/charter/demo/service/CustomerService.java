package com.charter.demo.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import com.charter.demo.controller.customer.CreateCustomerRequest;
import com.charter.demo.controller.customer.CustomerPointsResponse;
import com.charter.demo.customer.Customer;
import com.charter.demo.customer.CustomerTransaction;
import com.charter.demo.exception.DemoNotFoundException;
import com.charter.demo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerTransactionService customerTransactionService;

    public Customer findCustomer(final long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new DemoNotFoundException(String.format("Customer with id={%s} not found!", customerId)));
    }

    public Customer createCustomer(final CreateCustomerRequest request) {
        final Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setSurname(request.getSurname());

        return customerRepository.save(customer);
    }

    public CustomerPointsResponse getCustomerPoints(final long customerId) {
        final Customer customer = findCustomer(customerId);
        final List<CustomerTransaction> customerTransactions = customerTransactionService.findAllTransactionsByCustomerWithPointsGreaterThanZero(customer);
        final Month currentMonth = LocalDate.now().getMonth();

        final CustomerPointsResponse response = new CustomerPointsResponse();
        response.setCurrentMonthPoints(getCustomerPointsForGivenMonth(customerTransactions, currentMonth.getValue()));
        response.setThreeMonthsPeriodPoints(getCustomerPointsForGivenMonth(customerTransactions, currentMonth.minus(2).getValue()));

        return response;
    }

    private int getCustomerPointsForGivenMonth(final List<CustomerTransaction> customerTransactions, final int monthValue) {
        return customerTransactions.stream()
            .filter(transaction -> transaction.getCreateDate().getMonth().getValue() >= monthValue)
            .map(CustomerTransaction::getPoints)
            .reduce(0, Integer::sum);
    }
}
