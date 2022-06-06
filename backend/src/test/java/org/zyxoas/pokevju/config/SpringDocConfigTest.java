package org.zyxoas.pokevju.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SpringDocConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;

    @Test
    void testSwaggerAvailable() throws Exception {
        mockMvc
                .perform(get(swaggerPath))
                .andExpect(status().isFound()); //If it redirects then Swagger is correctly configured
    }
}