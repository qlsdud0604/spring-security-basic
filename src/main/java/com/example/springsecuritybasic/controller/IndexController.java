package com.example.springsecuritybasic.controller;

import com.example.springsecuritybasic.config.auth.PrincipalDetails;
import com.example.springsecuritybasic.model.User;
import com.example.springsecuritybasic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller   // View를 리턴하겠다는 애너테이션
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/test/login")
    @ResponseBody
    /* 일반 로그인 사용자에 대한 정보 받기 */
    public String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
        System.out.println("====================/test/login====================");

        /* 방법1 : Authentication을 DI해서 다운캐스팅 과정을 거쳐서 유저 정보를 받음 */
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println(principalDetails.getUser());

        /* 방법2 : "@AuthenticationPrincipal" 애너테이션을 통해서 유저 정보를 받음 */
        System.out.println(userDetails.getUser());

        return "세션 정보 확인";
    }

    @GetMapping("/test/oauth/login")
    @ResponseBody
    /* OAuth 로그인 사용자에 대한 정보 받기 */
    public String testOauthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth) {
        System.out.println("====================/test/oauth/login====================");

        /* 방법1 : Authentication을 DI해서 다운캐스팅 과정을 거쳐서 유저 정보를 받음 */
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println(oAuth2User.getAttributes());

        /* 방법2 : "@AuthenticationPrincipal" 애너테이션을 통해서 유저 정보를 받음 */
        System.out.println(oAuth.getAttributes());

        return "OAuth 세션 정보 확인";
    }

    @GetMapping({"", "/"})
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    @ResponseBody
    public String user() {
        return "user";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    @ResponseBody
    public String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String login() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);

        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = encoder.encode(rawPassword);
        user.setPassword(encPassword);

        userRepository.save(user);

        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")   // 해당 url은 ROLE_ADMIN 권한만 접근 가능
    @GetMapping("/info")
    @ResponseBody
    public String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")   // 해당 url은 ROLE_MANAGER 또는 ROLE_ADMIN 권한만 접근 가능
    @GetMapping("/data")
    @ResponseBody
    public String data() {
        return "데이터";
    }
}
