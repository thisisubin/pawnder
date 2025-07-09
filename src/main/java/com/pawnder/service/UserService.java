package com.pawnder.service;

import com.pawnder.constant.Role;
import com.pawnder.dto.UserLoginDto;
import com.pawnder.dto.UserSignUpDto;
import com.pawnder.entity.User;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String email) {
        //1. 이미 가입된 회원인지 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
        //2. 인증 코드 생성 (6자리 숫자)
        String code = String.format("%06d", new Random().nextInt(999999));
        //3. Redis에 저장 (TTL: 2분)
        redisTemplate.opsForValue().set("EMAIL_CODE:" + code, email, 2, TimeUnit.MINUTES);
        //4. 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Pawnder] 이메일 인증 코드");
        message.setText("인증 코드는 " + code + "입니다. \n2분 내에 입력해주세요.");
        javaMailSender.send(message);

    }

    public void verifiedCode(String code) {
        // 1. Redis에서 code에 해당하는 이메일 찾기
        String email = redisTemplate.opsForValue().get("EMAIL_CODE:" + code);
        if (email == null) {
            throw new IllegalStateException("유효하지 않거나 만료된 코드입니다.");
        }

        // 2. 인증된 이메일이라는 표시 저장 (e.g., VERIFIED_EMAIL:이메일 → true)
        redisTemplate.opsForValue().set("VERIFIED_EMAIL:" + email, "true", 2, TimeUnit.MINUTES);

        // 3. 인증에 사용된 코드 제거
        redisTemplate.delete("EMAIL_CODE:" + code);
    }


    public User signUp(UserSignUpDto dto) {
        // 이메일 인증이 완료된 이메일인지 확인
        String verifiedEmail = redisTemplate.opsForValue().get("VERIFIED_EMAIL:" + dto.getEmail());
        if (verifiedEmail == null || !verifiedEmail.equals("true")) {
            throw new IllegalStateException("이메일 인증을 먼저 완료해주세요.");
        }

        //1. 유효성 검사 (이메일 중복 등)
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("이미 가입된 회원의 이메일입니다.");
        }

        //2. DTO -> ENTITY 변환 회원생성
        User user = new User();
        user.setName(dto.getName());
        user.setUserId(dto.getId());
        user.setEmail(dto.getEmail());
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);
        user.setBirth(dto.getBirth());
        user.setPhoneNm(dto.getPhoneNm());
        user.setVerified(true);
        user.setRole(Role.USER);

        //3. 저장
        return userRepository.save(user);
    }

    public boolean login(UserLoginDto dto) {
        // userRepository에서 dto와 같은 userId를 조회
        User user = userRepository.findByUserId(dto.getUserId())
                .orElse(null);

        if (user == null) {
            return false; //아이디 존재 x
        }

        return passwordEncoder.matches(dto.getPassword(), user.getPassword());
    }


    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : " + userId));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserId())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }


}
