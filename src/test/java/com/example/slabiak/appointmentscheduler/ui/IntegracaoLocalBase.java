package com.example.slabiak.appointmentscheduler.ui;

import com.example.slabiak.appointmentscheduler.dao.AppointmentRepository;
import com.example.slabiak.appointmentscheduler.dao.WorkRepository;
import com.example.slabiak.appointmentscheduler.dao.user.customer.CustomerRepository;
import com.example.slabiak.appointmentscheduler.dao.user.provider.ProviderRepository;
import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.AppointmentStatus;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Base dos testes de integração do ClínicaFácil que rodam sem Docker:
 * sobe o sistema inteiro com o perfil "local" (banco H2 em memória, com os dados de exemplo)
 * e desfaz as alterações de cada teste no final (@Transactional).
 *
 * Usuários de exemplo: 'paciente' (id 3), 'estagiario' (id 2), 'estagiaria' (id 4) e 'coordenacao' (id 1).
 */
@RunWith(SpringRunner.class)
// a pasta de testes tem uma cópia antiga de appointmentscheduler.sql (para MySQL), por isso o caminho do script é explícito
@SpringBootTest(properties = "spring.datasource.schema=file:src/main/resources/appointmentscheduler.sql")
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
public abstract class IntegracaoLocalBase {

    protected static final int ID_PACIENTE = 3;
    protected static final int ID_ESTAGIARIO = 2;
    protected static final int ID_PSICOLOGIA = 1;

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected AppointmentRepository appointmentRepository;
    @Autowired
    protected CustomerRepository customerRepository;
    @Autowired
    protected ProviderRepository providerRepository;
    @Autowired
    protected WorkRepository workRepository;

    /** Cria uma consulta de Psicologia da paciente com o Lucas, começando daqui a {@code horasAFrente} horas. */
    protected Appointment criarConsulta(AppointmentStatus status, long horasAFrente) {
        Appointment consulta = new Appointment();
        consulta.setCustomer(customerRepository.findById(ID_PACIENTE).get());
        consulta.setProvider(providerRepository.findById(ID_ESTAGIARIO).get());
        consulta.setWork(workRepository.findById(ID_PSICOLOGIA).get());
        consulta.setStart(LocalDateTime.now().plusHours(horasAFrente));
        consulta.setEnd(LocalDateTime.now().plusHours(horasAFrente).plusMinutes(50));
        consulta.setStatus(status);
        return appointmentRepository.saveAndFlush(consulta);
    }

    protected AppointmentStatus statusAtual(Appointment consulta) {
        return appointmentRepository.findById(consulta.getId()).get().getStatus();
    }
}
