package com.polarbookshop.dispatcherservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class) //Configure the test binder
public class FunctionsStreamIntegrationTests {

    @Autowired
    private InputDestination input; //Represents the input binding packlabel-in-0
    @Autowired
    private OutputDestination output; //Represents the output binding packlabel-out-0
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenOrderAcceptedThenDispatched() throws IOException {
        long orderId = 121;
        Message<OrderAcceptedMessage> inputMessage = MessageBuilder
                .withPayload(new OrderAcceptedMessage(orderId)).build();
        Message<OrderDispatchedMessage> expectedOutputMessage = MessageBuilder
                .withPayload(new OrderDispatchedMessage(orderId)).build();

        this.input.send(inputMessage);

        byte[] outputMessage = output.receive().getPayload();
        OrderDispatchedMessage orderDispatchedMessage =
                objectMapper.readValue(outputMessage, OrderDispatchedMessage.class);
        assertThat(orderDispatchedMessage).isEqualTo(expectedOutputMessage.getPayload());

    }

}
