package com.example.slabiak.appointmentscheduler.service.appointment;

import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.Work;
import com.example.slabiak.appointmentscheduler.model.DayPlan;
import com.example.slabiak.appointmentscheduler.model.TimePeroid;
import com.example.slabiak.appointmentscheduler.service.impl.AppointmentServiceImpl;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * ClínicaFácil - RF03: um horário agendado deve sair da lista de horários livres,
 * sem esconder os outros horários do mesmo turno.
 */
public class HorariosLivresTest {

    private final AppointmentServiceImpl appointmentService =
            new AppointmentServiceImpl(null, null, null, null, null, null);

    @Test
    public void consultaAs8hDeveManterOsOutrosHorariosDaManha() {
        // dia da clínica: 8h às 17h com almoço das 12h às 13h; Psicologia dura 50 min
        DayPlan dia = new DayPlan();
        dia.setWorkingHours(new TimePeroid(LocalTime.of(8, 0), LocalTime.of(17, 0)));
        dia.addBreak(new TimePeroid(LocalTime.of(12, 0), LocalTime.of(13, 0)));
        Work psicologia = new Work();
        psicologia.setDuration(50);

        Appointment consulta = new Appointment();
        consulta.setStart(LocalDateTime.of(2026, 11, 17, 8, 0));
        consulta.setEnd(LocalDateTime.of(2026, 11, 17, 8, 50));
        List<Appointment> consultas = new ArrayList<>();
        consultas.add(consulta);

        // como em getAvailableHours: tira as consultas do estagiário e depois as do paciente
        List<TimePeroid> livres = dia.timePeroidsWithBreaksExcluded();
        livres = appointmentService.excludeAppointmentsFromTimePeroids(livres, consultas);
        livres = appointmentService.excludeAppointmentsFromTimePeroids(livres, consultas);
        List<String> inicios = appointmentService.calculateAvailableHours(livres, psicologia).stream()
                .map(p -> p.getStart().toString())
                .collect(Collectors.toList());

        assertEquals("[08:50, 09:40, 10:30, 13:00, 13:50, 14:40, 15:30]", inicios.toString());
    }

    @Test
    public void calcularHorariosLivresNaoDeveAlterarOExpedienteDoEstagiario() {
        // o expediente fica salvo no banco; se o cálculo alterar o objeto, a alteração é gravada
        DayPlan dia = new DayPlan();
        dia.setWorkingHours(new TimePeroid(LocalTime.of(8, 0), LocalTime.of(17, 0)));
        dia.addBreak(new TimePeroid(LocalTime.of(12, 0), LocalTime.of(13, 0)));

        dia.timePeroidsWithBreaksExcluded();
        dia.timePeroidsWithBreaksExcluded();

        assertEquals(LocalTime.of(8, 0), dia.getWorkingHours().getStart());
        assertEquals(LocalTime.of(17, 0), dia.getWorkingHours().getEnd());
        assertEquals(LocalTime.of(12, 0), dia.getBreaks().get(0).getStart());
        assertEquals(LocalTime.of(13, 0), dia.getBreaks().get(0).getEnd());
    }
}
