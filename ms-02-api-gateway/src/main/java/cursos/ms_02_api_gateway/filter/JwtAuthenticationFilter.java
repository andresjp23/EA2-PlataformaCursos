package cursos.ms_02_api_gateway.filter;

import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter implements HandlerInterceptor {

    // La clave secreta se lee desde application.yaml
    @Value("${jwt.secret}")
    private String secretKey;

    // Rutas que NO necesitan token para acceder
    private static final List<String> PUBLIC_ROUTES = List.of(
        "/auth/login",
        "/auth/register"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();

        // Si la ruta es pública la dejamos pasar sin revisar nada
        if (PUBLIC_ROUTES.contains(path)) {
            return true;
        }

        // Obtenemos el header Authorization
        String authHeader = request.getHeader("Authorization");

        // Si no viene el header o no empieza con "Bearer " rechazamos el request
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token requerido");
            return false;
        }

        // Extraemos el token quitando el prefijo "Bearer "
        String token = authHeader.substring(7);

        try {
            // Intentamos validar el token con nuestra clave secreta
            // Si el token es invalido o expirado lanza una excepcion
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);

            // Si llegamos hasta aca el token es valido, dejamos pasar el request
            return true;

        } catch (Exception e) {
            // Token invalido o expirado
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token invalido o expirado");
            return false;
        }
    }

    // Convierte la clave secreta al formato que necesita la libreria JWT
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}