package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.DnaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para MutantController.
 * Total: 8 tests según consigna 3.2
 */
@SpringBootTest
@AutoConfigureMockMvc
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ========================================
    // TESTS ENDPOINT POST /mutant
    // ========================================

    @Test
    @DisplayName("POST /mutant debe retornar 200 OK cuando es mutante")
    void testMutantEndpoint_ReturnOk_WhenIsMutant() throws Exception {
        String[] mutantDna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        DnaRequest request = new DnaRequest(mutantDna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 403 Forbidden cuando NO es mutante")
    void testMutantEndpoint_ReturnForbidden_WhenIsNotMutant() throws Exception {
        String[] humanDna = {
            "ATGCGA",
            "CAGTGC",
            "TTATTT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };

        DnaRequest request = new DnaRequest(humanDna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 Bad Request con ADN inválido")
    void testMutantEndpoint_ReturnBadRequest_WhenDnaInvalid() throws Exception {
        String[] invalidDna = {
            "ATGC",
            "CXYZ",  // ← Caracteres inválidos
            "TTAT",
            "AGAA"
        };

        DnaRequest request = new DnaRequest(invalidDna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 cuando matriz no es NxN")
    void testMutantEndpoint_ReturnBadRequest_WhenNotSquareMatrix() throws Exception {
        String[] nonSquareDna = {
            "ATGCGA",
            "CAGT",    // ← Fila más corta
            "TTATGT",
            "AGAAGG"
        };

        DnaRequest request = new DnaRequest(nonSquareDna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // TESTS ENDPOINT GET /stats
    // ========================================

    @Test
    @DisplayName("GET /stats debe retornar 200 OK con estructura JSON correcta")
    void testStatsEndpoint_ReturnOk_WithCorrectStructure() throws Exception {
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count_mutant_dna").isNumber())
                .andExpect(jsonPath("$.count_human_dna").isNumber())
                .andExpect(jsonPath("$.ratio").isNumber());
    }

    @Test
    @DisplayName("POST /mutant debe manejar matriz 4x4 mínima")
    void testMutantEndpoint_HandleMinimumSize_4x4() throws Exception {
        String[] minDna = {
            "AAAA",
            "CCCC",
            "TATA",
            "GCGC"
        };

        DnaRequest request = new DnaRequest(minDna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant debe rechazar JSON sin campo 'dna'")
    void testMutantEndpoint_RejectInvalidJson_MissingDnaField() throws Exception {
        String invalidJson = "{}";

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant debe rechazar JSON con dna null")
    void testMutantEndpoint_RejectInvalidJson_NullDna() throws Exception {
        String invalidJson = "{\"dna\": null}";

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}

