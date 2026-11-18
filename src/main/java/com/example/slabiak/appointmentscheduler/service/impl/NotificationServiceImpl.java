package com.example.slabiak.appointmentscheduler.service.impl;

import com.example.slabiak.appointmentscheduler.dao.NotificationRepository;
import com.example.slabiak.appointmentscheduler.entity.*;
import com.example.slabiak.appointmentscheduler.entity.user.User;
import com.example.slabiak.appointmentscheduler.service.EmailService;
import com.example.slabiak.appointmentscheduler.service.NotificationService;
import com.example.slabiak.appointmentscheduler.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final boolean mailingEnabled;
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    public NotificationServiceImpl(@Value("${mailing.enabled}") boolean mailingEnabled, NotificationRepository notificationRepository, UserService userService, EmailService emailService) {
        this.mailingEnabled = mailingEnabled;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    private static String formatar(LocalDateTime dataHora) {
        return dataHora.format(DATA_HORA);
    }

    @Override
    public void newNotification(String title, String message, String url, User user) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setUrl(url);
        notification.setCreatedAt(new Date());
        notification.setMessage(message);
        notification.setUser(user);
        notificationRepository.save(notification);
    }


    @Override
    public void markAsRead(int notificationId, int userId) {
        Notification notification = notificationRepository.getOne(notificationId);
        if (notification.getUser().getId() == userId) {
            notification.setRead(true);
            notificationRepository.save(notification);
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
    }

    @Override
    public void markAllAsRead(int userId) {
        List<Notification> notifications = notificationRepository.getAllUnreadNotifications(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public Notification getNotificationById(int notificationId) {
        return notificationRepository.getOne(notificationId);
    }

    @Override
    public List<Notification> getAll(int userId) {
        return userService.getUserById(userId).getNotifications();
    }

    @Override
    public List<Notification> getUnreadNotifications(int userId) {
        return notificationRepository.getAllUnreadNotifications(userId);
    }

    @Override
    public void newAppointmentFinishedNotification(Appointment appointment, boolean sendEmail) {
        String title = "Consulta realizada";
        String message = "Sua consulta foi realizada. Se ela não aconteceu, avise até " + formatar(appointment.getEnd().plusHours(24)) + ".";
        String url = "/appointments/" + appointment.getId();
        newNotification(title, message, url, appointment.getCustomer());
        if (sendEmail && mailingEnabled) {
            emailService.sendAppointmentFinishedNotification(appointment);
        }

    }

    @Override
    public void newAppointmentRejectionRequestedNotification(Appointment appointment, boolean sendEmail) {
        String title = "Consulta contestada";
        String message = appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName() + " informou que a consulta não aconteceu. Sua confirmação é necessária.";
        String url = "/appointments/" + appointment.getId();
        newNotification(title, message, url, appointment.getProvider());
        if (sendEmail && mailingEnabled) {
            emailService.sendAppointmentRejectionRequestedNotification(appointment);
        }
    }

    @Override
    public void newNewAppointmentScheduledNotification(Appointment appointment, boolean sendEmail) {
        String title = "Nova consulta agendada";
        String message = "Nova consulta agendada com " + appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName() + " em " + formatar(appointment.getStart()) + ".";
        String url = "/appointments/" + appointment.getId();
        newNotification(title, message, url, appointment.getProvider());
        if (sendEmail && mailingEnabled) {
            emailService.sendNewAppointmentScheduledNotification(appointment);
        }
    }

    @Override
    public void newAppointmentCanceledByCustomerNotification(Appointment appointment, boolean sendEmail) {
        String title = "Consulta cancelada";
        String message = appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName() + " cancelou a consulta de " + formatar(appointment.getStart()) + ".";
        String url = "/appointments/" + appointment.getId();
        newNotification(title, message, url, appointment.getProvider());
        if (sendEmail && mailingEnabled) {
            emailService.sendAppointmentCanceledByCustomerNotification(appointment);
        }
    }

    @Override
    public void newAppointmentCanceledByProviderNotification(Appointment appointment, boolean sendEmail) {
        String title = "Consulta cancelada";
        String message = appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName() + " cancelou a consulta de " + formatar(appointment.getStart()) + ".";
        String url = "/appointments/" + appointment.getId();
        newNotification(title, message, url, appointment.getCustomer());
        if (sendEmail && mailingEnabled) {
            emailService.sendAppointmentCanceledByProviderNotification(appointment);
        }
    }

    public void newInvoice(Invoice invoice, boolean sendEmail) {
        String title = "Novo comprovante";
        String message = "Um comprovante de atendimento foi emitido para você.";
        String url = "/invoices/" + invoice.getId();
        newNotification(title, message, url, invoice.getAppointments().get(0).getCustomer());
        if (sendEmail && mailingEnabled) {
            emailService.sendInvoice(invoice);
        }
    }

    @Override
    public void newExchangeRequestedNotification(Appointment oldAppointment, Appointment newAppointment, boolean sendEmail) {
        String title = "Pedido de troca";
        String message = "Outro paciente pediu para trocar o horário dele com a sua consulta.";
        String url = "/appointments/" + newAppointment.getId();
        newNotification(title, message, url, newAppointment.getCustomer());
        if (sendEmail && mailingEnabled) {
            emailService.sendNewExchangeRequestedNotification(oldAppointment, newAppointment);
        }
    }

    @Override
    public void newExchangeAcceptedNotification(ExchangeRequest exchangeRequest, boolean sendEmail) {
        String title = "Troca aceita";
        String message = "Seu pedido de troca da consulta de " + formatar(exchangeRequest.getRequested().getStart()) + " para " + formatar(exchangeRequest.getRequestor().getStart()) + " foi aceito.";
        String url = "/appointments/" + exchangeRequest.getRequested().getId();
        newNotification(title, message, url, exchangeRequest.getRequested().getCustomer());
        if (sendEmail && mailingEnabled) {
            emailService.sendExchangeRequestAcceptedNotification(exchangeRequest);
        }
    }

    @Override
    public void newExchangeRejectedNotification(ExchangeRequest exchangeRequest, boolean sendEmail) {
        String title = "Troca recusada";
        String message = "Seu pedido de troca da consulta de " + formatar(exchangeRequest.getRequestor().getStart()) + " para " + formatar(exchangeRequest.getRequested().getStart()) + " foi recusado.";
        String url = "/appointments/" + exchangeRequest.getRequestor().getId();
        newNotification(title, message, url, exchangeRequest.getRequestor().getCustomer());
        if (sendEmail && mailingEnabled) {
            emailService.sendExchangeRequestRejectedNotification(exchangeRequest);
        }
    }

    @Override
    public void newAppointmentRejectionAcceptedNotification(Appointment appointment, boolean sendEmail) {
        String title = "Contestação aceita";
        String message = "O estagiário confirmou que a consulta não aconteceu.";
        String url = "/appointments/" + appointment.getId();
        newNotification(title, message, url, appointment.getCustomer());
        if (sendEmail && mailingEnabled) {
            emailService.sendAppointmentRejectionAcceptedNotification(appointment);
        }
    }

    @Override
    public void newChatMessageNotification(ChatMessage chatMessage, boolean sendEmail) {
        String title = "Nova mensagem";
        String message = "Nova mensagem de " + chatMessage.getAuthor().getFirstName() + " sobre a consulta de " + formatar(chatMessage.getAppointment().getStart()) + ".";
        String url = "/appointments/" + chatMessage.getAppointment().getId();
        newNotification(title, message, url, chatMessage.getAuthor() == chatMessage.getAppointment().getProvider() ? chatMessage.getAppointment().getCustomer() : chatMessage.getAppointment().getProvider());
        if (sendEmail && mailingEnabled) {
            emailService.sendNewChatMessageNotification(chatMessage);
        }
    }

}
