package com.compassuol.sp.challenge.msuser.infra.mqueue.publisher;

import com.compassuol.sp.challenge.msuser.infra.mqueue.dto.UserRequestEventDTO;
import com.compassuol.sp.challenge.msuser.infra.mqueue.enums.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class UserRequestNotificationPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final Queue userQueue;

    public void sendNotification(String email, EventType eventType) throws JsonProcessingException {
        final Date now = new Date();
        final String json = convertDataIntoJson(new UserRequestEventDTO(email, eventType, now));
        rabbitTemplate.convertAndSend(userQueue.getName(), json);
    }

    private String convertDataIntoJson(UserRequestEventDTO data) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        return mapper.writeValueAsString(data);
    }
}
