package bg.startit.hackathon.airquiality.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        .cors()
          .and()
        .csrf()
          .disable()
        .formLogin()
          .and()
        .logout()
          .and()
        .headers()
          .frameOptions().sameOrigin().and()
        .authorizeRequests()
        // always public
          .antMatchers("/system/db/**").permitAll() // DB console has own authentication
          .antMatchers("/login").permitAll() // Allow people to use login page
          .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll() // Allow user registration
        // everything else requires authentication
          .antMatchers("/**").authenticated()
    ;
    // @formatter:on
  }

  @Bean
  PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
