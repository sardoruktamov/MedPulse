package api.medpulse.uz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // Bu fayl foydalanuvchilarni jonli efirga ulaydi.

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Frontend shu manzil orqali jonli ulanadi
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // Xabarlarni tarqatish kanali
        registry.setApplicationDestinationPrefixes("/app"); // Xabarlarni qabul qilish kanali
    }
}
