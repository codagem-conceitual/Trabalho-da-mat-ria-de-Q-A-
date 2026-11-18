package com.example.slabiak.appointmentscheduler.ui;

import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.AppointmentStatus;
import com.example.slabiak.appointmentscheduler.service.JwtTokenService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cenários 32 a 34 da auditoria: contestação de consulta ("a consulta não aconteceu").
 * O paciente recebe por e-mail um link com token para contestar, e o estagiário recebe um link para aceitar.
 */
public class AppointmentRejectionSecurityIT extends IntegracaoLocalBase {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    public void shouldAllowValidTokenWithoutAuthenticatedSession() throws Exception {
        // consulta que terminou há pouco: o paciente ainda pode contestar pelo link do e-mail
        Appointment consulta = criarConsulta(AppointmentStatus.FINISHED, -2);
        String token = jwtTokenService.generateAppointmentRejectionToken(consulta);

        // quem clica no link do e-mail normalmente não está logado no sistema
        mockMvc.perform(get("/appointments/reject").param("token", token))
                .andExpect(status().isOk());

        assertEquals(AppointmentStatus.REJECTION_REQUESTED, statusAtual(consulta));
    }

    @Test
    @WithUserDetails("estagiaria")
    public void shouldNotAllowProviderToActOnAnotherProvidersAppointment() throws Exception {
        // a consulta é do estagiário Lucas; a estagiária Ana tenta aceitar a contestação
        Appointment consulta = criarConsulta(AppointmentStatus.REJECTION_REQUESTED, -2);

        try {
            mockMvc.perform(post("/appointments/acceptRejection")
                    .param("appointmentId", consulta.getId().toString()).with(csrf()));
        } catch (Exception recusado) {
            // recusar com erro também é aceitável
        }

        assertEquals(AppointmentStatus.REJECTION_REQUESTED, statusAtual(consulta));
    }

    @Test
    @WithUserDetails("estagiario")
    public void shouldRejectInvalidOrUnauthorizedApprovalRequest() throws Exception {
        Appointment consulta = criarConsulta(AppointmentStatus.REJECTION_REQUESTED, -2);
        String tokenValido = jwtTokenService.generateAcceptRejectionToken(consulta);
        String tokenAdulterado = tokenValido.substring(0, tokenValido.length() - 4) + "abcd";

        try {
            mockMvc.perform(get("/appointments/acceptRejection").param("token", tokenAdulterado));
            mockMvc.perform(get("/appointments/acceptRejection").param("token", "token-invalido"));
        } catch (Exception recusado) {
            // recusar com erro também é aceitável
        }

        assertEquals(AppointmentStatus.REJECTION_REQUESTED, statusAtual(consulta));
    }
}
