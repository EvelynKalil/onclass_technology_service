package com.onclass.technology.infrastructure.entrypoints;

import com.onclass.technology.infrastructure.entrypoints.handler.TechnologyHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(TechnologyHandlerImpl handler) {
        return route(POST("/technology"), handler::create)
                .andRoute(GET("/technologies"), handler::list)
                .andRoute(GET("/technologies/{id}"), handler::findById);
    }
}
