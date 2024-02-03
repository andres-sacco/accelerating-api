package com.twa.reservations;

import io.github.microcks.testcontainers.MicrocksContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiReservationsApplicationTests {

    private MockMvc mockMvc;

    private static final MicrocksContainer container = new MicrocksContainer(
            DockerImageName.parse("quay.io/microcks/microcks-uber:1.8.1"))
                    .withMainArtifacts("api-docs-with-examples.yaml").withExposedPorts(8080, 8080);

    @BeforeAll
    public static void setUp() {
        container.start();

    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("http-connector.hosts.api-catalog.port", container::getFirstMappedPort);
    }

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void should_create_a_reservation() throws Exception {

        mockMvc.perform(post("/reservation").content(
                "{\"passengers\":[{\"firstName\":\"Andres\",\"lastName\":\"Sacco\",\"documentNumber\":\"12345678\",\"documentType\":\"DNI\",\"birthday\":\"1985-01-01\"}],\"itinerary\":{\"segment\":[{\"origin\":\"BUE\",\"destination\":\"SCL\",\"departure\":\"2024-12-31\",\"arrival\":\"2025-01-01\",\"carrier\":\"AA\"}],\"price\":{\"totalPrice\":1,\"totalTax\":0,\"basePrice\":1}}}")
                .contentType("application/json")).andExpect(status().isCreated());
    }

}
