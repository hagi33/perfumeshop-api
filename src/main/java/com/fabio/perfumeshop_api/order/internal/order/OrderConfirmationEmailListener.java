package com.fabio.perfumeshop_api.order.internal.order;


import jakarta.mail.MessagingException;   // ojo: es jakarta.mail
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.logging.Logger;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConfirmationEmailListener {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onOrderPaid(OrderPaidEvent event){
        try {
            // 1. Preparar los datos para la plantilla
            Context context = new Context();
            context.setVariable("orderId", event.orderId());
            context.setVariable("total", event.total());
            context.setVariable("items", event.items());

            // 2. Thymeleaf genera el HTML a partir de la plantilla + los datos
            String htmlBody = templateEngine.process("order-confirmation", context);

            // 3. Construir y enviar el correo
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(event.recipientEmail());
            helper.setSubject("Confirmación de tu pedido #" + event.orderId());
            helper.setFrom("noreply@perfumeshop.com");
            helper.setText(htmlBody, true);   // true = es HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            // El email es secundario: si falla, se registra pero NO se relanza,
            // para no afectar al pago (que ya está confirmado y guardado).
            log.error("No se puede enviar el email de confirmación del pedido #{} a {}",
                    event.orderId(), event.recipientEmail());
        }



}
}
