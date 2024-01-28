package ru.yandex.practicum.catsgram.config;

import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.yandex.practicum.catsgram.exception.JwtAuthException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider provider;

    public JwtTokenFilter(JwtTokenProvider provider) {
        this.provider = provider;
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = provider.resolveToken((HttpServletRequest) servletRequest);
        try {
            if (token != null && provider.validateToken(token)) {
                Authentication authentication = provider.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtAuthException exception) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) servletResponse).sendError(exception.getStatus().value());
            throw new JwtAuthException("Токен недействителен или просрочен");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
