package com.josec.catalog.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
/**
 * Clase Para crear y leer Tokens de sesión.
 * El tiempo de expiración es de 1 hora.
 */
public class JwtUtil {

    //Clave larga 256 bits
    private final String SECRET = "MiClaveSecretaYExtraLargaParaValidarLosTokensDeMiAppNuevaEn2026";

    //Convertir String a una clave criptográfica real
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    //Tiempo de validez: 1 hora en milisegundos
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    /**
     * FABRICANTE DE TOKENS - Genera un token de sesión para el username entrante
     *
     * @param username propietario del token
     * @return String token generado
     */
    public String generateToken(String username, int userId, String role) {
        return Jwts.builder()
                .setSubject(username) // A quién pertenece
                .claim("userId", userId) // Claim con userId para incrustarlo
                .claim("role", role) // Rol de usuario
                .setIssuedAt(new Date()) // Cuándo se creó
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Cuándo caduca
                .signWith(key, SignatureAlgorithm.HS256) // firma secreta
                .compact(); // Convertir a string
    }

    /**
     * LECTOR DE TOKENS - Extrae el nombre de usuario
     *
     * @param token del que se va extraer.
     * @return username
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * EXTRACTOR DE ID DE USUARIO - Extrae el userId del token
     *
     * @param token del que se va extraer
     * @return userId
     */
    public Integer extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Integer.class);
    }

    /**
     * EXTRACTOR DE ROL DE USUARIO
     *
     * @param token de inicio de sesión
     * @return Rol del usuario
     */
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    /**
     * DETECTOR DE VALIDEZ DEL TOKEN - Comprueba si el token es válido
     * @param token a validar
     * @return true si es válido, false si no
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            //Si la firma está mal o el token ha caducado, saltará una excepción
            return false;
        }
    }
}
