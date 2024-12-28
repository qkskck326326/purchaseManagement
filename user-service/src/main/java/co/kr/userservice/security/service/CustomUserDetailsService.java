package co.kr.userservice.security.service;


import co.kr.userservice.security.entity.CustomUserDetails;
import co.kr.userservice.user.entity.UserEntity;
import co.kr.userservice.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(userEmail);

        if (user == null) {
            throw new UsernameNotFoundException("해당 유저를 찾을 수 없습니다 :  " + userEmail);
        }

        return new CustomUserDetails(user);
    }
}
