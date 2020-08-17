package com.spring.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    private final DataSource dataSource;

    @Autowired
    public SecurityConfig(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        /*auth.inMemoryAuthentication()
                .withUser("parth")
                .password("spring")
                .roles("USER")
                .and()
                .passwordEncoder(NoOpPasswordEncoder.getInstance());*/

        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .passwordEncoder(new BCryptPasswordEncoder())
            .usersByUsernameQuery("select username, password, enabled"
                    + " from users where username=?")
            .authoritiesByUsernameQuery("select username, authority "
                    + "from authorities where username=?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/abt").hasAnyRole("ADMIN","USER")
            .antMatchers("/").permitAll()
            .anyRequest().authenticated()
            .and().formLogin();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}