package com.pawnder.controller;

import com.pawnder.dto.UserLoginDto;
import com.pawnder.dto.UserSignUpDto;
import com.pawnder.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "회원 관련 API")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //이메일 인증
    @Operation(summary = "이메일 인증")
    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        userService.sendVerificationEmail(email);
        return ResponseEntity.ok("인증 이메일을 전송했습니다.");
    }

    //이메일 인증 확인
    @Operation(summary = "이메일 인증 확인")
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyCode(@RequestParam String code) {
        userService.verifiedCode(code);
        return ResponseEntity.ok("인증되었습니다.");
    }

    //회원가입
    @Operation(
            summary = "회원가입",
            description = "이름, 아이디, 이메일, 비밀번호, 생년월일로 회원가입을 진행합니다. 전화번호는 현재 필수 입력 값이 아닙니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignUpDto userSignUpDto) {
        userService.signUp(userSignUpDto);
        return ResponseEntity.ok("회원가입 성공!");
    }

    //로그인
    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호로 로그인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공!"),
                    @ApiResponse(responseCode = "400", description = "인증 실패")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto userLoginDto) {
        boolean success = userService.login(userLoginDto);
        if (success) {
            return ResponseEntity.ok("로그인 성공!");
        } else {
            return ResponseEntity.status(400).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

}
