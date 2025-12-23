package co.com.manager.api;

import co.com.manager.api.handler.BusinessHandler;
import co.com.manager.api.handler.MessageHandler;
import co.com.manager.api.handler.UserHandler;
import co.com.manager.model.business.Business;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> messageRoutes(MessageHandler messageHandler) {
        return route(GET("/api/status"), messageHandler::apiStatus)
                .andRoute(POST("/webhook"), messageHandler::listenPOSTUseCase)
                .and(route(GET("/webhook"), messageHandler::verifyConnection));
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        return RouterFunctions.route()
                .GET("/api/users/{id}", userHandler::findUserById)
                .POST("/api/users", userHandler::createUser)
                .PUT("/api/users/{id}", userHandler::updateUser)
                .DELETE("/api/users/{id}", userHandler::deleteUser)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> businessRoutes(BusinessHandler businessHandler) {
        return RouterFunctions.route()
                .GET("/api/business/{id}", businessHandler::findBusinessById)
                .GET("/api/business/users/{id}", businessHandler::findBusinessByUserId)
                .POST("/api/business", businessHandler::createBusiness)
                .PUT("/api/business/{id}", businessHandler::updateBusiness)
                .PUT("/api/business/users/{id}", businessHandler::updateBusiness)
                .DELETE("/api/business/{id}", businessHandler::deleteBusiness)
                .build();
    }
}
