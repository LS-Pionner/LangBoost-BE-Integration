package com.example.integration.service;

import com.example.integration.common.enums.tts.VoiceLanguage;
import com.example.integration.common.enums.tts.VoiceName;
import com.example.integration.dto.tts.SingleTtsRequestDto;
import com.example.integration.common.response.CustomException;
import com.example.integration.common.response.ErrorCode;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class TextToSpeechService {


    /**
     * Google Cloud Text-to-Speech API를 사용하여 텍스트를 음성(MP3)으로 변환하는 메서드입니다.
     * <p>
     * - 환경 변수 'GOOGLE_APPLICATION_CREDENTIALS'에 지정된 경로에서 서비스 계정 키 파일을 로드합니다.
     * - 지정된 언어와 음성 설정을 기반으로 TTS 요청을 수행합니다.
     * - 결과로 음성 데이터(ByteString)를 반환합니다.
     *
     * @param text      변환할 텍스트
     * @param language  사용할 언어 설정
     * @param voiceName 사용할 음성 이름 설정
     * @return 변환된 음성 데이터 (MP3 형식)
     * @throws IOException 인증 실패 또는 API 호출 중 오류 발생 시
     */
    public ByteString convertTextToSpeechFromGoogleCloud(String text, VoiceLanguage language, VoiceName voiceName) throws IOException {
        // 시스템 환경 변수에서 서비스 계정 키 파일 경로 가져오기
        String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (credentialsPath == null) {
            throw new IllegalStateException("환경 변수 'GOOGLE_APPLICATION_CREDENTIALS'가 설정되지 않았습니다.");
        }

        // 인증 정보 로드
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream);
        }

        // Text-to-Speech 클라이언트 생성
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings)) {
            // 요청 내용 구성
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // 음성 설정 (언어 코드, 음성 유형)
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(language.getCode()) // 언어 설정
                    .setName(voiceName.getName()) // 특정 음성 이름 설정
                    .build();

            // 오디오 출력 형식 설정
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3) // MP3 형식
                    .build();

            // TTS 요청
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            return response.getAudioContent();
        } catch (ApiException e) {
            throw new IOException("Text-to-Speech API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }


    /**
     * 입력된 텍스트를 Google Cloud Text-to-Speech API를 통해 음성(MP3)으로 변환하고,
     * 로컬 파일로 저장한 뒤 파일 경로를 반환하는 메서드입니다.
     * <p>
     * - 기본 음성 설정은 미국 영어(ENGLISH_US)의 EN_US_NEURAL2_A 음성을 사용합니다.
     * - 필요에 따라 한국어 음성 설정으로 변경할 수 있습니다.
     *
     * @param text 변환할 텍스트
     * @return 생성된 오디오 파일의 경로 (String)
     * @throws IOException TTS 처리 또는 파일 저장 중 오류 발생 시
     */
    public String synthesizeSpeechToLocalFile(String text) throws IOException {
        VoiceLanguage language = VoiceLanguage.ENGLISH_US;
//    VoiceLanguage language = VoiceLanguage.KOREAN;

        VoiceName voiceName = VoiceName.EN_US_NEURAL2_A;
//    VoiceName voiceName = VoiceName.KO_KR_STANDARD_A;

        // Text-to-Speech 요청을 Google Cloud API를 사용해 처리
        ByteString audioContents = convertTextToSpeechFromGoogleCloud(text, language, voiceName);

        // ByteString을 byte[]로 변환
        byte[] audioByteArray = audioContents.toByteArray();

        // 파일 이름 반환
        return saveAudioToLocalFile(audioByteArray);
    }

    /**
     * 주어진 오디오 데이터를 사용자의 바탕화면의 'tts' 폴더에 MP3 파일로 저장합니다.
     *
     * - 파일 이름은 현재 날짜와 시간을 포함하여 고유하게 생성됩니다.
     * - 바탕화면에 'tts' 폴더가 없으면 자동으로 생성됩니다.
     *
     * @param audioContents 저장할 오디오 데이터 (byte 배열)
     * @return 저장된 MP3 파일의 전체 경로
     * @throws IOException 파일 저장 중 오류가 발생한 경우
     */
    public String saveAudioToLocalFile(byte[] audioContents) throws IOException {
        // 사용자 바탕화면 경로를 얻고, tts 폴더를 설정
        String userHome = System.getProperty("user.home");
//        String desktopPath = userHome + "/Desktop"; // 바탕화면 경로
//        String ttsFolderPath = desktopPath + "/tts"; // tts 폴더 경로
        // 추후 특정 경로로 설정
        String oneDrive = userHome + "\\OneDrive";
        String desktopPath = oneDrive + "\\바탕 화면"; // 바탕화면 경로
        String ttsFolderPath = desktopPath + "\\tts"; // tts 폴더 경로

        // tts 폴더가 없으면 생성
        File ttsFolder = new File(ttsFolderPath);
        if (!ttsFolder.exists()) {
            ttsFolder.mkdir(); // 폴더 생성
        }

        // 현재 시각을 파일 이름에 포함시켜 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String outputFileName = ttsFolderPath + "\\output_" + currentDateTime + ".mp3"; // 현재 시각 포함

        // MP3 파일로 저장
        try (FileOutputStream out = new FileOutputStream(outputFileName)) {
            out.write(audioContents);
            System.out.println("Audio content written to file: " + outputFileName);
        }

        return outputFileName; // 저장된 파일 경로 반환
    }

    /**
     * 전달받은 텍스트와 언어 정보를 기반으로 Google Cloud TTS API를 호출하여 음성 데이터를 생성합니다.
     * <p>
     * - 요청 언어가 ENGLISH인 경우 미국 영어 음성으로 변환됩니다.
     * - 변환된 음성은 MP3 포맷의 바이트 배열로 반환됩니다.
     *
     * @param singleTtsRequestDto 변환할 텍스트와 언어 정보를 담은 DTO
     * @return 생성된 음성의 바이트 배열
     * @throws CustomException TTS 생성 중 오류가 발생한 경우
     */
    public byte[] generateSpeechAudio(SingleTtsRequestDto singleTtsRequestDto) {
        try {
            VoiceLanguage voiceLanguage = null;
            VoiceName voiceName = null;

            if ("ENGLISH".equalsIgnoreCase(singleTtsRequestDto.language())) {
                voiceLanguage = VoiceLanguage.ENGLISH_US;
                voiceName = VoiceName.EN_US_NEURAL2_A;
            } else {
                voiceLanguage = VoiceLanguage.KOREAN;
                voiceName = VoiceName.KO_KR_STANDARD_A;
            }

            ByteString audioContent = convertTextToSpeechFromGoogleCloud(
                    singleTtsRequestDto.text(),
                    voiceLanguage,
                    voiceName
            );

            return audioContent.toByteArray();
        } catch (IOException e) {
            e.printStackTrace(); // 로그 남기기
            throw new CustomException(ErrorCode.TTS_GENERATION_FAILED);
        }
    }



}

