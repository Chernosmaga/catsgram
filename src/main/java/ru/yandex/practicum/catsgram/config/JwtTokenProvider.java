package ru.yandex.practicum.catsgram.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.exception.JwtAuthException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class JwtTokenProvider {
    private final UserDetailsService service;

    @Value("${jwt.header}")
    private String authHeader;
    @Value("${jwt.secret_key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long validityInMillis;

    @Autowired
    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService service) {
        this.service = service;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMillis * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(HS256, "stratosphere")
                .compact();
    }

    public boolean validateToken(String token) throws JwtAuthException {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtAuthException("Токен невалиден или просрочен", UNAUTHORIZED);
        }
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.service.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(authHeader);
    }
}
