package com.charter.demo.controller.transaction;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTransactionRequest {

    @NotNull
    private Long customerId;

    @Positive
    @Digits(integer = 14, fraction = 2)
    private BigDecimal amount;
}
