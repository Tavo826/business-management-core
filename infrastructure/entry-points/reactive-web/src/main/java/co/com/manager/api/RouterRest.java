package co.com.manager.api;

import co.com.manager.api.handler.BusinessHandler;
import co.com.manager.api.handler.MessageHandler;
import co.com.manager.api.handler.OrderHandler;
import co.com.manager.api.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> messageRoutes(MessageHandler messageHandler) {
        return route(GET("/api/status"), messageHandler::apiStatus)
                .andRoute(POST("/webhook"), messageHandler::handleUserMessage)
                .and(route(GET("/webhook"), messageHandler::verifyConnection));
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        return RouterFunctions.route()
                .POST("/api/users/auth", userHandler::authenticate)
                .GET("/api/users/{id}", userHandler::findUserById)
                .POST("/api/users/", userHandler::createUser)
                .PUT("/api/users/{id}", userHandler::updateUser)
                .DELETE("/api/users/{id}", userHandler::deleteUser)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderRoutes(OrderHandler orderHandler) {
        return RouterFunctions.route()
                .GET("/api/orders/{businessId}/business", orderHandler::findAllOrdersByBusiness)
                .GET("/api/orders/{id}", orderHandler::findOrderById)
                .PATCH("/api/orders/{id}/status", orderHandler::updateOrderStatus)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> businessRoutes(BusinessHandler businessHandler) {
        return RouterFunctions.route()
                .GET("/api/businesses/{id}", businessHandler::findBusinessById)
                .GET("/api/businesses/users/{id}", businessHandler::findBusinessByUserId)
                .POST("/api/businesses", businessHandler::createBusiness)
                .PUT("/api/businesses/{id}", businessHandler::updateBusiness)
                .PUT("/api/businesses/users/{id}", businessHandler::updateBusiness)
                .DELETE("/api/businesses/{id}", businessHandler::deleteBusiness)
                .build();
    }
}
