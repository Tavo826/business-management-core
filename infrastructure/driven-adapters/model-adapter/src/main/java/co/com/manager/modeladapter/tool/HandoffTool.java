package co.com.manager.modeladapter.tool;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessContext;
import co.com.manager.model.business.CustomerContext;
import co.com.manager.model.message.user.MessageGateway;
import co.com.manager.model.message.user.Text;
import co.com.manager.model.message.user.UserMessageRequest;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HandoffTool {

    private static final String REASON_CUSTOMER_REQUEST = "customer_request";
    private static final String REASON_VETERINARY_QUESTION = "veterinary_question";

    private final MessageGateway messageGateway;

    @Tool("Notifica al dueño del negocio para que contacte al cliente. Usa esta herramienta SOLO en dos casos: (1) el cliente pide explícitamente hablar con una persona o con el dueño; (2) el cliente pregunta por medicamentos veterinarios, dosis, posología o consejo médico/veterinario. NO la uses para preguntas normales de catálogo o pedidos. El teléfono del cliente NO se pasa como parámetro: el sistema lo toma del remitente real de WhatsApp. Llámala como máximo una vez por turno.")
    public String requestOwnerContact(
            @P("Nombre del cliente si lo proporcionó en la conversación; si aún no lo dio, envía 'no proporcionado'") String customerName,
            @P("Motivo de la derivación. Valores permitidos: 'customer_request' (el cliente pidió hablar con una persona) o 'veterinary_question' (consulta de medicamento veterinario o dosis)") String reason) {

        log.info("HandoffTool invocado - razón: {}, cliente: {}", reason, customerName);

        Business business = BusinessContext.get();
        if (business == null) {
            log.error("No se encontró el contexto del negocio");
            return "Error: No se pudo identificar el negocio. Intenta de nuevo.";
        }

        String customerPhone = CustomerContext.get();
        if (customerPhone == null || customerPhone.isBlank()) {
            log.error("No se encontró el teléfono del cliente en el contexto de la conversación");
            return "Error: No se pudo identificar el remitente. Intenta de nuevo.";
        }

        String ownerMessage = formatOwnerNotification(customerName, customerPhone, reason);

        System.out.println("OwnerMessage: " + ownerMessage);

        UserMessageRequest notification = UserMessageRequest.builder()
                .messagingProduct("whatsapp")
                .recipientType("individual")
                .to(business.getPhone())
                .type("text")
                .text(Text.builder()
                        .previewUrl(false)
                        .body(ownerMessage)
                        .build())
                .build();

        try {
            messageGateway.sendMessage(business.getPhoneNumberId(), notification)
                    .doOnSuccess(r -> log.info("Derivación enviada al dueño del negocio: {} - {}", business.getName(), business.getPhone()))
                    .doOnError(e -> log.error("Error al notificar derivación al dueño: {}", e.getMessage()))
                    .subscribe();
            return formatCustomerConfirmation(reason);
        } catch (Exception e) {
            log.error("Error al ejecutar la derivación: {}", e.getMessage(), e);
            return "Error al notificar al dueño. Por favor intenta de nuevo.";
        }
    }

    private String formatOwnerNotification(String customerName, String customerPhone, String reason) {
        String header = switch (reason) {
            case REASON_VETERINARY_QUESTION -> "🩺 *CONSULTA VETERINARIA - REQUIERE TU ATENCIÓN*";
            case REASON_CUSTOMER_REQUEST -> "📞 *EL CLIENTE QUIERE HABLAR CONTIGO*";
            default -> "📞 *NUEVA SOLICITUD DE CONTACTO*";
        };

        String reasonLine = switch (reason) {
            case REASON_VETERINARY_QUESTION ->
                    "El cliente está preguntando por medicamentos veterinarios o dosis. El asistente no responde estas consultas, ponte en contacto con él por WhatsApp.";
            case REASON_CUSTOMER_REQUEST ->
                    "El cliente pidió hablar con una persona del negocio.";
            default -> "El cliente solicita contacto con el negocio.";
        };

        String displayName = (customerName == null || customerName.isBlank() || "no proporcionado".equalsIgnoreCase(customerName.trim()))
                ? "(no proporcionado)"
                : customerName.trim();

        return header + "\n\n"
                + "👤 Cliente: " + displayName + "\n"
                + "📱 Teléfono: " + customerPhone + "\n\n"
                + reasonLine;
    }

    private String formatCustomerConfirmation(String reason) {
        if (REASON_VETERINARY_QUESTION.equals(reason)) {
            return "Las consultas sobre medicamentos veterinarios y dosis se recomienda que se realicen directamente con el negocio. Ya le pasé tu número y se comunicará contigo.";
        }
        return "Listo, le pasé tu número al dueño del negocio. Se comunicará contigo lo antes posible.";
    }
}
