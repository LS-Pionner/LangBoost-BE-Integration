package com.example.integration.service;

import com.example.integration.common.response.CustomException;
import com.example.integration.common.response.ErrorCode;
import com.example.integration.common.util.RedisUtil;
import com.example.integration.entity.RoleType;
import com.example.integration.entity.User;
import com.example.integration.dto.email.EmailDto;
import com.example.integration.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final TemplateEngine templateEngine;

    private static final String senderEmail = "jj@naver.com";

    /**
     * 무작위 6자리 코드 생성
     * @return
     */
    private static String createCode() {
        int leftLimit = 48; // number '0'
        int rightLimit = 57; // number '9'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)  // 숫자 0부터 9까지
                .limit(targetStringLength)  // 6자리 길이로 제한
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * 이메일 내용 초기화
     * @param code
     * @return
     */
    // 이메일 내용 초기화
    private String setVerificationEmailContext(String code) {
        Context context = new Context();
        context.setVariable("code", code); // 코드 값 템플릿에 전달

        // Thymeleaf 템플릿 파일을 사용하여 HTML 생성
        return templateEngine.process("verification-email", context);
    }

    /**
     * 이메일 폼 생성
     * @param emailDto
     * @return
     * @throws MessagingException
     */
    private MimeMessage createEmailForm(EmailDto emailDto) throws MessagingException {
        String authCode = createCode();

        String purpose = emailDto.purpose();
        String email = emailDto.mail();

        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("안녕하세요. 인증번호입니다.");
        message.setFrom(senderEmail);
        message.setText(setVerificationEmailContext(authCode), "utf-8", "html");

        String redisKey = purpose + ":" + email;

        // Redis 에 해당 인증코드 인증 시간 설정
        redisUtil.setDataExpire(redisKey, authCode, 60 * 30L);

        return message;
    }

    /**
     * 인증코드가 담긴 이메일 송신
     * @param emailDto
     * @throws MessagingException
     */
    public void sendEmail(EmailDto emailDto) throws MessagingException {
        String toEmail = emailDto.mail();

        if (redisUtil.existData(toEmail)) {
            redisUtil.deleteData(toEmail);
        }

        // 이메일 폼 생성
        MimeMessage emailForm = createEmailForm(emailDto);
        // 이메일 송신
        javaMailSender.send(emailForm);
    }

    /**
     * 인증 코드 일치 여부 검증
     * @param emailDto
     * @return
     */
    public Boolean verifyEmailCode(EmailDto emailDto) {
        String purpose = emailDto.purpose();
        String email = emailDto.mail();
        String code = emailDto.verifyCode();

        String redisKey = purpose + ":" + email;

        // Redis에서 코드 가져오기
        String codeFoundByEmail = redisUtil.getData(redisKey);
        log.info("code found by email: " + codeFoundByEmail);

        // 코드가 없거나 다르면 실패 처리
        if (codeFoundByEmail == null || !codeFoundByEmail.equals(code)) {
            throw new CustomException(ErrorCode.INVALID_VERIFY_CODE); // 유효하지 않은 코드
        }

        // 이메일로 사용자 찾기
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (!optionalUser.isPresent()) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER); // 사용자 정보 없음
        }

        // 사용자 존재하면 enabled 설정 후 저장
        User user = optionalUser.get();
        user.updateRole(RoleType.USER);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            // 사용자 저장 중 오류가 발생한 경우
            log.error("Failed to enable user with email: {}", email, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 내부 서버 오류
        }

        // 성공적으로 활성화 처리된 경우
        return true;
    }

}
