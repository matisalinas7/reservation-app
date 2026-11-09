package com.devsenior.msal.reservation;

import com.devsenior.msal.reservation.controller.UsuarioController;
import com.devsenior.msal.reservation.dto.response.ClienteResumenDTO;
import com.devsenior.msal.reservation.entity.Usuario;
import com.devsenior.msal.reservation.enums.Rol;
import com.devsenior.msal.reservation.security.JwtFilter;
import com.devsenior.msal.reservation.security.JwtService;
import com.devsenior.msal.reservation.security.SecurityConfig;
import com.devsenior.msal.reservation.security.UserDetailsImpl;
import com.devsenior.msal.reservation.security.UserDetailsServiceImpl;
import com.devsenior.msal.reservation.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Se importa SecurityConfig para que @PreAuthorize (@EnableMethodSecurity) se aplique en el slice
@WebMvcTest(UsuarioController.class)
@Import({SecurityConfig.class, JwtFilter.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private static UserDetailsImpl conRol(Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setId(99L);
        usuario.setRol(rol);
        return new UserDetailsImpl(usuario);
    }

    @Test
    void clientes_empleado_recibeSoloDatosMinimos() throws Exception {
        when(usuarioService.findClientesActivos()).thenReturn(List.of(
                new ClienteResumenDTO(1L, "Juan", "Doe", "juandoe@gmail.com")));

        mockMvc.perform(get("/usuarios/clientes").with(user(conRol(Rol.EMPLEADO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].apellido").value("Doe"))
                .andExpect(jsonPath("$[0].mail").value("juandoe@gmail.com"))
                // El DTO no expone teléfono ni rol
                .andExpect(jsonPath("$[0].telefono").doesNotExist())
                .andExpect(jsonPath("$[0].rol").doesNotExist());
    }

    @Test
    void clientes_cliente_recibe403() throws Exception {
        mockMvc.perform(get("/usuarios/clientes").with(user(conRol(Rol.CLIENTE))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void activos_empleado_recibe403() throws Exception {
        mockMvc.perform(get("/usuarios/activos").with(user(conRol(Rol.EMPLEADO))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void activos_admin_recibe200() throws Exception {
        when(usuarioService.findAllUsuariosActivos()).thenReturn(List.of());

        mockMvc.perform(get("/usuarios/activos").with(user(conRol(Rol.ADMIN))))
                .andExpect(status().isOk());
    }
}
