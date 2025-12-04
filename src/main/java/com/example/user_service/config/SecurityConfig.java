package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.example.user_service.service.CustomUserDetailsService;

/**
 * Security configuration for the application
 * Configura autenticación HTTP Basic y autorización basada en roles
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    // Constructor manual para evitar problemas con Lombok en algunos IDEs
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configura la cadena de filtros de seguridad
     * Define qué rutas son públicas y cuáles requieren autenticación/autorización
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF para APIs REST
                .csrf(csrf -> csrf.disable())
                // Configurar CORS
                .cors(withDefaults())
                // Configurar autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // ============= RUTAS PÚBLICAS =============
                        // Registro y documentación Swagger
                        .requestMatchers("/api/v1/users/register/**").permitAll()
                        .requestMatchers("/api/v1/users/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()

                        // ============= ENDPOINTS INTERNOS PARA MICROSERVICIOS =============
                        // Estos endpoints NO requieren autenticación para comunicación interna
                        // Usados por graphql-profile-service y otros microservicios
                        .requestMatchers("/api/v1/users/*/exists").permitAll()
                        .requestMatchers("/api/v1/users/internal/**").permitAll()

                        // ============= USUARIOS =============
                        // Obtener usuario por ID - solo el propio usuario o admin
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}").authenticated()
                        // Actualizar perfil - solo el propio usuario o admin
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/profile").authenticated()
                        // Cambiar contraseña - solo el propio usuario
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/password").authenticated()
                        // Logout - solo el propio usuario
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/{userId}/logout").authenticated()
                        // ============= PERFILES =============
                        // Solo el usuario propietario puede gestionar sus perfiles
                        .requestMatchers("/api/v1/profiles/**").authenticated()
                        // ============= MUSIC GENRES (PÚBLICO) =============
                        // Catálogo de géneros es público para que usuarios puedan explorar
                        .requestMatchers(HttpMethod.GET, "/api/v1/genres/**").permitAll()
                        // ============= USER PREFERENCES =============
                        // Solo el usuario propietario puede gestionar sus preferencias
                        .requestMatchers("/api/v1/users/*/preferences/**").authenticated()
                        // ============= OTRAS RUTAS =============
                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated())
                // Usar HTTP Basic Authentication (credenciales en cada request)
                .httpBasic(withDefaults())
                // Habilitar Remember Me
                .rememberMe(withDefaults())
                // Configurar Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll())
                // Cambiar a IF_REQUIRED para permitir sesiones (necesario para Swagger)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }

    /**
     * Configura el proveedor de autenticación DAO
     * Usa CustomUserDetailsService y BCrypt para autenticación
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean del AuthenticationManager para autenticación programática
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Codificador de contraseñas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
