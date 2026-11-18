package com.example.slabiak.appointmentscheduler.service.impl;


import com.example.slabiak.appointmentscheduler.service.AppointmentService;
import com.example.slabiak.appointmentscheduler.service.InvoiceService;
import com.example.slabiak.appointmentscheduler.service.ScheduledTasksService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasksServiceImpl implements ScheduledTasksService {

    private final AppointmentService appointmentService;
    private final InvoiceService invoiceService;

    public ScheduledTasksServiceImpl(AppointmentService appointmentService, InvoiceService invoiceService) {
        this.appointmentService = appointmentService;
        this.invoiceService = invoiceService;
    }

    // roda a cada 30 minutos
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    @Override
    public void updateAllAppointmentsStatuses() {
        appointmentService.updateAppointmentsStatusesWithExpiredExchangeRequest();
        appointmentService.updateAllAppointmentsStatuses();
    }

    // roda no primeiro dia de cada mês
    @Scheduled(cron = "0 0 0 1 * ?")
    @Override
    public void issueInvoicesForCurrentMonth() {
        invoiceService.issueInvoicesForConfirmedAppointments();
    }


}
