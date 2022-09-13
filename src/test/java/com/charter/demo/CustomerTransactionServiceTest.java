package com.charter.demo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.charter.demo.controller.transaction.CustomerTransactionDto;
import com.charter.demo.controller.transaction.EditTransactionRequest;
import com.charter.demo.customer.Customer;
import com.charter.demo.customer.CustomerTransaction;
import com.charter.demo.exception.DemoNotFoundException;
import com.charter.demo.repository.CustomerTransactionRepository;
import com.charter.demo.service.CustomerTransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class CustomerTransactionServiceTest {

    @Mock
    private CustomerTransactionRepository customerTransactionRepository;

    @InjectMocks
    private CustomerTransactionService customerTransactionService;

    public static Stream<Arguments> provideTransactionAmountAndExpectedPoints() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(0), 0),
            Arguments.of(BigDecimal.valueOf(1), 0),
            Arguments.of(BigDecimal.valueOf(50.99), 0),
            Arguments.of(BigDecimal.valueOf(51), 1),
            Arguments.of(BigDecimal.valueOf(51.99), 1),
            Arguments.of(BigDecimal.valueOf(99), 49),
            Arguments.of(BigDecimal.valueOf(100), 50),
            Arguments.of(BigDecimal.valueOf(100.99), 50),
            Arguments.of(BigDecimal.valueOf(101), 52),
            Arguments.of(BigDecimal.valueOf(120), 90),
            Arguments.of(BigDecimal.valueOf(300), 450)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTransactionAmountAndExpectedPoints")
    void shouldCreateAndSaveTransactionAndCalculatePointsCorrectly(final BigDecimal transactionAmount, final int expectedPoints) {
        //given
        ReflectionTestUtils.setField(customerTransactionService, "firstLimit", 50);
        ReflectionTestUtils.setField(customerTransactionService, "secondLimit", 100);
        ReflectionTestUtils.setField(customerTransactionService, "firstLimitPoints", 1);
        ReflectionTestUtils.setField(customerTransactionService, "secondLimitPoints", 2);

        final Customer customer = new Customer();

        //when
        customerTransactionService.createTransaction(customer, transactionAmount);

        //then
        final ArgumentCaptor<CustomerTransaction> captor = ArgumentCaptor.forClass(CustomerTransaction.class);

        verify(customerTransactionRepository).save(captor.capture());

        final CustomerTransaction capturedTransaction = captor.getValue();

        assertThat(capturedTransaction.getCustomer()).isEqualTo(customer);
        assertThat(capturedTransaction.getAmount()).isEqualTo(transactionAmount);
        assertThat(capturedTransaction.getPoints()).isEqualTo(expectedPoints);
    }

    @Test
    void shouldCallRepositoryForAllCustomerTransactions() {
        //given
        final Customer customer = mock(Customer.class);

        final CustomerTransaction transaction = new CustomerTransaction();
        transaction.setId(3L);
        transaction.setCustomer(customer);
        transaction.setPoints(5);
        transaction.setAmount(BigDecimal.valueOf(10));
        transaction.setCreateDate(OffsetDateTime.now(ZoneOffset.UTC));

        when(customerTransactionRepository.findAllByCustomer(customer)).thenReturn(Collections.singletonList(transaction));

        //when
        final List<CustomerTransactionDto> result = customerTransactionService.findAllTransactionsByCustomer(customer);
        //then
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldCallRepositoryForCustomerTransactionsWithPointsGreaterThanZero() {
        //when
        customerTransactionService.findAllTransactionsByCustomerWithPointsGreaterThanZero(mock(Customer.class));
        //then
        verify(customerTransactionRepository).findAllByCustomerAndPointsGreaterThan(any(Customer.class), eq(0));
    }

    @Test
    void shouldEditTransactionWhenTransactionExists() {
        //given
        ReflectionTestUtils.setField(customerTransactionService, "firstLimit", 50);
        ReflectionTestUtils.setField(customerTransactionService, "secondLimit", 100);
        ReflectionTestUtils.setField(customerTransactionService, "firstLimitPoints", 1);
        ReflectionTestUtils.setField(customerTransactionService, "secondLimitPoints", 2);

        final EditTransactionRequest request = new EditTransactionRequest();
        request.setTransactionId(1L);
        request.setAmount(BigDecimal.valueOf(120L));
        request.setCreateDate(OffsetDateTime.now(ZoneOffset.UTC));
        final CustomerTransaction existingTransaction = new CustomerTransaction();
        existingTransaction.setAmount(BigDecimal.ZERO);
        existingTransaction.setPoints(0);

        when(customerTransactionRepository.findById(request.getTransactionId())).thenReturn(Optional.of(existingTransaction));

        //when && then
        assertThatCode(() -> customerTransactionService.editExistingTransaction(request)).doesNotThrowAnyException();
        final ArgumentCaptor<CustomerTransaction> captor = ArgumentCaptor.forClass(CustomerTransaction.class);
        verify(customerTransactionRepository).save(captor.capture());

        final CustomerTransaction capturedTransaction = captor.getValue();

        assertThat(capturedTransaction.getPoints()).isEqualTo(90);
        assertThat(capturedTransaction.getAmount()).isEqualTo(BigDecimal.valueOf(120));
        assertThat(capturedTransaction.getCreateDate()).isEqualTo(request.getCreateDate());
    }

    @Test
    void shouldThrowExceptionWhenTransactionDoesNotExist() {
        //given
        final EditTransactionRequest request = new EditTransactionRequest();
        request.setTransactionId(1L);

        //when
        when(customerTransactionRepository.findById(request.getTransactionId())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> customerTransactionService.editExistingTransaction(request))
            .isInstanceOf(DemoNotFoundException.class)
            .hasMessage("Transaction with id {1} not found!");
    }
}