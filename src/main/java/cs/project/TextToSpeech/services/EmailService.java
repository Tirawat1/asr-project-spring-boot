package cs.project.TextToSpeech.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    private final String appUrl = "#";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendHtmlEmail(String to) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("ASR Project: Invite to Join Workspace");
        helper.setFrom(fromEmail);

        // HTML Content
        String htmlContent = "<html><body>" +
                "<h1 style='color: #333;'>You're Invited to Join Our Workspace! 🎉</h1>" +
                "<p style='color: #555; font-size: 16px;'>Hello,</p>" +
                "<p style='color: #555; font-size: 16px;'>You have been invited to join <strong>Our Workspace</strong>. Collaborate with your team, share ideas, and stay productive!</p>" +
                "<p style='color: #555; font-size: 16px;'>Click the button below to join:</p>" +
                "<a href='" + appUrl + "' style='display: inline-block; background-color: #007bff; color: #ffffff; text-decoration: none; font-size: 18px; padding: 12px 24px; border-radius: 5px; font-weight: bold;'>Join Now</a>" +
                "<p style='font-size: 14px; color: #777;'>If you didn’t request this invitation, please ignore this email.</p>" +
                "</body></html>";

        helper.setText(htmlContent, true);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new MessagingException("Failed to send email", e);
        }
    }
}
