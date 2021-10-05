package com.example.springsecuritybasic.config.oauth;

import com.example.springsecuritybasic.config.auth.PrincipalDetails;
import com.example.springsecuritybasic.config.oauth.provider.FacebookUserInfo;
import com.example.springsecuritybasic.config.oauth.provider.GoogleUserInfo;
import com.example.springsecuritybasic.config.oauth.provider.NaverUserInfo;
import com.example.springsecuritybasic.config.oauth.provider.OAuth2UserInfo;
import com.example.springsecuritybasic.model.User;
import com.example.springsecuritybasic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    /* 구글로부터 받은 userRequest 데이터를 후처리하는 함수 */
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        System.out.println("getClientRegistration : " + userRequest.getClientRegistration());   // registrationId로 어떤 OAuth로 로그인 헸는지 확인 가능
        System.out.println("getAccessToken : " + userRequest.getAccessToken());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println("getAttributes : " + oAuth2User.getAttributes());

        /* 소셜 로그인 수행시 강제 회원가입 */
        OAuth2UserInfo oAuth2UserInfo = null;

        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("Google 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            System.out.println("Facebook 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        } else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("Naver 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }else{
            System.out.println("Google, Facebook, Naver 로그인만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider();   // ex) google
        String provideId = oAuth2UserInfo.getProviderId();  // ex) 103163902666771091884
        String username = provider + "_" + provideId;   // ex) google_103163902666771091884
        String password = encoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(provideId)
                    .build();

            userRepository.save(userEntity);
        } else {
            System.out.println("소셜 계정으로 회원가입을 한 상태입니다.");
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}

// 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> code 리턴 (OAuth-Client 라이브러리가 받음)
// -> AccessToken 요청 : 여기까지가 userRequest 정보
// userRequest 정보를 통해 회원 프로필 정보를 받아야 함 -> 이때 사용되는 함수가 loadUser 함수