package com.example.integration.common.enums.tts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoiceLanguage {
    ENGLISH_US("en-US"),
    KOREAN("ko-KR");

    private final String code;
}
