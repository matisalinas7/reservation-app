package com.devsenior.msal.reservation;

import com.devsenior.msal.reservation.entity.Usuario;
import com.devsenior.msal.reservation.enums.Rol;
import com.devsenior.msal.reservation.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Prueba la query real contra H2: el filtro por rol y baja vive en la base, no en Java
@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario guardar(String nombre, String apellido, Rol rol, boolean dadoDeBaja) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setMail(nombre.toLowerCase() + "." + apellido.toLowerCase() + "@mail.com");
        u.setContrasenia("hash");
        u.setRol(rol);
        if (dadoDeBaja) {
            u.setFechaBaja(LocalDateTime.now());
        }
        return usuarioRepository.save(u);
    }

    @Test
    void clientesActivos_excluyeStaffYBajas_yOrdenaPorApellidoYNombre() {
        guardar("Zoe", "Perez", Rol.CLIENTE, false);
        guardar("Ana", "Perez", Rol.CLIENTE, false);
        guardar("Luis", "Alvarez", Rol.CLIENTE, false);
        guardar("Baja", "Cliente", Rol.CLIENTE, true);
        guardar("Emp", "Leado", Rol.EMPLEADO, false);
        guardar("Ad", "Min", Rol.ADMIN, false);

        List<Usuario> resultado =
                usuarioRepository.findByRolAndFechaBajaIsNullOrderByApellidoAscNombreAsc(Rol.CLIENTE);

        assertThat(resultado)
                .extracting(u -> u.getApellido() + ", " + u.getNombre())
                .containsExactly("Alvarez, Luis", "Perez, Ana", "Perez, Zoe");
    }
}
