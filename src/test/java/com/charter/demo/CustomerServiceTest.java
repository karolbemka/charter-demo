package com.charter.demo;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.charter.demo.controller.customer.CreateCustomerRequest;
import com.charter.demo.controller.customer.CustomerPointsResponse;
import com.charter.demo.customer.Customer;
import com.charter.demo.customer.CustomerTransaction;
import com.charter.demo.exception.DemoNotFoundException;
import com.charter.demo.repository.CustomerRepository;
import com.charter.demo.service.CustomerService;
import com.charter.demo.service.CustomerTransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.charter.demo.CustomerTransactionFactory.createTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerTransactionService customerTransactionService;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldFindCustomerById() {
        final long customerId = 1L;
        final Customer customer = new Customer();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        //when
        final Customer result = customerService.findCustomer(customerId);

        //then
        assertThat(result).isEqualTo(customer);
    }

    @Test
    void shouldThrowExceptionWhenCustomerIsNotFound() {
        //given
        final long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> customerService.findCustomer(customerId))
            .isInstanceOf(DemoNotFoundException.class)
            .hasMessage("Customer with id={1} not found!");
    }

    @Test
    void shouldCreateCustomer() {
        //given
        final CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("Joe");
        request.setSurname("Biden");

        //when
        customerService.createCustomer(request);

        //then
        final ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);

        verify(customerRepository).save(captor.capture());

        final Customer savedCustomer = captor.getValue();

        assertThat(savedCustomer.getName()).isEqualTo(request.getName());
        assertThat(savedCustomer.getSurname()).isEqualTo(request.getSurname());
    }

    @Test
    void shouldReturnTransactionsForGivenCustomerId() {
        //given
        final long customerId = 1L;
        final CustomerTransaction t1 = createTransaction(50, OffsetDateTime.now());
        final CustomerTransaction t2 = createTransaction(100, OffsetDateTime.now().minusMonths(1));
        final CustomerTransaction t3 = createTransaction(150, OffsetDateTime.now().minusMonths(2));
        //transactions outside 3 months period window
        final CustomerTransaction t4 = createTransaction(50, OffsetDateTime.now().minusMonths(3));
        final CustomerTransaction t5 = createTransaction(50, OffsetDateTime.now().minusMonths(4));

        final List<CustomerTransaction> transactions = Stream.of(t1, t2, t3, t4, t5).collect(Collectors.toList());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mock(Customer.class)));
        when(customerTransactionService.findAllTransactionsByCustomerWithPointsGreaterThanZero(any(Customer.class)))
            .thenReturn(transactions);

        //when
        final CustomerPointsResponse result = customerService.getCustomerPoints(customerId);

        //then
        assertThat(result.getCurrentMonthPoints()).isEqualTo(50);
        assertThat(result.getThreeMonthsPeriodPoints()).isEqualTo(300);
    }
}