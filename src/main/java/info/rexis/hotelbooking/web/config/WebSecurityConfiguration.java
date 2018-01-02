package info.rexis.hotelbooking.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(ManagementServerProperties.BASIC_AUTH_ORDER + 1)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/main", "/reservation-form", "/reservation-show", "/css/**", "/fonts/**", "/js/**", "/images/**", "/locales/**").permitAll()
                .antMatchers("/hotel/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().hasRole("ADMIN")
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password(userpw).roles("USER")
                .and()
                .withUser("admin").password(adminpw).roles("ADMIN");
    }
}
