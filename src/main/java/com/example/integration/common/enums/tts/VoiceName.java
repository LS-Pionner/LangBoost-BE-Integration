package com.example.integration.common.enums.tts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoiceName {
    EN_US_NEURAL2_A("en-US-Neural2-A"),
    EN_US_NEURAL2_B("en-US-Neural2-B"),
    KO_KR_STANDARD_A("ko-KR-Standard-A");

    private final String name;

}

