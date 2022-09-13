package com.charter.demo;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.charter.demo.controller.transaction.CreateTransactionRequest;
import com.charter.demo.controller.transaction.CustomerTransactionController;
import com.charter.demo.controller.transaction.CustomerTransactionDto;
import com.charter.demo.controller.transaction.EditTransactionRequest;
import com.charter.demo.customer.Customer;
import com.charter.demo.exception.DemoNotFoundException;
import com.charter.demo.service.CustomerService;
import com.charter.demo.service.CustomerTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerTransactionController.class)
@ExtendWith({MockitoExtension.class})
class CustomerTransactionControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerTransactionService customerTransactionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenValidRequestShouldCreateTransactionAndReturnStatusOk() throws Exception {
        //given
        final Customer customer = new Customer();
        customer.setName("Joe");
        customer.setSurname("Biden");
        customer.setId(1L);

        final CreateTransactionRequest request = new CreateTransactionRequest();
        request.setCustomerId(1L);
        request.setAmount(BigDecimal.valueOf(100));

        when(customerService.findCustomer(1)).thenReturn(customer);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post("/api/customer/transaction/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request));

        //when && then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk());

        verify(customerTransactionService).createTransaction(customer, request.getAmount());
    }

    @Test
    void givenInvalidCustomerIdShouldNotFoundWhenCreatingTransaction() throws Exception {
        //given
        final String errorMessage = "customer not found";

        final CreateTransactionRequest request = new CreateTransactionRequest();
        request.setCustomerId(1L);

        when(customerService.findCustomer(request.getCustomerId())).thenThrow(new DemoNotFoundException(errorMessage));

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post("/api/customer/transaction/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request));

        //when && then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorMessage", is(equalTo(errorMessage))));
    }

    @Test
    void givenValidCustomerIdShouldReturnListOfCustomerTransactions() throws Exception {
        //given
        final long customerId = 1L;
        final Customer customer = mock(Customer.class);
        final CustomerTransactionDto dto1 = CustomerTransactionFactory.createTransactionDto(1L, 10, BigDecimal.valueOf(20));
        final CustomerTransactionDto dto2 = CustomerTransactionFactory.createTransactionDto(2L, 300, BigDecimal.valueOf(500.99));

        when(customerService.findCustomer(customerId)).thenReturn(customer);
        when(customerTransactionService.findAllTransactionsByCustomer(customer)).thenReturn(Arrays.asList(dto1, dto2));

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/api/customer/transaction/1");

        //when && then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerTransactions[0].id").value(1))
            .andExpect(jsonPath("$.customerTransactions[0].amount").value(20))
            .andExpect(jsonPath("$.customerTransactions[0].points").value(10))
            .andExpect(jsonPath("$.customerTransactions[0].createDate", is(equalTo(dto1.getCreateDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))));
    }

    @Test
    void givenValidRequestShouldRespondWithOkStatusWhenEditingTransaction() throws Exception {
        //given
        final EditTransactionRequest request = new EditTransactionRequest();
        request.setTransactionId(1L);
        request.setAmount(BigDecimal.valueOf(999));

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put("/api/customer/transaction/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request));

        //when && then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequestShouldRespondWithErrorMessage() throws Exception {
        final EditTransactionRequest request = new EditTransactionRequest();
        request.setTransactionId(null);
        request.setAmount(BigDecimal.valueOf(0));

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put("/api/customer/transaction/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request));

        //when && then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorMessage", containsString("field {transactionId} has failed validation due to {must not be null}")))
            .andExpect(jsonPath("$.errorMessage", containsString("field {amount} has failed validation due to {must be greater than 0}")));
    }

    @Test
    void whenUnexpectedErrorIsThrownThenExceptionShouldBeHandledAndProperMessageReturned() throws Exception {
        when(customerService.findCustomer(1L)).thenThrow(new RuntimeException("unexpected exception"));

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/api/customer/transaction/1");

        mockMvc.perform(requestBuilder)
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorMessage", is(equalTo("Internal Error"))));
    }
}