package com.charter.demo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class DemoErrorResponse {

    private final String errorMessage;
}
