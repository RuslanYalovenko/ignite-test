/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.Queue;

@Component
public class DiffProducer {

    private static final Logger LOG = LoggerFactory.getLogger(DiffProducer.class);

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    private Queue queue;

    @PostConstruct
    public void init() {
        queue = new ActiveMQQueue("diff.queue");
    }

    /**
     * Sends diffId to a queue. Consumer will run diffService.compare method for diffId
     * @param diffId
     */
    public void send(long diffId) {
        try {
            jmsMessagingTemplate.convertAndSend(queue, diffId);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

}
