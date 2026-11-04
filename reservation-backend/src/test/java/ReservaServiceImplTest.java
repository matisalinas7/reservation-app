import com.devsenior.msal.reservation.config.ClockConfig;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    // Reloj fijo en hora de Argentina: los tests no dependen de la hora ni del huso de la máquina
    private final Clock clock = Clock.fixed(
            LocalDateTime.of(2026, 11, 3, 15, 0).atZone(ClockConfig.ZONA_ARGENTINA).toInstant(),
            ClockConfig.ZONA_ARGENTINA);

    private ReservaServiceImpl reservaService;

    @BeforeEach
    void setUp() {
        reservaService = new ReservaServiceImpl(
                reservaRepository, usuarioRepository, turnoRepository, servicioRepository, clock);
    }

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
        turno.setFecha(LocalDate.now(clock).plusDays(1));
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
        ReservaResponseDTO resultado = reservaService.crearReserva(request, new UserDetailsImpl(usuario));

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
        turno.setFecha(LocalDate.now(clock).plusDays(1));
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
        assertThrows(BusinessRuleViolationException.class, () -> reservaService.crearReserva(request, new UserDetailsImpl(usuario)));
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
        turno.setFecha(LocalDate.now(clock).minusDays(1));
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
        assertThrows(BusinessRuleViolationException.class, () -> reservaService.crearReserva(request, new UserDetailsImpl(usuario)));
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
        turno.setFecha(LocalDate.now(clock).plusDays(1));
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
        assertThrows(BusinessRuleViolationException.class, () -> reservaService.crearReserva(request, new UserDetailsImpl(usuario)));
    }

    @Test
    void crearReserva_clienteReservaParaOtroUsuario_lanzaAccessDeniedException() {

        // ARRANGE
        Usuario cliente = new Usuario();
        cliente.setId(1L);
        cliente.setRol(Rol.CLIENTE);

        ReservaRequestDTO request = new ReservaRequestDTO(2L, 1L, 1L);

        // ACT & ASSERT
        assertThrows(AccessDeniedException.class,
                () -> reservaService.crearReserva(request, new UserDetailsImpl(cliente)));
    }

    @Test
    void crearReserva_empleadoReservaParaUsuarioNoCliente_lanzaBusinessRuleViolationException() {

        // ARRANGE
        Usuario empleado = new Usuario();
        empleado.setId(1L);
        empleado.setRol(Rol.EMPLEADO);

        Usuario admin = new Usuario();
        admin.setId(2L);
        admin.setRol(Rol.ADMIN);

        ReservaRequestDTO request = new ReservaRequestDTO(2L, 1L, 1L);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(admin));

        // ACT & ASSERT
        assertThrows(BusinessRuleViolationException.class,
                () -> reservaService.crearReserva(request, new UserDetailsImpl(empleado)));
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
        reserva.setUsuario(usuario);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // ACT & ASSERT
        assertThrows(BusinessRuleViolationException.class,
                () -> reservaService.cancelarReserva(1L, null, new UserDetailsImpl(usuario)));

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
        turno.setFecha(LocalDate.now(clock).minusDays(1));
        turno.setHoraInicio(LocalTime.of(10, 0));
        turno.setHoraFin(LocalTime.of(10, 30));
        turno.setHorario(horario);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado(ReservationStatus.ACTIVE);
        reserva.setUsuario(usuario);
        reserva.setTurno(turno);
        reserva.setServicio(servicio);

        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // ACT & ASSERT
        assertThrows(BusinessRuleViolationException.class, () -> reservaService.cancelarReserva(1L, null, userDetails));
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
        LocalDateTime inicio = LocalDateTime.now(clock).plusMinutes(10);
        turno.setFecha(inicio.toLocalDate());
        turno.setHoraInicio(inicio.toLocalTime()); // empieza en 10 min
        turno.setHoraFin(inicio.toLocalTime().plusMinutes(30));
        turno.setHorario(horario);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado(ReservationStatus.ACTIVE);
        reserva.setUsuario(usuario);
        reserva.setTurno(turno);
        reserva.setServicio(servicio);

        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(BusinessRuleViolationException.class, () -> reservaService.cancelarReserva(1L, null, userDetails));
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
        turno.setFecha(LocalDate.now(clock).minusDays(1));
        turno.setHoraInicio(LocalTime.of(10, 0));
        turno.setHoraFin(LocalTime.of(10, 30));
        turno.setHorario(horario);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado(ReservationStatus.ACTIVE);
        reserva.setUsuario(usuario);
        reserva.setTurno(turno);
        reserva.setServicio(servicio);

        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // ACT & ASSERT
        assertDoesNotThrow(() -> reservaService.cancelarReserva(1L, null, userDetails));
    }

    // Acceso a reservas ajenas
    private Reserva reservaActivaDe(Usuario duenio) {
        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setDuracion(30);

        Horario horario = new Horario();
        horario.setServicio(servicio);

        Turno turno = new Turno();
        turno.setId(1L);
        turno.setFecha(LocalDate.now(clock).plusDays(1));
        turno.setHoraInicio(LocalTime.of(10, 0));
        turno.setHoraFin(LocalTime.of(10, 30));
        turno.setHorario(horario);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setEstado(ReservationStatus.ACTIVE);
        reserva.setUsuario(duenio);
        reserva.setTurno(turno);
        reserva.setServicio(servicio);
        return reserva;
    }

    private Usuario usuarioCon(Long id, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setRol(rol);
        return usuario;
    }

    @Test
    void cancelarReserva_clienteCancelaReservaAjena_lanzaForbidden() {

        // ARRANGE
        Reserva reserva = reservaActivaDe(usuarioCon(2L, Rol.CLIENTE));
        UserDetailsImpl otroCliente = new UserDetailsImpl(usuarioCon(1L, Rol.CLIENTE));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // ACT & ASSERT
        BusinessRuleViolationException ex = assertThrows(BusinessRuleViolationException.class,
                () -> reservaService.cancelarReserva(1L, null, otroCliente));
        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
        assertEquals(ReservationStatus.ACTIVE, reserva.getEstado());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void findReservaById_clienteObtieneReservaAjena_lanzaForbidden() {

        // ARRANGE
        Reserva reserva = reservaActivaDe(usuarioCon(2L, Rol.CLIENTE));
        UserDetailsImpl otroCliente = new UserDetailsImpl(usuarioCon(1L, Rol.CLIENTE));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // ACT & ASSERT
        BusinessRuleViolationException ex = assertThrows(BusinessRuleViolationException.class,
                () -> reservaService.findReservaById(1L, otroCliente));
        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
    }

    @Test
    void cancelarReserva_empleadoCancelaReservaDeCliente_cancela() {

        // ARRANGE
        Reserva reserva = reservaActivaDe(usuarioCon(2L, Rol.CLIENTE));
        UserDetailsImpl empleado = new UserDetailsImpl(usuarioCon(1L, Rol.EMPLEADO));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // ACT
        reservaService.cancelarReserva(1L, null, empleado);

        // ASSERT
        assertEquals(ReservationStatus.CANCELLED, reserva.getEstado());
        verify(reservaRepository).save(reserva);
    }

    // Ventana de cancelación: el cliente puede cancelar hasta (inicio del turno - duración del servicio)
    private Reserva reservaQueEmpiezaEn(Usuario duenio, int minutos, int duracionServicio) {
        Reserva reserva = reservaActivaDe(duenio);
        reserva.getServicio().setDuracion(duracionServicio);
        LocalDateTime inicio = LocalDateTime.now(clock).plusMinutes(minutos);
        reserva.getTurno().setFecha(inicio.toLocalDate());
        reserva.getTurno().setHoraInicio(inicio.toLocalTime());
        reserva.getTurno().setHoraFin(inicio.toLocalTime().plusMinutes(duracionServicio));
        return reserva;
    }

    @Test
    void cancelarReserva_turnoEnUnaHoraServicioDe30Min_dentroDeVentana_cancela() {

        // ARRANGE
        Usuario cliente = usuarioCon(1L, Rol.CLIENTE);
        Reserva reserva = reservaQueEmpiezaEn(cliente, 60, 30);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // ACT
        reservaService.cancelarReserva(1L, null, new UserDetailsImpl(cliente));

        // ASSERT
        assertEquals(ReservationStatus.CANCELLED, reserva.getEstado());
    }

    @Test
    void cancelarReserva_turnoEnUnaHoraServicioDe90Min_fueraDeVentana_lanzaConflict() {

        // ARRANGE
        Usuario cliente = usuarioCon(1L, Rol.CLIENTE);
        Reserva reserva = reservaQueEmpiezaEn(cliente, 60, 90);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // ACT & ASSERT
        BusinessRuleViolationException ex = assertThrows(BusinessRuleViolationException.class,
                () -> reservaService.cancelarReserva(1L, null, new UserDetailsImpl(cliente)));
        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        assertEquals(ReservationStatus.ACTIVE, reserva.getEstado());
    }
}
