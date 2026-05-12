package cursos.ms_03_auth_service.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cursos.ms_03_auth_service.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Obtenemos el header Authorization del request
        final String authHeader = request.getHeader("Authorization");

        // Si no viene el header o no empieza con "Bearer ", dejamos pasar el request sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token quitando el prefijo "Bearer "
        final String token = authHeader.substring(7);

        // Extraemos el username desde el token
        final String username = jwtService.extractUsername(token);

        // Si el username existe y el usuario aún no está autenticado en el contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargamos el usuario desde la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validamos el token
            if (jwtService.isTokenValid(token, username)) {

                // Creamos el objeto de autenticación y lo metemos en el contexto de Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuamos con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}
