package com.charter.demo;

import com.charter.demo.controller.customer.CreateCustomerRequest;
import com.charter.demo.controller.customer.CustomerController;
import com.charter.demo.controller.customer.CustomerPointsResponse;
import com.charter.demo.customer.Customer;
import com.charter.demo.exception.DemoNotFoundException;
import com.charter.demo.service.CustomerService;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@ExtendWith({MockitoExtension.class})
class CustomerControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenCustomerIdShouldReturnStatusOk() throws Exception {
        //given
        final Customer customer = new Customer();
        customer.setName("Joe");
        customer.setSurname("Biden");

        when(customerService.findCustomer(1)).thenReturn(customer);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/api/customer/1");

        //when && then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(equalTo("Joe"))))
            .andExpect(jsonPath("$.surname", is(equalTo("Biden"))));
    }

    @Test
    void givenCustomerIdShouldReturnStatusNotFound() throws Exception {
        //given
        final String errorMessage = "customer not found";

        when(customerService.findCustomer(1)).thenThrow(new DemoNotFoundException(errorMessage));

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/api/customer/1");

        //when && then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorMessage", is(equalTo(errorMessage))));
    }

    @Test
    void givenValidCreateCustomerRequestShouldReturnStatusOk() throws Exception {
        //given
        final CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("Joe");
        request.setSurname("Biden");

        final Customer customer = new Customer();
        customer.setId(1L);

        when(customerService.createCustomer(any())).thenReturn(customer);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post("/api/customer/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request));

        //when && then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId", is(equalTo(1))));
    }

    @Test
    void givenValidCustomerIdShouldReturnCustomerPointsResponse() {
        //given
        final CustomerPointsResponse response = new CustomerPointsResponse();
        response.setCurrentMonthPoints(150);
        response.setThreeMonthsPeriodPoints(300);

        when(customerService.getCustomerPoints(1L)).thenReturn(response);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/api/customer/1/points");

        //when && then
        try {
            mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentMonthPoints", is(equalTo(150))))
                .andExpect(jsonPath("$.threeMonthsPeriodPoints", is(equalTo(300))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void givenInvalidCustomerIdShouldReturnNotFoundWithErrorMessage() throws Exception {
        //given
        final String errorMessage = "customer not found";

        when(customerService.getCustomerPoints(1)).thenThrow(new DemoNotFoundException(errorMessage));

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/api/customer/1/points");

        //when && then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorMessage", is(equalTo(errorMessage))));
    }
}