package com.example.springsecuritybasic.config.auth;

import com.example.springsecuritybasic.model.User;
import com.example.springsecuritybasic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userEntity = userRepository.findByUsername(username);

        if (userEntity != null) {   // DB에 등록된 사용자 이름이 있는지 확인
            return new PrincipalDetails(userEntity);
        }

        return null;
    }
}

// "/login" 요청이 오면 자동으로 UserDetailsService 타입으로 IoC에 등록된 loadUserByUsername 함수가 실행됨
// loadUserByUsername() 메서드에 의해서 리턴된 UserDetails 객체는 Authentication 객체에 들어가게 됨