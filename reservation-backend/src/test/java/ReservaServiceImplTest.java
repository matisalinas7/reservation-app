import com.devsenior.msal.reservation.dto.request.ReservaRequestDTO;
import com.devsenior.msal.reservation.dto.response.ReservaResponseDTO;
import com.devsenior.msal.reservation.entity.*;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import com.devsenior.msal.reservation.enums.Rol;
import com.devsenior.msal.reservation.exception.BusinessRuleViolationException;
import com.devsenior.msal.reservation.repository.ReservaRepository;
import com.devsenior.msal.reservation.repository.ServicioRepository;
import com.devsenior.msal.reservation.repository.TurnoRepository;
import com.devsenior.msal.reservation.repository.UsuarioRepository;
import com.devsenior.msal.reservation.security.UserDetailsImpl;
import com.devsenior.msal.reservation.service.ReservaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    // Crear reserva
    @Test
    void crearReserva_caminoFeliz_retornaReservaResponseDTO() {

        // ARRANGE
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Peluquería");

        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setNombre("Corte");
        servicio.setDuracion(30);
        servicio.setCategoria(categoria);

        Horario horario = new Horario();
        horario.setId(1L);
        horario.setServicio(servicio);

        Turno turno = new Turno();
        turno.setId(1L);
        turno.setFecha(LocalDate.now().plusDays(1));
        turno.setHoraInicio(LocalTime.of(10, 0));
        turno.setHoraFin(LocalTime.of(10, 30));
        turno.setHorario(horario);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.CLIENTE);

        ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, 1L);

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setId(1L);
        reservaGuardada.setUsuario(usuario);
        reservaGuardada.setTurno(turno);
        reservaGuardada.setServicio(servicio);
        reservaGuardada.setEstado(ReservationStatus.ACTIVE);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(reservaRepository.existsByTurnoAndEstado(turno, ReservationStatus.ACTIVE)).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        // ACT
        ReservaResponseDTO resultado = reservaService.crearReserva(request);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals(ReservationStatus.ACTIVE, resultado.estado());
    }

    @Test
    void crearReserva_turnoOcupado_lanzaBusinessRuleViolationException() {

        // ARRANGE
        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setDuracion(30);

        Horario horario = new Horario();
        horario.setServicio(servicio);

        Turno turno = new Turno();
        turno.setId(1L);
        turno.setFecha(LocalDate.now().plusDays(1));
        turno.setHoraInicio(LocalTime.of(10, 0));
        turno.setHoraFin(LocalTime.of(10, 30));
        turno.setHorario(horario);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.CLIENTE);

        ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, 1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(reservaRepository.existsByTurnoAndEstado(turno, ReservationStatus.ACTIVE)).thenReturn(true);

        // ACT & ASSERT
        assertThrows(BusinessRuleViolationException.class, () -> reservaService.crearReserva(request));
    }

    @Test
    void crearReserva_turnoYaComenzado_lanzaBusinessRuleViolationException() {

        // ARRANGE
        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setDuracion(30);

        Horario horario = new Horario();
        horario.setServicio(servicio);

        Turno turno = new Turno();
        turno.setId(1L);
        turno.setFecha(LocalDate.now().minusDays(1));
        turno.setHoraInicio(LocalTime.of(10, 0));
        turno.setHoraFin(LocalTime.of(10, 30));
        turno.setHorario(horario);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.CLIENTE);

        ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, 1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        // ACT & ASSERT
        assertThrows(BusinessRuleViolationException.class, () -> reservaService.crearReserva(request));
    }

    @Test
    void crearReserva_turnoNoCorrespondeAlServicio_lanzaBusinessRuleViolationException() {

        // ARRANGE
        Servicio servicio = new Servicio();
        servicio.setId(1L);

        Servicio servicioDelTurno = new Servicio();
        servicioDelTurno.setId(2L);

        Horario horario = new Horario();
        horario.setServicio(servicioDelTurno);

        Turno turno = new Turno();
        turno.setId(2L);
        turno.setFecha(LocalDate.now().plusDays(1));
        turno.setHoraInicio(LocalTime.of(10, 0));
        turno.setHoraFin(LocalTime.of(10, 30));
        turno.setHorario(horario);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.CLIENTE);

        ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, 1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        // ACT & ASSERT
        assertThrows(BusinessRuleViolationException.class, () -> reservaService.crearReserva(request));
    }

    // Cancelar reserva
    @Test
    void cancelarReserva_reservaYaCancelada_lanzaBusinessRuleViolationException() {

        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.CLIENTE);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado(ReservationStatus.CANCELLED);


        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // ACT & ASSERT
        assertThrows(BusinessRuleViolationException.class,
                () -> reservaService.cancelarReserva(1L, null, mock(Authentication.class)));

    }

    @Test
    void cancelarReserva_turnoYaComenzado_lanzaBusinessRuleViolationException() {

        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.CLIENTE);

        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setDuracion(30);

        Horario horario = new Horario();
        horario.setServicio(servicio);

        Turno turno = new Turno();
        turno.setId(1L);
        turno.setFecha(LocalDate.now().minusDays(1));
        turno.setHoraInicio(LocalTime.of(10, 0));
        turno.setHoraFin(LocalTime.of(10, 30));
        turno.setHorario(horario);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado(ReservationStatus.ACTIVE);
        reserva.setUsuario(usuario);
        reserva.setTurno(turno);
        reserva.setServicio(servicio);

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // ACT & ASSERT
        assertThrows(BusinessRuleViolationException.class, () -> reservaService.cancelarReserva(1L, null, authentication));
    }

    @Test
    void cancelarReserva_fueraVentanaCancelacion_lanzaBusinessRuleViolationException() {
        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.CLIENTE);

        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setDuracion(30);

        Horario horario = new Horario();
        horario.setServicio(servicio);

        Turno turno = new Turno();
        turno.setId(1L);
        turno.setFecha(LocalDate.now());
        turno.setHoraInicio(LocalTime.now(ZoneOffset.UTC).plusMinutes(10)); // empieza en 10 min
        turno.setHoraFin(LocalTime.now(ZoneOffset.UTC).plusMinutes(40));
        turno.setHorario(horario);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado(ReservationStatus.ACTIVE);
        reserva.setUsuario(usuario);
        reserva.setTurno(turno);
        reserva.setServicio(servicio);

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(BusinessRuleViolationException.class, () -> reservaService.cancelarReserva(1L, null, authentication));
    }

    @Test
    void cancelarReserva_empleadoCancelaSinRestriccion() {
        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.EMPLEADO);

        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setDuracion(30);

        Horario horario = new Horario();
        horario.setServicio(servicio);

        Turno turno = new Turno();
        turno.setId(1L);
        turno.setFecha(LocalDate.now().minusDays(1));
        turno.setHoraInicio(LocalTime.of(10, 0));
        turno.setHoraFin(LocalTime.of(10, 30));
        turno.setHorario(horario);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado(ReservationStatus.ACTIVE);
        reserva.setUsuario(usuario);
        reserva.setTurno(turno);
        reserva.setServicio(servicio);

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // ACT & ASSERT
        assertDoesNotThrow(() -> reservaService.cancelarReserva(1L, null, authentication));
    }
}