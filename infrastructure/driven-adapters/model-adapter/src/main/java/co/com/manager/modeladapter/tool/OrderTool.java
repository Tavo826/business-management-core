package co.com.manager.modeladapter.tool;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessContext;
import co.com.manager.model.message.user.MessageGateway;
import co.com.manager.model.message.user.Text;
import co.com.manager.model.message.user.UserMessageRequest;
import co.com.manager.model.order.Order;
import co.com.manager.model.order.OrderRepository;
import co.com.manager.model.order.OrderItem;
import co.com.manager.model.order.Status;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OrderTool {

    private final OrderRepository orderRepository;
    private final MessageGateway messageGateway;

    @Tool("Crea una orden de compra cuando el cliente confirma su pedido. Usa esta herramienta SOLO cuando el cliente haya confirmado los productos, cantidades, su nombre y dirección.")
    public String createOrder(
            @P("Nombre completo del cliente") String customerName,
            @P("Teléfono del cliente") String customerPhone,
            @P("Dirección de entrega del cliente") String customerAddress,
            @P("Lista de productos separados por punto y coma. Cada producto tiene el formato: nombre,cantidad,precio. Ejemplo: 'DONKAT GATITOS X 1 kg,2,12380;DOG SHOW ADULTO RAZA PEQUEÑA X 1 kg,1,14761'") String products) {

        log.info("OrderTool invocado - creando orden para cliente: {}", customerName);

        Business business = BusinessContext.get();
        if (business == null) {
            log.error("No se encontró el contexto del negocio");
            return "Error: No se pudo identificar el negocio. Intenta de nuevo.";
        }

        List<OrderItem> items = parseProducts(products);

        log.info("Items a comprar: {}", items);

        if (items.isEmpty()) {
            return "Error: No se pudieron interpretar los productos. Verifica el formato.";
        }

        BigDecimal total = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .businessId(business.getNit())
                .customerName(customerName)
                .customerPhone(customerPhone)
                .customerAddress(customerAddress)
                .status(String.valueOf(Status.PENDING))
                .totalAmount(total)
                .items(items)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            Order savedOrder = orderRepository.saveOrder(order).block();
            notifyBusinessOwner(savedOrder);
            return formatOrderConfirmation(savedOrder);
        } catch (Exception e) {
            log.error("Error al crear la orden: {}", e.getMessage(), e);
            return "Error al crear la orden. Por favor intenta de nuevo.";
        }
    }

    private List<OrderItem> parseProducts(String products) {
        List<OrderItem> items = new ArrayList<>();
        String[] productEntries = products.split(";");

        for (String entry : productEntries) {
            String[] parts = entry.trim().split(",");
            if (parts.length >= 3) {
                try {
                    items.add(OrderItem.builder()
                            .productName(parts[0].trim())
                            .quantity(Integer.parseInt(parts[1].trim()))
                            .unitPrice(new BigDecimal(parts[2].trim()))
                            .build());
                } catch (NumberFormatException e) {
                    log.warn("No se pudo parsear el producto: {}", entry);
                }
            }
        }
        return items;
    }

    private void notifyBusinessOwner(Order order) {
        Business business = BusinessContext.get();
        if (business == null) {
            log.error("No se pudo notificar al dueño: contexto del negocio no disponible");
            return;
        }

        String message = formatOwnerNotification(order);

        UserMessageRequest notification = UserMessageRequest.builder()
                .messagingProduct("whatsapp")
                .recipientType("individual")
                .to(business.getPhone())
                .type("text")
                .text(Text.builder()
                        .previewUrl(false)
                        .body(message)
                        .build())
                .build();

        log.info("Notifying business owner from: {} - notification: {}", business.getPhoneNumberId(), notification);

        messageGateway.sendMessage(business.getPhoneNumberId(), notification)
                .doOnSuccess(r -> log.info("Notificación enviada al dueño del negocio: {}", business.getName()))
                .doOnError(e -> log.error("Error al notificar al dueño: {}", e.getMessage()))
                .subscribe();
    }

    private String formatOwnerNotification(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("🛒 *NUEVA ORDEN DE COMPRA*\n\n");
        sb.append("📋 Orden: ").append(order.getId()).append("\n");
        sb.append("👤 Cliente: ").append(order.getCustomerName()).append("\n");
        sb.append("📱 Teléfono: ").append(order.getCustomerPhone()).append("\n");
        sb.append("📍 Dirección: ").append(order.getCustomerAddress()).append("\n\n");
        sb.append("*Productos:*\n");

        for (OrderItem item : order.getItems()) {
            sb.append("  • ").append(item.getProductName())
                    .append(" x").append(item.getQuantity())
                    .append(" - $").append(item.getUnitPrice()).append("\n");
        }

        sb.append("\n💰 *Total: $").append(order.getTotalAmount()).append("*\n");
        sb.append("\nRevisa la orden en el panel y contacta al cliente para confirmar el pago.");
        return sb.toString();
    }

    private String formatOrderConfirmation(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Orden creada exitosamente.\n");
        sb.append("Número de orden: ").append(order.getId()).append("\n");
        sb.append("Total: $").append(order.getTotalAmount()).append("\n");
        sb.append("El dueño del negocio ha sido notificado y se comunicará contigo para confirmar el pago.");
        return sb.toString();
    }
}
