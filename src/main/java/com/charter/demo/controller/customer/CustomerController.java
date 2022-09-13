package com.charter.demo.controller.customer;

import javax.validation.Valid;

import com.charter.demo.customer.Customer;
import com.charter.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/customer", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{customerId:\\d+}")
    public ResponseEntity<Customer> getCustomer(@PathVariable final long customerId) {
        Customer customer = customerService.findCustomer(customerId);
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateCustomerResponse> createCustomer(@Valid @RequestBody final CreateCustomerRequest request) {
        Long newId = customerService.createCustomer(request).getId();
        return ResponseEntity.ok(new CreateCustomerResponse(newId));
    }

    @GetMapping("/{customerId:\\d+}/points")
    public ResponseEntity<CustomerPointsResponse> getCustomerPoints(@PathVariable final long customerId) {
        final CustomerPointsResponse response = customerService.getCustomerPoints(customerId);
        return ResponseEntity.ok(response);
    }
}
