package com.charter.demo.controller.customer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class CreateCustomerRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Size(min = 2, max = 100)
    private String surname;

    public void setName(final String name) {
        this.name = name.trim();
    }

    public void setSurname(final String surname) {
        this.surname = surname.trim();
    }
}
