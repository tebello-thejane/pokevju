package org.zyxoas.pokevju.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.zyxoas.pokevju.component.ApiComponent;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiComponent apiComponent;

    @Test
    void testGetNames() throws Exception {
        mockMvc.perform(get("/api/v1/allnames")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getPictureUrlByName() throws Exception {
        apiComponent.getIdMap().put("slowpoke", 1);

        mockMvc.perform(get("/api/v1/sprite/slowpoke")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith("image/svg+xml"));

        mockMvc.perform(get("/api/sprite/fastpuke")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}