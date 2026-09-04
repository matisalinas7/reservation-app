package com.devsenior.msal.reservation;

import com.devsenior.msal.reservation.controller.ReservaController;
import com.devsenior.msal.reservation.dto.request.ReservaRequestDTO;
import com.devsenior.msal.reservation.dto.response.ReservaResponseDTO;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import com.devsenior.msal.reservation.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @Test
    void crearReserva_requestValido_retorna201() throws Exception {

        // ARRANGE
        ReservaResponseDTO response = new ReservaResponseDTO(
                1L, 1L, 1L, 1L,
                ReservationStatus.ACTIVE,
                null,
                null
        );

        when(reservaService.crearReserva(any(ReservaRequestDTO.class))).thenReturn(response);

        // ACT & ASSERT
        mockMvc.perform(post("/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "usuarioId": 1,
                            "turnoId": 1,
                            "servicioId":1
                        }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void crearReserva_usuarioInvalido_retorna400() throws Exception {

        // ACT & ASSERT
        mockMvc.perform(post("/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "turnoId": 1,
                            "servicioId":1
                        }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void crearReserva_turnoInvalido_retorna400() throws Exception {

        // ACT & ASSERT
        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "usuarioId": 1,
                            "servicioId":1
                        }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    void crearReserva_servicioInvalido_retorna400() throws Exception {

        // ACT & ASSERT
        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "usuarioId": 1,
                            "turnoId":1
                        }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }
}