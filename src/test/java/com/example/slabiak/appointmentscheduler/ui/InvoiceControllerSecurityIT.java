package com.example.slabiak.appointmentscheduler.ui;

import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cenários 22 a 24 da auditoria: só a coordenação (administrador) pode emitir comprovantes/faturas
 * manualmente pelo endereço /invoices/issue.
 */
public class InvoiceControllerSecurityIT extends IntegracaoLocalBase {

    @Test
    @WithUserDetails("paciente")
    public void shouldNotAllowCustomerToIssueInvoices() throws Exception {
        mockMvc.perform(get("/invoices/issue")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("estagiario")
    public void shouldNotAllowProviderToIssueInvoices() throws Exception {
        mockMvc.perform(get("/invoices/issue")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("coordenacao")
    public void shouldAllowAdminToIssueInvoices() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/invoices/issue")).andReturn();

        int codigo = resultado.getResponse().getStatus();
        assertTrue("A coordenação deveria conseguir emitir (código HTTP " + codigo + ")", codigo < 400);
    }
}
