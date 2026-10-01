package com.stephanie.webdev2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listReturnsTaskListView() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-list"));
    }

    @Test
    void missingTaskShowsNotFoundPage() throws Exception {
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/not-found"));
    }

    @Test
    void invalidFormRedisplaysWithErrors() throws Exception {
        mockMvc.perform(post("/tasks").param("title", "a"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-form"))
                .andExpect(model().attributeHasFieldErrors("task", "title"));
    }

    @Test
    void validFormRedirectsToList() throws Exception {
        mockMvc.perform(post("/tasks").param("title", "Buy milk"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }
}
