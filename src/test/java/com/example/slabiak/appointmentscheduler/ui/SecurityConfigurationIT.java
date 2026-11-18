package com.example.slabiak.appointmentscheduler.ui;

import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.AppointmentStatus;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cenários 35 e 36 da auditoria: proteção contra CSRF.
 * Um pedido que muda dados (aqui, cancelar uma consulta) só deve ser aceito com o token CSRF do próprio site.
 */
public class SecurityConfigurationIT extends IntegracaoLocalBase {

    @Test
    @WithUserDetails("paciente")
    public void shouldRejectStateChangingRequestWithoutCsrfToken() throws Exception {
        Appointment consulta = criarConsulta(AppointmentStatus.SCHEDULED, 72);

        mockMvc.perform(post("/appointments/cancel").param("appointmentId", consulta.getId().toString()))
                .andExpect(status().isForbidden());

        assertEquals(AppointmentStatus.SCHEDULED, statusAtual(consulta));
    }

    @Test
    @WithUserDetails("paciente")
    public void shouldAllowStateChangingRequestWithValidCsrfToken() throws Exception {
        Appointment consulta = criarConsulta(AppointmentStatus.SCHEDULED, 72);

        mockMvc.perform(post("/appointments/cancel").param("appointmentId", consulta.getId().toString()).with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertEquals(AppointmentStatus.CANCELED, statusAtual(consulta));
    }
}
