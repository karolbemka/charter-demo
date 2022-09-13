package com.charter.demo.controller.transaction;

import java.util.List;
import javax.validation.Valid;

import com.charter.demo.customer.Customer;
import com.charter.demo.customer.CustomerTransaction;
import com.charter.demo.service.CustomerService;
import com.charter.demo.service.CustomerTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/transaction")
@RequiredArgsConstructor
public class CustomerTransactionController {

    private final CustomerService customerService;
    private final CustomerTransactionService customerTransactionService;

    @PostMapping("/create")
    public ResponseEntity<Void> createTransaction(@Valid @RequestBody final CreateTransactionRequest request) {
        final Customer customer = customerService.findCustomer(request.getCustomerId());
        customerTransactionService.createTransaction(customer, request.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{customerId:\\d+}")
    public ResponseEntity<CustomerTransactionsResponse> findAllTransactionsByCustomer(@PathVariable final long customerId) {
        final Customer customer = customerService.findCustomer(customerId);
        final List<CustomerTransactionDto> customerTransactions = customerTransactionService.findAllTransactionsByCustomer(customer);

        return ResponseEntity.ok(CustomerTransactionsResponse.builder().customerTransactions(customerTransactions).build());
    }

    @PutMapping("/edit")
    public ResponseEntity<Void> editTransaction(@Valid @RequestBody final EditTransactionRequest request) {
        customerTransactionService.editExistingTransaction(request);
        return ResponseEntity.ok().build();
    }
}
