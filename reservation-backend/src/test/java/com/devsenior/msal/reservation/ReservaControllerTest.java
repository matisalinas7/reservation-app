package com.devsenior.msal.reservation;

import com.devsenior.msal.reservation.controller.ReservaController;
import com.devsenior.msal.reservation.dto.request.ReservaRequestDTO;
import com.devsenior.msal.reservation.dto.response.ReservaResponseDTO;
import com.devsenior.msal.reservation.entity.Usuario;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import com.devsenior.msal.reservation.enums.Rol;
import com.devsenior.msal.reservation.security.JwtFilter;
import com.devsenior.msal.reservation.security.JwtService;
import com.devsenior.msal.reservation.security.SecurityConfig;
import com.devsenior.msal.reservation.security.UserDetailsImpl;
import com.devsenior.msal.reservation.security.UserDetailsServiceImpl;
import com.devsenior.msal.reservation.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Sin SecurityConfig el slice no aplica los @PreAuthorize: los tests pasarían con cualquier rol
@WebMvcTest(ReservaController.class)
@Import({SecurityConfig.class, JwtFilter.class})
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    // Dependencias del JwtFilter, que el slice de @WebMvcTest carga
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private static UserDetailsImpl cliente() {
        return conRol(Rol.CLIENTE);
    }

    private static UserDetailsImpl conRol(Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(rol);
        return new UserDetailsImpl(usuario);
    }

    @Test
    void crearReserva_admin_retorna403() throws Exception {
        mockMvc.perform(post("/reservas")
                        .with(user(conRol(Rol.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usuarioId": 1, "turnoId": 1, "servicioId": 1}
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(reservaService);
    }

    @Test
    void crearReserva_requestValido_retorna201() throws Exception {

        // ARRANGE
        ReservaResponseDTO response = new ReservaResponseDTO(
                1L, 1L, "Juan Doe", "juandoe@gmail.com", 1L, 1L,
                "Corte", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(10, 30),
                ReservationStatus.ACTIVE,
                null,
                null
        );

        when(reservaService.crearReserva(any(ReservaRequestDTO.class), any())).thenReturn(response);

        // ACT & ASSERT
        mockMvc.perform(post("/reservas")
                        .with(user(cliente()))
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
                        .with(user(cliente()))
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
                        .with(user(cliente()))
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
                        .with(user(cliente()))
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