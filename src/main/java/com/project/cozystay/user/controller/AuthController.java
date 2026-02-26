package com.project.cozystay.user.controller;

import com.project.cozystay.auth.JwtProvider;
import com.project.cozystay.user.dto.*;
import com.project.cozystay.user.service.EmailService;
import com.project.cozystay.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest
    ) {

        if(!emailService.isVerified(signUpRequest.email())){
            return ResponseEntity.badRequest().body(ApiResponse.fail("이메일 인증이 필요합니다"));
        }

        userService.signUp(signUpRequest);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", null));
    }

    /**
     * 아이디 중복 검사
     */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Void>> existsUsername(
            @Valid @ModelAttribute UsernameCheckRequest request
    ){
        userService.existsByUsername(request.username());

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 로그인
     */
    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {

        SignInResponse signInResponse = userService.signIn(signInRequest);

        return ResponseEntity.ok(signInResponse);
    }

    /**
     * 이메일 인증 요청
     */
    @PostMapping("/email-verification/request")
    public ResponseEntity<String> requestEmailVerification(
            @RequestParam @Email String email
    ) {

        emailService.sendVerificationEmail(email);

        return ResponseEntity.ok("인증 코드를 보냈습니다! 📧");
    }

    /**
     * 인증 확인 (코드 검사)
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(
            @RequestBody VerificationEmailRequestDTO request
    ) {

        boolean isVerified = emailService.verifyCode(request.email(), request.code());

        if (isVerified) {
            return ResponseEntity.ok("인증 성공! 환영합니다. 🎉");
        } else {
            return ResponseEntity.badRequest().body("인증 실패... 코드를 다시 확인해주세요.");
        }
    }
}
