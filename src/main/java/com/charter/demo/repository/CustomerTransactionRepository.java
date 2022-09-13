package com.charter.demo.repository;

import java.math.BigDecimal;
import java.util.List;

import com.charter.demo.customer.Customer;
import com.charter.demo.customer.CustomerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerTransactionRepository extends JpaRepository<CustomerTransaction, Long> {

    List<CustomerTransaction> findAllByCustomer(final Customer customer);

    List<CustomerTransaction> findAllByCustomerAndPointsGreaterThan(final Customer customer, final int points);
}
