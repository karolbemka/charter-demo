package com.charter.demo.controller.customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class CreateCustomerResponse {

    private final Long customerId;
}
