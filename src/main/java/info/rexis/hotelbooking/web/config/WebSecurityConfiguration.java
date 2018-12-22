package info.rexis.hotelbooking.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login", "/logout").permitAll()
                .antMatchers("/", "/main", "/reservation-form", "/reservation-show", "/css/**", "/fonts/**", "/js/**", "/images/**", "/locales/**").permitAll()
                .antMatchers("/hotel/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/**").hasRole("ADMIN")
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/hotel/hotel-list")
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // prevents spring boot auto-configuration of an AuthenticationManager
        // among other things this removes the useless default security password
        return super.authenticationManagerBean();
    }

    @Value("${hotelbooking.user.pw}")
    private String userpw;

    @Value("${hotelbooking.admin.pw}")
    private String adminpw;

    @Bean
    public UserDetailsService userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password(encoder.encode(userpw)).roles("USER").build());
        manager.createUser(User.withUsername("admin").password(encoder.encode(adminpw)).roles("ADMIN").build());
        return manager;
    }
}
