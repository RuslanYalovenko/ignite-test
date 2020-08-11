/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.docker;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DockerTest {

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

    @ClassRule
    public static GenericContainer webServer
            = new GenericContainer("test/ignite-test:latest")
            .withExposedPorts(8080)
            .withCommand("java","-jar","app.jar");
    

    @Test
    public void submit_not_json() {
        String body = "a";
        ResponseEntity<String> res = restTemplate.postForEntity(getUrl()+"1/left", body, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 415);
    }

    @Test
    public void submit_DocNull() {
        HttpEntity<String> entity = getEntity(null);
        ResponseEntity<String> res = restTemplate.postForEntity(getUrl()+"1/left", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 400);
    }

    @Test
    public void submit_DocOk() {
        HttpEntity<String> entity = getEntity("{\"a\":1}");

        ResponseEntity<String> res = restTemplate.postForEntity(getUrl()+"2/left", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);
    }

    @Test
    public void compare_DocEquals() {
        HttpEntity<String> entity = getEntity("something");

        ResponseEntity<String> res = restTemplate.postForEntity(getUrl()+"3/left", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);

        res = restTemplate.postForEntity(getUrl()+"3/right", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);

        res = restTemplate.getForEntity(getUrl()+"3", String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);
        Assert.assertEquals(res.getBody(), "{\"status\":\"Equals\"}");
    }

    @Test
    public void compare_DocDiff() {
        ResponseEntity<String> res = restTemplate.postForEntity(getUrl()+"4/left", getEntity("1231111133"), String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);

        res = restTemplate.postForEntity(getUrl()+"4/right", getEntity("1233331122"), String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);

        res = restTemplate.getForEntity(getUrl()+"4", String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);
        Assert.assertEquals(res.getBody(), "{\"status\":\"Different\",\"offset\":{\"8\":2,\"3\":3}}");
    }

    @Test
    public void compare_OnlyOneDoc() {

        HttpEntity<String> entity = getEntity("something");

        ResponseEntity<String> res = restTemplate.postForEntity(getUrl()+"5/left", entity, String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 200);

        res = restTemplate.getForEntity(getUrl()+"5", String.class);
        Assert.assertEquals(res.getStatusCodeValue(), 400);
        Assert.assertEquals(res.getBody(), "Both left and right documents should be provided");
    }

    private String getUrl() {
        return "http://"
                + webServer.getContainerIpAddress()
                + ":" + webServer.getMappedPort(8080)
                + "/v1/diff/";
    }

    private HttpEntity<String> getEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
