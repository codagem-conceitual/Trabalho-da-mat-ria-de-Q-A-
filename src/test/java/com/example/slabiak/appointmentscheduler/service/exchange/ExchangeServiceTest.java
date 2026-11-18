package com.example.slabiak.appointmentscheduler.service.exchange;

import com.example.slabiak.appointmentscheduler.dao.AppointmentRepository;
import com.example.slabiak.appointmentscheduler.dao.ExchangeRequestRepository;
import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.AppointmentStatus;
import com.example.slabiak.appointmentscheduler.entity.ExchangeRequest;
import com.example.slabiak.appointmentscheduler.entity.ExchangeStatus;
import com.example.slabiak.appointmentscheduler.entity.Work;
import com.example.slabiak.appointmentscheduler.entity.user.customer.Customer;
import com.example.slabiak.appointmentscheduler.entity.user.provider.Provider;
import com.example.slabiak.appointmentscheduler.service.NotificationService;
import com.example.slabiak.appointmentscheduler.service.impl.ExchangeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Cenários 5 a 12 da auditoria: troca de horário entre pacientes.
 * O paciente A pediu para trocar a consulta dele (requestor) pela consulta do paciente B (requested).
 * Quem pode aceitar ou recusar é só o paciente B, e só enquanto o pedido estiver pendente.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExchangeServiceTest {

    private static final int ID_TROCA = 50;
    private static final int ID_CONSULTA_A = 20;
    private static final int ID_CONSULTA_B = 21;
    private static final int ID_OUTRO_PACIENTE = 99;

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ExchangeRequestRepository exchangeRequestRepository;

    @InjectMocks
    private ExchangeServiceImpl exchangeService;

    private Customer pacienteA;
    private Customer pacienteB;
    private Appointment consultaA;
    private Appointment consultaB;
    private ExchangeRequest troca;

    @Before
    public void prepararTroca() {
        pacienteA = new Customer();
        pacienteA.setId(1);
        pacienteB = new Customer();
        pacienteB.setId(2);
        Provider estagiario = new Provider();
        estagiario.setId(3);
        Work especialidade = new Work();
        especialidade.setId(4);

        consultaA = novaConsulta(ID_CONSULTA_A, pacienteA, estagiario, especialidade, 5);
        consultaA.setStatus(AppointmentStatus.EXCHANGE_REQUESTED);
        consultaB = novaConsulta(ID_CONSULTA_B, pacienteB, estagiario, especialidade, 6);

        troca = new ExchangeRequest(consultaA, consultaB, ExchangeStatus.PENDING);
        troca.setId(ID_TROCA);

        lenient().when(exchangeRequestRepository.getOne(ID_TROCA)).thenReturn(troca);
        lenient().when(appointmentRepository.getOne(ID_CONSULTA_A)).thenReturn(consultaA);
        lenient().when(appointmentRepository.getOne(ID_CONSULTA_B)).thenReturn(consultaB);
    }

    private static Appointment novaConsulta(int id, Customer paciente, Provider estagiario, Work especialidade, int diasAFrente) {
        Appointment consulta = new Appointment();
        consulta.setId(id);
        consulta.setCustomer(paciente);
        consulta.setProvider(estagiario);
        consulta.setWork(especialidade);
        consulta.setStatus(AppointmentStatus.SCHEDULED);
        consulta.setStart(LocalDateTime.now().plusDays(diasAFrente));
        consulta.setEnd(LocalDateTime.now().plusDays(diasAFrente).plusMinutes(50));
        return consulta;
    }

    /** Executa a ação; uma exceção também conta como "o sistema recusou". */
    private void tentar(Runnable acao) {
        try {
            acao.run();
        } catch (RuntimeException recusado) {
            // recusar com exceção é aceitável
        }
    }

    private void conferirQueNadaMudou(ExchangeStatus statusEsperado) {
        assertEquals("O status do pedido de troca não deveria mudar", statusEsperado, troca.getStatus());
        assertSame("A consulta A deveria continuar com o paciente A", pacienteA, consultaA.getCustomer());
        assertSame("A consulta B deveria continuar com o paciente B", pacienteB, consultaB.getCustomer());
        verify(exchangeRequestRepository, never()).save(any(ExchangeRequest.class));
    }

    @Test
    public void shouldAllowRecipientToAcceptExchange() {
        exchangeService.acceptExchange(ID_TROCA, pacienteB.getId());

        assertEquals(ExchangeStatus.ACCEPTED, troca.getStatus());
        assertSame(pacienteB, consultaA.getCustomer());
        assertSame(pacienteA, consultaB.getCustomer());
    }

    @Test
    public void shouldNotAllowThirdPartyToAcceptExchange() {
        tentar(() -> exchangeService.acceptExchange(ID_TROCA, ID_OUTRO_PACIENTE));

        conferirQueNadaMudou(ExchangeStatus.PENDING);
    }

    @Test
    public void shouldAllowRecipientToRejectExchange() {
        exchangeService.rejectExchange(ID_TROCA, pacienteB.getId());

        assertEquals(ExchangeStatus.REJECTED, troca.getStatus());
        assertEquals(AppointmentStatus.SCHEDULED, consultaA.getStatus());
    }

    @Test
    public void shouldNotAllowThirdPartyToRejectExchange() {
        tentar(() -> exchangeService.rejectExchange(ID_TROCA, ID_OUTRO_PACIENTE));

        conferirQueNadaMudou(ExchangeStatus.PENDING);
    }

    @Test
    public void shouldNotAcceptExchangeThatIsNotPending() {
        troca.setStatus(ExchangeStatus.REJECTED);

        tentar(() -> exchangeService.acceptExchange(ID_TROCA, pacienteB.getId()));

        conferirQueNadaMudou(ExchangeStatus.REJECTED);
    }

    @Test
    public void shouldNotRejectExchangeThatIsNotPending() {
        troca.setStatus(ExchangeStatus.ACCEPTED);

        tentar(() -> exchangeService.rejectExchange(ID_TROCA, pacienteB.getId()));

        conferirQueNadaMudou(ExchangeStatus.ACCEPTED);
    }

    @Test
    public void shouldNotRequestExchangeWithCanceledAppointment() {
        consultaA.setStatus(AppointmentStatus.CANCELED);

        tentar(() -> exchangeService.requestExchange(ID_CONSULTA_A, ID_CONSULTA_B, pacienteA.getId()));

        assertNotEquals(AppointmentStatus.EXCHANGE_REQUESTED, consultaA.getStatus());
        verify(exchangeRequestRepository, never()).save(any(ExchangeRequest.class));
    }

    @Test
    public void shouldNotRequestExchangeWithAppointmentThatIsNotScheduled() {
        consultaA.setStatus(AppointmentStatus.FINISHED);

        tentar(() -> exchangeService.requestExchange(ID_CONSULTA_A, ID_CONSULTA_B, pacienteA.getId()));

        assertNotEquals(AppointmentStatus.EXCHANGE_REQUESTED, consultaA.getStatus());
        verify(exchangeRequestRepository, never()).save(any(ExchangeRequest.class));
    }
}
