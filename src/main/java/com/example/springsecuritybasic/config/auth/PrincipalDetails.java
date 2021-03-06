package com.example.springsecuritybasic.config.auth;

import com.example.springsecuritybasic.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;

    private Map<String, Object> attributes;

    /* 일반 로그인 시 사용되는 생성자 */
    public PrincipalDetails(User user) {
        this.user = user;
    }

    /* OAuth 로그인 시 사용되는 생성자 */
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    /* 해당 유저의 권한을 리턴하는 메서드 (현재 User 객채의 role 타입은 String 타입이기 때문에 변환이 필요)*/
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }
}

// 시큐리티가 "/login" 주소 요청이 오면 낚아채서 로그인을 진행시킴
// 로그인 진행이 완료가 되면 시큐리티 session을 만들어 줌
// session에 들어갈수 있는 정보는 Authentication 객체여야 함
// Authentication 안에 User 정보가 있어야 함 (이때 User 객체의 타입은 UserDetails 타입이어야 함)
// 시큐리티 session => Authentication => UserDetails