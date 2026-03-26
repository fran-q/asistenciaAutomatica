package com.appasistencia.configurations;

import com.appasistencia.websocket.FaceWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

// Configuracion: WebSocket para comunicacion de reconocimiento facial en tiempo real
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final FaceWebSocketHandler faceWebSocketHandler;

    public WebSocketConfig(FaceWebSocketHandler faceWebSocketHandler) {
        this.faceWebSocketHandler = faceWebSocketHandler;
    }

    // Registra handler en /ws/face con CORS abierto
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(faceWebSocketHandler, "/ws/face")
                .setAllowedOrigins("*");
    }

    // Aumentar buffer del WebSocket para soportar frames JPEG (~40-80KB)
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(256 * 1024);  // 256KB para frames JPEG
        container.setMaxTextMessageBufferSize(16 * 1024);     // 16KB para mensajes JSON
        return container;
    }
}
