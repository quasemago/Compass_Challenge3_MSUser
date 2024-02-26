package com.compassuol.sp.challenge.msuser.infra;

import com.compassuol.sp.challenge.msuser.infra.mqueue.enums.EventType;
import com.compassuol.sp.challenge.msuser.infra.mqueue.publisher.UserRequestNotificationPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RabbitMQNotificationPublisherTest {
    @InjectMocks
    private UserRequestNotificationPublisher publisher;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private Queue userQueue;

    @Test
    public void sendNotification_WithValidData() throws JsonProcessingException {
        when(userQueue.getName()).thenReturn("userQueue");
        publisher.sendNotification("teste@hotmail.com", EventType.CREATE);

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), any(Object.class));
    }
}
