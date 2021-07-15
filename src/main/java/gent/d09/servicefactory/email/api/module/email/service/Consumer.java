package gent.d09.servicefactory.email.api.module.email.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gent.d09.servicefactory.email.api.module.common.service.Tracer;
import gent.d09.servicefactory.email.api.container.QueueConfig;
import gent.d09.servicefactory.email.api.module.email.domain.event.EmailStatusEvent;
import gent.d09.servicefactory.email.api.module.email.domain.event.Event;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.jms.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class Consumer implements Runnable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ConnectionFactory connectionFactory;
    private final ExecutorService scheduler;
    private final EmailService emailService;
    private final QueueConfig queueConfig;

    public Consumer(ConnectionFactory connectionFactory, EmailService emailService, QueueConfig queueConfig) {
        this.connectionFactory = connectionFactory;
        this.emailService = emailService;
        this.scheduler = Executors.newSingleThreadExecutor();
        this.queueConfig = queueConfig;
    }

    @Override
    public void run() {
        creationStatusConsumer();
    }

    void onStart(@Observes StartupEvent ev) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    private void creationStatusConsumer() {
        createConsumer(queueConfig.getPrefix() + "-" + queueConfig.getStatus(), EmailStatusEvent.class, event -> {
            log.info("Received status event for email entity with id " + event.getId());
            emailService.handleStatusEvent((EmailStatusEvent) event);
        });
    }

    private void createConsumer(String topic, Class<?> eventClass, EventHandler handler) {
        JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
        JMSConsumer consumer = context.createConsumer(context.createTopic(topic));
        consumer.setMessageListener(msg -> {
            try {
                Event event = (Event) new ObjectMapper().readValue(msg.getBody(String.class), eventClass);
                Tracer.setCorrelationId(event.getCorrelationId());
                CompletableFuture.runAsync(() -> {
                    handler.handle(event);
                });
            } catch(JsonProcessingException | JMSException e) {
                log.error("Failed to parse event: " + e.getMessage());
            }
        });
    }

    private interface EventHandler {
        void handle(Event event);
    }
}
