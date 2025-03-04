package cit.edu.studyspace.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/oauth2/authorization/google").permitAll() // Allow public access
                        .anyRequest().authenticated() // Protect other endpoints
                )
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/user", true)) // Redirect after login
                .logout(logout -> logout.logoutSuccessUrl("/")) // Redirect after logout
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF (if necessary)
                .build();
    }
}
