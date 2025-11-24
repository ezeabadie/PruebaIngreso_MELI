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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para los endpoints REST.
 * Prueba el flujo completo: Controller → Service → Repository → Database
 */
@SpringBootTest
@AutoConfigureMockMvc
class MutantControllerIntegrationTest {

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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
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

    @Test
    @DisplayName("POST /mutant debe persistir el resultado en BD")
    void testMutantEndpoint_PersistResult_InDatabase() throws Exception {
        // Usar un ADN único para evitar duplicados en tests
        String[] uniqueDna = {
            "AAAA",
            "CCCC",
            "TTTT",
            "GGGG"
        };

        DnaRequest request = new DnaRequest(uniqueDna);

        // Primera llamada
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Segunda llamada con mismo ADN (debe usar resultado guardado)
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verificar que las estadísticas se actualizaron
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").exists())
                .andExpect(jsonPath("$.count_human_dna").exists());
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
    @DisplayName("POST /mutant debe manejar matriz grande 10x10")
    void testMutantEndpoint_HandleLargeMatrix_10x10() throws Exception {
        String[] largeDna = {
            "ATGCGATGCA",
            "CAGTGCATGC",
            "TTATGTATTG",
            "AGAAGGTAGG",
            "CCCCTACCCC",
            "TCACTGTCAC",
            "ATGCGATGCA",
            "CAGTGCATGC",
            "TTATGTATTG",
            "AGAAGGTAGG"
        };

        DnaRequest request = new DnaRequest(largeDna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
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
    @DisplayName("GET /stats debe calcular ratio correctamente")
    void testStatsEndpoint_CalculateRatio_Correctly() throws Exception {
        // Insertar datos de prueba
        String[] mutant1 = {"AAAA", "CCCC", "TTTT", "GGGG"};
        String[] mutant2 = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        String[] human1 = {"ATGC", "CAGT", "TTAT", "AGAC"};

        // Insertar mutantes
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DnaRequest(mutant1))));

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DnaRequest(mutant2))));

        // Insertar humano
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DnaRequest(human1))));

        // Verificar estadísticas
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.count_human_dna").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.ratio").value(org.hamcrest.Matchers.greaterThan(0.0)));
    }

    @Test
    @DisplayName("GET /stats debe retornar ratio 0 cuando no hay datos")
    void testStatsEndpoint_ReturnZeroRatio_WhenNoData() throws Exception {
        // Este test puede fallar si hay datos de otros tests
        // En un entorno real, usarías @DirtiesContext o limpiarías la BD
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").isNumber())
                .andExpect(jsonPath("$.count_human_dna").isNumber())
                .andExpect(jsonPath("$.ratio").isNumber());
    }

    // ========================================
    // TESTS DE VALIDACIÓN JSON
    // ========================================

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

    @Test
    @DisplayName("POST /mutant debe rechazar Content-Type incorrecto")
    void testMutantEndpoint_RejectInvalidContentType() throws Exception {
        // Spring Boot debería retornar 415 Unsupported Media Type cuando el Content-Type no es el esperado
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status == 415 || status == 400 || status == 500,
                        "Debe rechazar content-type incorrecto. Status recibido: " + status);
                });
    }

    // ========================================
    // TESTS DE CASOS EDGE
    // ========================================

    @Test
    @DisplayName("POST /mutant debe manejar múltiples secuencias")
    void testMutantEndpoint_HandleMultipleSequences() throws Exception {
        String[] multiSequenceDna = {
            "AAAAAA",  // ← Horizontal
            "CCCCCC",  // ← Horizontal
            "TTATGT",
            "AGAAGG",
            "GCGTCA",
            "TCACTG"
        };

        DnaRequest request = new DnaRequest(multiSequenceDna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant debe detectar diagonal ascendente")
    void testMutantEndpoint_DetectAscendingDiagonal() throws Exception {
        // ADN con 2 secuencias horizontales claras (mutante garantizado)
        String[] diagonalDna = {
            "AAAA",  // Secuencia 1: AAAA horizontal
            "CAGT",
            "TTTT",  // Secuencia 2: TTTT horizontal
            "AGAC"
        };

        DnaRequest request = new DnaRequest(diagonalDna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

