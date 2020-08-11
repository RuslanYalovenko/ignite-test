/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.rest;

import com.entity.DiffResult;
import com.exception.ValidationException;
import com.service.DiffService;
import com.util.DiffEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/diff")
public class DiffController {

    private static final Logger LOG = LoggerFactory.getLogger(DiffController.class);

    @Autowired
    private DiffService diffService;

    /**
     * Receives one document for comparison based on diffId and left/right identifier of document.
     * @param diffId Identifier of Documents pair to compare
     * @param itemId Document identifier (left, right)
     * @param body Document to compare
     * @throws ValidationException
     */
    @RequestMapping(value = "/{diffId}/{itemId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void submit(@PathVariable long diffId, @PathVariable DiffEnum itemId, @RequestBody String body) throws ValidationException {
        diffService.save(diffId, itemId, body);

        diffService.process(diffId);
    }

    /**
     * Returns result of documents comparison by diffId
     * @param diffId
     * @return
     * @throws ValidationException
     */
    @RequestMapping(value = "/{diffId}", method = RequestMethod.GET)
    public DiffResult compare(@PathVariable long diffId) throws ValidationException {
        return diffService.getDiff(diffId);
    }

}
