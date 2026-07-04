package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 02.07.2026
 * Time: 21:27
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.from-name:KFZ Technik GDG}")
    private String fromName;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public void sendAppointmentConfirmation(Appointment appointment) {
        String recipient = appointment.getUser() != null ? appointment.getUser().getEmail() : null;

        if (recipient == null || recipient.isBlank()) {
            log.warn("Keine E-Mail-Adresse für Termin {} vorhanden – Bestätigungsmail wird nicht versendet.", appointment.getId());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(recipient);
            helper.setSubject("Terminbestätigung – " + safeServiceType(appointment));
            helper.setText(buildHtmlBody(appointment), true);

            mailSender.send(message);
            log.info("Terminbestätigung an {} für Termin {} versendet.", recipient, appointment.getId());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Terminbestätigung für Termin {} konnte nicht erstellt werden: {}", appointment.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Terminbestätigung für Termin {} konnte nicht versendet werden: {}", appointment.getId(), e.getMessage());
        }
    }

    public void sendAppointmentStatusUpdate(Appointment appointment) {
        String recipient = appointment.getUser() != null ? appointment.getUser().getEmail() : null;

        if (recipient == null || recipient.isBlank()) {
            log.warn("Keine E-Mail-Adresse für Termin {} vorhanden – Status-Update-Mail wird nicht versendet.", appointment.getId());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            String statusLabel = statusLabel(appointment.getStatus() != null ? appointment.getStatus().name() : "NEU");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(recipient);
            helper.setSubject("Terminstatus aktualisiert: " + statusLabel + " – " + safeServiceType(appointment));
            helper.setText(buildStatusUpdateHtmlBody(appointment), true);

            mailSender.send(message);
            log.info("Status-Update-Mail ({}) an {} für Termin {} versendet.", statusLabel, recipient, appointment.getId());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Status-Update-Mail für Termin {} konnte nicht erstellt werden: {}", appointment.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Status-Update-Mail für Termin {} konnte nicht versendet werden: {}", appointment.getId(), e.getMessage());
        }
    }

    /**
     * Versendet die E-Mail mit dem "Passwort zurücksetzen"-Link an den Nutzer.
     * Wird stillschweigend übersprungen, wenn der Nutzer keine E-Mail-Adresse hat
     * (kommt praktisch nicht vor, da E-Mail Pflichtfeld ist) oder der Versand fehlschlägt –
     * ein Mail-Fehler soll den "Passwort vergessen"-Ablauf nicht mit einem 500er abbrechen.
     *
     * @param user      der Nutzer, für den ein neues Passwort angefordert wurde
     * @param resetLink der fertige Link inkl. Token, den der Nutzer anklickt
     */
    public void sendPasswordResetEmail(User user, String resetLink) {
        String recipient = user.getEmail();

        if (recipient == null || recipient.isBlank()) {
            log.warn("Keine E-Mail-Adresse für Passwort-Reset (User {}) vorhanden – E-Mail wird nicht versendet.", user.getId());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(recipient);
            helper.setSubject("Passwort zurücksetzen – " + fromName);
            helper.setText(buildPasswordResetHtmlBody(user, resetLink), true);

            mailSender.send(message);
            log.info("Passwort-Reset-Mail an {} versendet.", recipient);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Passwort-Reset-Mail konnte nicht erstellt werden: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Passwort-Reset-Mail konnte nicht versendet werden: {}", e.getMessage());
        }
    }

    private String safeServiceType(Appointment appointment) {
        return (appointment.getServiceType() != null && !appointment.getServiceType().isBlank())
                ? appointment.getServiceType()
                : "Ihre Leistung";
    }

    private String buildHtmlBody(Appointment appointment) {
        String customerName = (appointment.getCustomerName() != null && !appointment.getCustomerName().isBlank())
                ? appointment.getCustomerName() : "Kunde/-in";
        String dateStr = appointment.getPreferredDate() != null ? appointment.getPreferredDate().format(DATE_FMT) : "-";
        String timeStr = appointment.getPreferredDate() != null ? appointment.getPreferredDate().format(TIME_FMT) + " Uhr" : "-";
        String vehicle = (appointment.getVehicle() != null && !appointment.getVehicle().isBlank()) ? appointment.getVehicle() : "-";
        String licensePlate = (appointment.getVehicleEntity() != null && appointment.getVehicleEntity().getLicensePlate() != null)
                ? appointment.getVehicleEntity().getLicensePlate() : "-";
        String priceStr = appointment.getPrice() != null
                ? NumberFormat.getCurrencyInstance(new Locale("de", "AT")).format(appointment.getPrice())
                : "-";
        String duration = appointment.getDurationMinutes() != null ? appointment.getDurationMinutes() + " Min." : "-";
        String note = (appointment.getNote() != null && !appointment.getNote().isBlank()) ? appointment.getNote() : null;
        String statusLabel = statusLabel(appointment.getStatus() != null ? appointment.getStatus().name() : "NEU");

        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"font-family:Arial,Helvetica,sans-serif;max-width:560px;margin:0 auto;color:#1a1a1a;\">");
        sb.append("<div style=\"background:#111;padding:20px 24px;border-radius:8px 8px 0 0;\">");
        sb.append("<h1 style=\"color:#ff6b00;margin:0;font-size:22px;\">").append(escape(fromName)).append("</h1>");
        sb.append("</div>");
        sb.append("<div style=\"border:1px solid #eee;border-top:none;padding:24px;border-radius:0 0 8px 8px;\">");
        sb.append("<h2 style=\"margin-top:0;font-size:18px;\">Terminbestätigung</h2>");
        sb.append("<p>Hallo ").append(escape(customerName)).append(",</p>");
        sb.append("<p>vielen Dank für Ihre Terminanfrage. Wir haben folgende Daten erhalten:</p>");

        sb.append("<table style=\"width:100%;border-collapse:collapse;margin:16px 0;\">");
        sb.append(row("Leistung: ", safeServiceType(appointment)));
        sb.append(row("Datum: ", dateStr));
        sb.append(row("Uhrzeit: ", timeStr));
        sb.append(row("Dauer: ", duration));
        sb.append(row("Preis: ", priceStr));
        sb.append(row("Fahrzeug: ", vehicle));
        sb.append(row("Kennzeichen: ", licensePlate));
        sb.append(row("Status: ", statusLabel));
        if (note != null) {
            sb.append(row("Anmerkung", note));
        }
        sb.append("</table>");

        sb.append("<p style=\"color:#555;font-size:14px;\">Sie erhalten eine weitere Nachricht, sobald Ihr Termin bestätigt wurde. Bei Fragen können Sie uns jederzeit kontaktieren.</p>");
        sb.append("<p style=\"margin-top:24px;\">Mit freundlichen Grüßen<br/>Ihr ").append(escape(fromName)).append(" Team</p>");
        sb.append("</div>");
        sb.append("<p style=\"color:#999;font-size:12px;text-align:center;margin-top:12px;\">Dies ist eine automatisch generierte E-Mail, bitte antworten Sie nicht darauf.</p>");
        sb.append("</div>");
        return sb.toString();
    }

    private String buildStatusUpdateHtmlBody(Appointment appointment) {
        String customerName = (appointment.getCustomerName() != null && !appointment.getCustomerName().isBlank())
                ? appointment.getCustomerName() : "Kunde/-in";
        String dateStr = appointment.getPreferredDate() != null ? appointment.getPreferredDate().format(DATE_FMT) : "-";
        String timeStr = appointment.getPreferredDate() != null ? appointment.getPreferredDate().format(TIME_FMT) + " Uhr" : "-";
        String vehicle = (appointment.getVehicle() != null && !appointment.getVehicle().isBlank()) ? appointment.getVehicle() : "-";
        String licensePlate = (appointment.getVehicleEntity() != null && appointment.getVehicleEntity().getLicensePlate() != null)
                ? appointment.getVehicleEntity().getLicensePlate() : "-";
        String priceStr = appointment.getPrice() != null
                ? NumberFormat.getCurrencyInstance(new Locale("de", "AT")).format(appointment.getPrice())
                : "-";
        String duration = appointment.getDurationMinutes() != null ? appointment.getDurationMinutes() + " Min." : "-";
        String note = (appointment.getNote() != null && !appointment.getNote().isBlank()) ? appointment.getNote() : null;
        String statusLabel = statusLabel(appointment.getStatus() != null ? appointment.getStatus().name() : "NEU");
        String introText = statusIntroText(appointment.getStatus() != null ? appointment.getStatus().name() : "NEU");

        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"font-family:Arial,Helvetica,sans-serif;max-width:560px;margin:0 auto;color:#1a1a1a;\">");
        sb.append("<div style=\"background:#111;padding:20px 24px;border-radius:8px 8px 0 0;\">");
        sb.append("<h1 style=\"color:#ff6b00;margin:0;font-size:22px;\">").append(escape(fromName)).append("</h1>");
        sb.append("</div>");
        sb.append("<div style=\"border:1px solid #eee;border-top:none;padding:24px;border-radius:0 0 8px 8px;\">");
        sb.append("<h2 style=\"margin-top:0;font-size:18px;\">Terminstatus aktualisiert</h2>");
        sb.append("<p>Hallo ").append(escape(customerName)).append(",</p>");
        sb.append("<p>").append(escape(introText)).append("</p>");

        sb.append("<table style=\"width:100%;border-collapse:collapse;margin:16px 0;\">");
        sb.append(row("Leistung", safeServiceType(appointment)));
        sb.append(row("Datum", dateStr));
        sb.append(row("Uhrzeit", timeStr));
        sb.append(row("Dauer", duration));
        sb.append(row("Preis", priceStr));
        sb.append(row("Fahrzeug", vehicle));
        sb.append(row("Kennzeichen", licensePlate));
        sb.append(row("Status", statusLabel));
        if (note != null) {
            sb.append(row("Anmerkung", note));
        }
        sb.append("</table>");

        sb.append("<p style=\"color:#555;font-size:14px;\">Bei Fragen können Sie uns jederzeit kontaktieren.</p>");
        sb.append("<p style=\"margin-top:24px;\">Mit freundlichen Grüßen<br/>Ihr ").append(escape(fromName)).append(" Team</p>");
        sb.append("</div>");
        sb.append("<p style=\"color:#999;font-size:12px;text-align:center;margin-top:12px;\">Dies ist eine automatisch generierte E-Mail, bitte antworten Sie nicht darauf.</p>");
        sb.append("</div>");
        return sb.toString();
    }

    private String buildPasswordResetHtmlBody(User user, String resetLink) {
        String customerName = (user.getFirstName() != null && !user.getFirstName().isBlank())
                ? user.getFirstName() : "Kunde/-in";

        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"font-family:Arial,Helvetica,sans-serif;max-width:560px;margin:0 auto;color:#1a1a1a;\">");
        sb.append("<div style=\"background:#111;padding:20px 24px;border-radius:8px 8px 0 0;\">");
        sb.append("<h1 style=\"color:#ff6b00;margin:0;font-size:22px;\">").append(escape(fromName)).append("</h1>");
        sb.append("</div>");
        sb.append("<div style=\"border:1px solid #eee;border-top:none;padding:24px;border-radius:0 0 8px 8px;\">");
        sb.append("<h2 style=\"margin-top:0;font-size:18px;\">Passwort zurücksetzen</h2>");
        sb.append("<p>Hallo ").append(escape(customerName)).append(",</p>");
        sb.append("<p>wir haben eine Anfrage erhalten, das Passwort für Ihr Konto zurückzusetzen. ")
                .append("Klicken Sie auf den folgenden Button, um ein neues Passwort zu vergeben:</p>");
        sb.append("<p style=\"text-align:center;margin:28px 0;\">");
        sb.append("<a href=\"").append(resetLink).append("\" style=\"background:#ff6b00;color:#fff;text-decoration:none;padding:12px 28px;border-radius:6px;font-weight:600;display:inline-block;\">Neues Passwort vergeben</a>");
        sb.append("</p>");
        sb.append("<p style=\"color:#555;font-size:14px;\">Der Link ist 60 Minuten gültig. Falls Sie diese Anfrage nicht gestellt haben, ")
                .append("können Sie diese E-Mail einfach ignorieren – Ihr Passwort bleibt unverändert.</p>");
        sb.append("<p style=\"margin-top:24px;\">Mit freundlichen Grüßen<br/>Ihr ").append(escape(fromName)).append(" Team</p>");
        sb.append("</div>");
        sb.append("<p style=\"color:#999;font-size:12px;text-align:center;margin-top:12px;\">Dies ist eine automatisch generierte E-Mail, bitte antworten Sie nicht darauf.</p>");
        sb.append("</div>");
        return sb.toString();
    }

    private String statusIntroText(String status) {
        return switch (status) {
            case "BESTÄTIGT" -> "gute Nachrichten – Ihr Termin wurde bestätigt.";
            case "ABGELEHNT" -> "leider müssen wir Ihnen mitteilen, dass Ihr Termin abgelehnt wurde.";
            case "ABGESCHLOSSEN" -> "Ihr Termin wurde als abgeschlossen markiert. Vielen Dank für Ihren Besuch!";
            case "NEU" -> "der Status Ihres Termins wurde aktualisiert.";
            default -> "der Status Ihres Termins wurde aktualisiert.";
        };
    }

    private String row(String label, String value) {
        return "<tr>"
                + "<td style=\"padding:8px 0;color:#777;font-size:14px;width:140px;border-bottom:1px solid #f0f0f0;\">" + escape(label) + "</td>"
                + "<td style=\"padding:8px 0;font-weight:600;font-size:14px;border-bottom:1px solid #f0f0f0;\">" + escape(value) + "</td>"
                + "</tr>";
    }

    private String statusLabel(String status) {
        return switch (status) {
            case "NEU" -> "Neu eingegangen";
            case "AUSSTEHEND" -> "Ausstehend";
            case "BESTÄTIGT" -> "Bestätigt";
            case "ABGELEHNT" -> "Abgelehnt";
            case "ABGESCHLOSSEN" -> "Abgeschlossen";
            default -> status;
        };
    }

    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}