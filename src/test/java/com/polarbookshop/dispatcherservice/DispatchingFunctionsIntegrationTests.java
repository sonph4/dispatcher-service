package com.polarbookshop.dispatcherservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalSpringBootTest
@Disabled("These tests are only necessary when using the functions alone (no bindings)")
public class DispatchingFunctionsIntegrationTests {
    @Autowired
    private FunctionCatalog functionCatalog;

    @Test
    void packOrder(){
        Function<OrderAcceptedMessage, Long> packFunction =
                functionCatalog.lookup(Function.class, "pack");
        long orderId = 121;
        assertThat(packFunction.apply(new OrderAcceptedMessage(orderId))).isEqualTo(orderId);
    }

    @Test
    void labelOrder(){
        Function<Flux<Long>, Flux<OrderDispatchedMessage>> labelFunction =
                functionCatalog.lookup(Function.class, "label");
        Flux<Long> orderId = Flux.just(121L);
        StepVerifier.create(labelFunction.apply(orderId))
                .expectNextMatches(orderDispatchedMessage ->
                        orderDispatchedMessage.equals(new OrderDispatchedMessage(121L)))
                .verifyComplete();
    }

    @Test
    void packAndLabelOrder() {
        Function<OrderAcceptedMessage, Flux<OrderDispatchedMessage>>
                packAndLabel = functionCatalog.lookup(Function.class, "pack|label");
        long orderId = 121;
        StepVerifier.create(packAndLabel.apply(new OrderAcceptedMessage(orderId)))
                .expectNextMatches(dispatchedOrder ->
                        dispatchedOrder.equals(new OrderDispatchedMessage(orderId)))
                .verifyComplete();
    }
}
