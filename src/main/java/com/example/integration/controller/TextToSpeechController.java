package com.example.integration.controller;

import com.example.integration.dto.tts.SingleTtsRequestDto;
import com.example.integration.common.response.ApiResponse;
import com.example.integration.common.response.ErrorCode;
import com.example.integration.service.TextToSpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tts")
public class TextToSpeechController {

    private final TextToSpeechService textToSpeechService;


    /**
     * 주어진 문장을 음성으로 변환하여 로컬에 MP3 파일로 저장합니다.
     *
     * - HTTP POST 요청으로 전달된 text 파라미터를 받아 TTS(Text-to-Speech)를 수행합니다.
     * - 변환된 음성 파일은 로컬의 지정된 경로에 저장됩니다.
     * - 저장 경로를 포함한 성공 메시지를 반환하거나, 실패 시 에러 응답을 반환합니다.
     *
     * @param text 음성으로 변환할 입력 문장
     * @return 생성된 오디오 파일 경로 또는 에러 메시지를 포함한 응답
     */
    @PostMapping("/test")
    public ApiResponse<String> generateSpeechToLocalTest(@RequestParam String text) {
        try {
            // 출력 파일 이름 지정
            String filePath = textToSpeechService.synthesizeSpeechToLocalFile(text);
            log.info("audio 파일 생성");

            return ApiResponse.ok("Audio file generated: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.fail(ErrorCode.TTS_GENERATION_FAILED);
        }
    }

    /**
     * 텍스트와 언어 정보를 포함한 요청을 받아 음성 데이터를 생성하고,
     * 생성된 음성(MP3)을 바이트 배열 형태로 클라이언트에게 반환하는 엔드포인트입니다.
     * <p>
     * - 요청 본문에 포함된 텍스트와 언어 정보를 기반으로 TTS 변환이 수행됩니다.
     * - 반환된 음성은 'tts_output.mp3' 파일 이름으로 브라우저에서 바로 재생 또는 다운로드할 수 있습니다.
     *
     * @param singleTtsRequestDto 변환할 텍스트와 언어 정보를 담은 요청 객체
     * @return 음성 데이터가 담긴 ResponseEntity (HTTP 200 또는 오류 시 500)
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateSpeechToClient(@RequestBody SingleTtsRequestDto singleTtsRequestDto) {
        byte[] audioBytes = textToSpeechService.generateSpeechAudio(singleTtsRequestDto);

        if (audioBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(audioBytes.length);
        headers.setContentDisposition(ContentDisposition.inline().filename("tts_output.mp3").build());

        return new ResponseEntity<>(audioBytes, headers, HttpStatus.OK);
    }

}
