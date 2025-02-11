package com.example.integration.config.util;

import java.time.LocalDate;

public class DateUtil {

    // 마지막 조회 날짜 반환
    public static LocalDate getLastViewedDate() {
        return LocalDate.now();
    }

}
