package in.asmj.kalahandiconnect.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService userDetailsService(PasswordEncoder encoder,
            @Value("${APP_ADMIN_USERNAME:admin}") String username,
            @Value("${APP_ADMIN_PASSWORD:}") String password) {
        if (username == null || username.isBlank()) throw new IllegalStateException("Set APP_ADMIN_USERNAME to the admin username before starting the application.");
        if (password == null || password.isBlank() || password.length() < 12) throw new IllegalStateException("Set APP_ADMIN_PASSWORD to a strong password of at least 12 characters before starting the application.");
        log.info("Admin account configured for username '{}'. Password value is not logged.", username);
        return new InMemoryUserDetailsManager(User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build());
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/news/**", "/submit-news", "/css/**", "/images/**", "/error").permitAll()
                .requestMatchers("/admin/login", "/admin/login/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().permitAll())
            .formLogin(form -> form.loginPage("/admin/login").loginProcessingUrl("/admin/login").defaultSuccessUrl("/admin", true).failureUrl("/admin/login?error=true").permitAll())
            .logout(logout -> logout.logoutUrl("/admin/logout").logoutSuccessUrl("/"));
        return http.build();
    }
}
