package gimnasio.config;

import gimnasio.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMINISTRADOR = "ADMINISTRADOR";
    private static final String ENTRENADOR = "ENTRENADOR";
    private static final String MIEMBRO = "MIEMBRO";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Públicos: inicio de sesión, registro de miembros y la página web
                        .requestMatchers("/gimnasioAPI/auth/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/gimnasioAPI/miembros").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/index.html").permitAll()

                        // Usuarios: solo el administrador los gestiona; el entrenador puede consultar miembros
                        .requestMatchers("/gimnasioAPI/administrador/**").hasRole(ADMINISTRADOR)
                        .requestMatchers(HttpMethod.GET, "/gimnasioAPI/miembros/**").hasAnyRole(ADMINISTRADOR, ENTRENADOR)
                        .requestMatchers("/gimnasioAPI/miembros/**").hasRole(ADMINISTRADOR)
                        .requestMatchers(HttpMethod.GET, "/gimnasioAPI/entrenador/**").authenticated()
                        .requestMatchers("/gimnasioAPI/entrenador/**").hasRole(ADMINISTRADOR)

                        // Catálogos: todos consultan, solo el administrador los modifica
                        .requestMatchers(HttpMethod.GET, "/gimnasioAPI/categorias/**", "/gimnasioAPI/salas/**").authenticated()
                        .requestMatchers("/gimnasioAPI/categorias/**", "/gimnasioAPI/salas/**").hasRole(ADMINISTRADOR)

                        // Clases: todos consultan; administrador y entrenador las gestionan
                        .requestMatchers(HttpMethod.GET, "/gimnasioAPI/clases/**").authenticated()
                        .requestMatchers("/gimnasioAPI/clases/**").hasAnyRole(ADMINISTRADOR, ENTRENADOR)

                        // Inscripciones: el miembro solo ve y gestiona las suyas (se valida en el servicio)
                        .requestMatchers(HttpMethod.GET, "/gimnasioAPI/inscripciones/**").authenticated()
                        .requestMatchers("/gimnasioAPI/inscripciones/**").hasAnyRole(ADMINISTRADOR, MIEMBRO)

                        // Asistencias: administrador y entrenador las registran; el miembro solo ve las suyas
                        .requestMatchers(HttpMethod.GET, "/gimnasioAPI/asistencias/clases/**").hasAnyRole(ADMINISTRADOR, ENTRENADOR)
                        .requestMatchers(HttpMethod.GET, "/gimnasioAPI/asistencias/**").authenticated()
                        .requestMatchers("/gimnasioAPI/asistencias/**").hasAnyRole(ADMINISTRADOR, ENTRENADOR)

                        .anyRequest().authenticated() // Requiere JWT válido para el resto
                )
                .exceptionHandling(e -> e
                        // Sin token válido: 401 Unauthorized
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        // Con token, pero sin el rol necesario: 403 Forbidden
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\":\"Tu rol no tiene permiso para esta acción\"}");
                        }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
