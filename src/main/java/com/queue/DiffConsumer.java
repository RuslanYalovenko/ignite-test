/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.queue;

import com.exception.ValidationException;
import com.service.DiffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class DiffConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(DiffConsumer.class);

    @Autowired
    private DiffService diffService;

    @JmsListener(destination = "diff.queue")
    public void receive(String diffId) {
        try {
            diffService.compare(Long.valueOf(diffId));
        } catch (ValidationException e) {
            LOG.error(diffId + ": " + e.getMessage());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
