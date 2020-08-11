/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.rest;

import com.entity.DiffResult;
import com.service.DiffService;
import com.util.DiffEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DiffController.class)
public class DiffControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DiffService diffService;

    @Test
    public void submit_doc_null() throws Exception {
        mvc.perform(post("/diff/1/left")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void submit_doc() throws Exception {
        doNothing().when(diffService).save(isA(Long.class), isA(DiffEnum.class), isA(String.class));
        doNothing().when(diffService).process(isA(Long.class));
        String body = "something";
        mvc.perform(post("/diff/2/left")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void compare_doc() throws Exception {
        when(diffService.getDiff(3)).thenReturn(new DiffResult("Equals"));

        String url = "/diff/3/";

        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"status\":\"Equals\"}"));
    }
}
