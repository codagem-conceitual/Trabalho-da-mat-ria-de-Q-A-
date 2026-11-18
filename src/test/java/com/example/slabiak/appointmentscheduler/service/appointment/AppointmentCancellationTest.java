package com.example.slabiak.appointmentscheduler.service.appointment;

import com.example.slabiak.appointmentscheduler.dao.AppointmentRepository;
import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.AppointmentStatus;
import com.example.slabiak.appointmentscheduler.entity.Work;
import com.example.slabiak.appointmentscheduler.entity.user.customer.Customer;
import com.example.slabiak.appointmentscheduler.entity.user.provider.Provider;
import com.example.slabiak.appointmentscheduler.service.NotificationService;
import com.example.slabiak.appointmentscheduler.service.UserService;
import com.example.slabiak.appointmentscheduler.service.WorkService;
import com.example.slabiak.appointmentscheduler.service.impl.AppointmentServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Cenários 1 a 4 da auditoria: regras de cancelamento.
 * As regras (24 horas, especialidade que não pode ser cancelada pelo paciente, limite mensal e status Agendada)
 * aparecem na tela, mas os testes chamam diretamente o cancelamento (o mesmo que o POST /appointments/cancel faz)
 * para conferir se o servidor também respeita essas regras.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppointmentCancellationTest {

    private static final int ID_CONSULTA = 10;

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private UserService userService;
    @Mock
    private WorkService workService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Customer paciente;
    private Provider estagiario;
    private Work especialidade;
    private Appointment consulta;

    @Before
    public void prepararConsulta() {
        paciente = new Customer();
        paciente.setId(1);
        estagiario = new Provider();
        estagiario.setId(2);
        especialidade = new Work();
        especialidade.setId(3);
        especialidade.setEditable(true);

        consulta = new Appointment();
        consulta.setId(ID_CONSULTA);
        consulta.setCustomer(paciente);
        consulta.setProvider(estagiario);
        consulta.setWork(especialidade);
        consulta.setStatus(AppointmentStatus.SCHEDULED);
        consulta.setStart(LocalDateTime.now().plusDays(5));
        consulta.setEnd(LocalDateTime.now().plusDays(5).plusMinutes(50));

        lenient().when(appointmentRepository.getOne(ID_CONSULTA)).thenReturn(consulta);
        lenient().when(appointmentRepository.findById(ID_CONSULTA)).thenReturn(java.util.Optional.of(consulta));
        lenient().when(userService.getUserById(1)).thenReturn(paciente);
        lenient().when(userService.getUserById(2)).thenReturn(estagiario);
        lenient().when(appointmentRepository.findByCustomerIdCanceledAfterDate(anyInt(), any())).thenReturn(Collections.emptyList());
    }

    /** Tenta cancelar; aceita como "bloqueado" tanto uma exceção quanto a consulta continuar sem cancelar. */
    private void tentarCancelar(int idUsuario) {
        try {
            appointmentService.cancelUserAppointmentById(ID_CONSULTA, idUsuario);
        } catch (RuntimeException bloqueado) {
            // o sistema recusou com uma exceção: é um comportamento aceitável
        }
    }

    private void conferirQueNaoFoiCancelada() {
        assertNotEquals("A consulta não deveria ter sido cancelada", AppointmentStatus.CANCELED, consulta.getStatus());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void shouldNotCancelAppointmentLessThan24HoursBeforeStart() {
        consulta.setStart(LocalDateTime.now().plusHours(5));
        consulta.setEnd(LocalDateTime.now().plusHours(5).plusMinutes(50));

        tentarCancelar(paciente.getId());

        conferirQueNaoFoiCancelada();
    }

    @Test
    public void shouldNotCancelAppointmentWhenWorkIsNotEditable() {
        especialidade.setEditable(false);

        tentarCancelar(paciente.getId());

        conferirQueNaoFoiCancelada();
    }

    @Test
    public void shouldNotCancelWhenMonthlyCancellationLimitIsReached() {
        List<Appointment> jaCanceladasNoMes = new ArrayList<>();
        jaCanceladasNoMes.add(new Appointment());
        lenient().when(appointmentRepository.findByCustomerIdCanceledAfterDate(anyInt(), any())).thenReturn(jaCanceladasNoMes);

        tentarCancelar(paciente.getId());

        conferirQueNaoFoiCancelada();
    }

    @Test
    public void shouldNotCancelAppointmentThatIsNotScheduled() {
        consulta.setStatus(AppointmentStatus.FINISHED);

        tentarCancelar(estagiario.getId());

        conferirQueNaoFoiCancelada();
    }
}
