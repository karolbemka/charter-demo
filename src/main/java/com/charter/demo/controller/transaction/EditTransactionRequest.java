package com.charter.demo.controller.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditTransactionRequest {

    @NotNull
    private Long transactionId;

    @Positive
    @Digits(integer = 14, fraction = 2)
    private BigDecimal amount;

    private OffsetDateTime createDate;
}
