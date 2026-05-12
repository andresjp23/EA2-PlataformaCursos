package cursos.ms_03_auth_service.security.jwt;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;


@Service
public class JwtService {

    // Leemos los valores del application.properties

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    // Genera un token JWT a partir del username
    public String generateToken(String username, String role){
        return Jwts.builder()
               .subject(username)
               .claim("role", role)
               .issuedAt(new Date())
               .expiration(new Date(System.currentTimeMillis() + expiration))
               .signWith(getSigningKey())
               .compact();
                
    }

    // Extrae el username del token JWT
    public String extractUsername(String token){
        return Jwts.parser()
               .verifyWith(getSigningKey())
               .build()
               .parseSignedClaims(token)
               .getPayload()
               .getSubject();
    }

    // Verifica que el token sea correcto y no este expirado
    public boolean isTokenValid(String token, String username){
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    // Verifica si el token esta expirado
    private boolean isTokenExpired(String token){
        return Jwts.parser()
               .verifyWith(getSigningKey())
               .build()
               .parseSignedClaims(token)
               .getPayload()
               .getExpiration()
               .before(new Date());
    }

    // Extrae el rol del token JWT
    public String extractRole(String token) {
    return Jwts.parser()
           .verifyWith(getSigningKey())
           .build()
           .parseSignedClaims(token)
           .getPayload()
           .get("role", String.class);
}

    // Convierte la clave secreta en un formato que entiende la libreria JWT
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
