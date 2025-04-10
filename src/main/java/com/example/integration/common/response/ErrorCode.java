package com.example.integration.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Test Error
    TEST_ERROR(10000, HttpStatus.BAD_REQUEST, "테스트 에러입니다."),


    // 400 BAD REQUEST
    INVALID_VERIFY_CODE(40001, HttpStatus.BAD_REQUEST, "유효하지 않은 인증 코드입니다."),
    EMAIL_ALREADY_EXISTS(40002, HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
    PRIVATE_SENTENCE_SET(40003, HttpStatus.BAD_REQUEST, "허용되지 않은 문장 세트입니다."),

    // 401 Unauthorized - 잘못된 토큰
    INVALID_TOKEN(40101, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(40102, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_REFRESH_TOKEN(40103, HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다"),
    NOT_MATCHED_REFRESH_TOKEN(40104, HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다"),
    INVALID_PASSWORD(40105, HttpStatus.UNAUTHORIZED, "비밀번호 에러"),
    LOGIN_ERROR(40106, HttpStatus.UNAUTHORIZED, "로그인 에러"),


    // 403 Forbidden
    FORBIDDEN(40301, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 404 Not Found
    NOT_FOUND_END_POINT(40400, HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),
    NOT_FOUND_USER(40401, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    SENTENCE_SET_NOT_FOUND(40402, HttpStatus.NOT_FOUND, "해당 문장 세트를 찾을 수 없습니다."),
    ID_NOT_FOUND_SENTENCE(40403, HttpStatus.NOT_FOUND, "해당 문장 ID를 찾을 수 없습니다."),


    // 409 Conflict
    REFERENCE_ALREADY_EXISTS(40901, HttpStatus.CONFLICT, "참조는 문장당 1개만 존재"),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    TEST(50001, HttpStatus.INTERNAL_SERVER_ERROR, "테스트 에러"),
    TTS_GENERATION_FAILED(50002, HttpStatus.INTERNAL_SERVER_ERROR, "TTS 생성 중 오류가 발생했습니다."),

    ;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}