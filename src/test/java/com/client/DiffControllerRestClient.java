/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.client;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

@Ignore
/**
 * Rest Client to test API when server is started
 */
public class DiffControllerRestClient {

    private String url = "http://localhost:8080/v1/diff/";

    private static RestTemplate restTemplate;

    @BeforeClass
    public static void init() {
        restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(2))
                .errorHandler(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
                        return false;
                    }

                    @Override
                    public void handleError(ClientHttpResponse httpResponse) throws IOException {
//                        throw new IOException(CharStreams.toString(new InputStreamReader(httpResponse.getBody(), Charsets.UTF_8)));
                    }
                })
                .build();
    }

    @Test
    public void submit_NotJson() {
        String body = "a";
        ResponseEntity<String> res = restTemplate.postForEntity(url+"1/left", body, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 415);
    }

    @Test
    public void submit_Null() {
        HttpEntity<String> entity = getEntity(null);
        ResponseEntity<String> res = restTemplate.postForEntity(url+"1/left", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 400);
    }

    @Test
    public void submit_Doc() {
        HttpEntity<String> entity = getEntity("{\"a\":1}");

        ResponseEntity<String> res = restTemplate.postForEntity(url+"2/left", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);
    }

    @Test
    public void compare_Doc() {
        HttpEntity<String> entity = getEntity("something");

        ResponseEntity<String> res = restTemplate.postForEntity(url+"3/left", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);

        res = restTemplate.postForEntity(url+"3/right", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);

        res = restTemplate.getForEntity(url+"3", String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);
        Assert.assertEquals(res.getBody(), "{\"status\":\"Equals\"}");
    }

    @Test
    public void compare_OnlyOneDoc() {

        HttpEntity<String> entity = getEntity("something");

        ResponseEntity<String> res = restTemplate.postForEntity(url+"4/left", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);

        res = restTemplate.getForEntity(url+"4", String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 400);
        Assert.assertEquals(res.getBody(), "Both left and right documents should be provided");
    }

    private HttpEntity<String> getEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

}
