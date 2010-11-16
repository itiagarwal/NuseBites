package com.xebia;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageHandler;
import spring.test.TemporarySpringContext;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.integration.test.matcher.PayloadMatcher.hasPayload;

/**
 * @author iwein
 */
public class FullApplicationTest {
    @Rule
    public TemporarySpringContext context = new TemporarySpringContext("/TEST-nuse-application.xml");

    @Autowired PublishSubscribeChannel bites;

    @Test
    public void shouldLoadContext() throws Exception {
        //just checking if the context loads correctly
    }

    public static interface BitesAdapter{
        void takeBite(String bite);
    }

    @Autowired
    BitesAdapter bitesAdapter;

    @Test
    public void shouldAcceptBites() throws Exception {
        final AtomicReference<Message<?>> received = new AtomicReference<Message<?>>();
        bites.subscribe(new MessageHandler(){
            public void handleMessage(Message<?> message) throws MessagingException {
                received.set(message);
            }
        });
        bitesAdapter.takeBite("Eat this!");
        assertThat(((String) received.get().getPayload()), is("Eat this!"));
        assertThat(received.get(), hasPayload("Eat this!"));
    }
}
