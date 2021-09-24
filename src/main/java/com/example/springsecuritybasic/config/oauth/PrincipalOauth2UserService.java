package com.example.springsecuritybasic.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Override
    /* 구글로부터 받은 userRequest 데이터를 후처리하는 함수 */
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        System.out.println("getClientRegistration : " + userRequest.getClientRegistration());   // registrationId로 어떤 OAuth로 로그인 헸는지 확인 가능
//        System.out.println("getAccessToken : " + userRequest.getAccessToken());
//        System.out.println("getAttributes : " + super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        return super.loadUser(userRequest);
    }
}

// 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> code 리턴 (OAuth-Client 라이브러리가 받음)
// -> AccessToken 요청 : 여기까지가 userRequest 정보
// userRequest 정보를 통해 회원 프로필 정보를 받아야 함 -> 이때 사용되는 함수가 loadUser