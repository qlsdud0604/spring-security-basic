package com.example.springsecuritybasic.config;

import com.example.springsecuritybasic.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity   // 스프링 시큐리티 필터가 스프링 필터 체인에 등록됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)   // "@Secured", "@PreAuthorize" 애너테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean   // 해당 메서드의 리턴되는 객체를 IoC로 등록해줌
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()   // 인증 필요
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")   // ROLE_ADMIN, ROLE_MANAGER 권한 필요
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")   // ROLE_ADMIN 권한 필요
                .anyRequest().permitAll()   // 다른 요청은 모두 허용
                .and().formLogin().loginPage("/loginForm")   // 로그인 페이지의 기본 경로를 설정
                .loginProcessingUrl("/login")   // "/login" 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해 줌 (따라서, 컨트롤러 영역에 /login을 구현하지 않아도 됨)
                .defaultSuccessUrl("/")   // 인증 완료 시 이동 url 설정
                .and().oauth2Login().loginPage("/loginForm")   // OAuth 로그인이 수행되는 로그인 페이지의 경로를 설정
                .userInfoEndpoint()   // OAuth2 로그인 성공 후 사용자 정보를 가져올 때의 설정을 담당
                .userService(principalOauth2UserService);   // 소셜 로그인 성공 시 후속조치를 수행할 OAuth2UserService 인터페이스의 구현체를 등록
    }
}
