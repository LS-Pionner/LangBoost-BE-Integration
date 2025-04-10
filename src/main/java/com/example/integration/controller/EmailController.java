package com.example.integration.controller;

import com.example.integration.dto.email.EmailDto;
import com.example.integration.common.response.ApiResponse;
import com.example.integration.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/email")
@RestController
public class EmailController {
    private final EmailService emailService;

    /**
     * 인증코드를 받기 위한 이메일 송신 API
     * @param emailDto
     * @return
     * @throws MessagingException
     */
    @PostMapping("/send")
    public ApiResponse<String> mailSend(@RequestBody EmailDto emailDto) throws MessagingException {
        log.info("EmailController.mailSend()");
        emailService.sendEmail(emailDto);
        String message = "인증코드가 발송되었습니다.";
        return ApiResponse.ok(message);
    }


    /**
     * 인증 코드 일치 여부 검증 API
     * @param emailDto
     * @return
     */
    @PostMapping("/verify")
    public ApiResponse<String> verify(@RequestBody EmailDto emailDto) {
        log.info("EmailController.verify()");
        emailService.verifyEmailCode(emailDto);

        return ApiResponse.ok("인증이 완료되었습니다.");
    }

}
