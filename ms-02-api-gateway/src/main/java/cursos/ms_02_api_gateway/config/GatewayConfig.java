package cursos.ms_02_api_gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cursos.ms_02_api_gateway.filter.JwtAuthenticationFilter;

@Configuration
public class GatewayConfig  implements WebMvcConfigurer{

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registramos el filtro JWT para que intercepte TODOS los requests
        // Las rutas publicas se manejan dentro del propio filtro
        registry.addInterceptor(jwtAuthenticationFilter)
                .addPathPatterns("/**");
    }
}
