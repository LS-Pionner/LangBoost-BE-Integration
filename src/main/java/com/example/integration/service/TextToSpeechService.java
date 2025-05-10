package com.example.integration.service;

import com.example.integration.common.enums.tts.VoiceLanguage;
import com.example.integration.common.enums.tts.VoiceName;
import com.example.integration.common.util.SecurityUtil;
import com.example.integration.dto.tts.SingleTtsRequestDto;
import com.example.integration.common.response.CustomException;
import com.example.integration.common.response.ErrorCode;
import com.example.integration.entity.User;
import com.example.integration.repository.UserRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.drive.Drive;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TextToSpeechService {

    private static final String APPLICATION_NAME = "Pioneer"; // 애플리케이션 이름 설정
    private static final String DRIVE_FOLDER_NAME = "tts_audio"; // 구글 드라이브에 생성할 폴더 이름
    private static final String AUTH_KEY = "gd:";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

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
    public String synthesizeSpeechAndUploadToDrive(String text) throws IOException, GeneralSecurityException {
        User user = userRepository.findByEmail(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        VoiceLanguage language = VoiceLanguage.ENGLISH_US;
//    VoiceLanguage language = VoiceLanguage.KOREAN;

        VoiceName voiceName = VoiceName.EN_US_NEURAL2_A;
//    VoiceName voiceName = VoiceName.KO_KR_STANDARD_A;

        // Text-to-Speech 요청을 Google Cloud API를 사용해 처리
        ByteString audioContents = convertTextToSpeechFromGoogleCloud(text, language, voiceName);

        // ByteString을 byte[]로 변환
        byte[] audioBytes = audioContents.toByteArray();

        return uploadFile(user.getEmail(), audioBytes);
    }

    // 구글 드라이브에 파일 업로드
    private String uploadFile(String email, byte[] audioBytes) throws GeneralSecurityException, IOException {
        String redisKey = AUTH_KEY + email;
        String accessToken = redisTemplate.opsForValue().get(redisKey);

        if (accessToken == null) {
            throw new CustomException(ErrorCode.GOOGLE_OAUTH_TOKEN_NOT_FOUND);
        }

        Credential credential = new GoogleCredential().setAccessToken(accessToken);

        Drive driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // 업로드할 폴더를 찾고, 없으면 생성
        String folderId = findOrCreateDriveFolder(driveService, DRIVE_FOLDER_NAME);

        // 파일 메타 데이터 설정
        File fileMetaData = new File();
        fileMetaData.setName("tts_output_" + System.currentTimeMillis() + ".mp3");
        fileMetaData.setParents(Collections.singletonList(folderId));

        // 파일 콘텐츠 생성
        ByteArrayContent mediaContent = new ByteArrayContent("audio/mp3", audioBytes);

        // 구글 드라이브에 파일 업로드
        File uploadFile = driveService.files().create(fileMetaData, mediaContent)
                .setFields("id, webViewLink") // 응답 필드 지정
                .execute();

        if (uploadFile == null || uploadFile.getId() == null) {
            throw new IOException("구글 드라이브 업로드에 실패했습니다.");
        }

        return uploadFile.getId();
    }

    private String findOrCreateDriveFolder(Drive driveService, String folderName) throws IOException {
        // 폴더 검색 쿼리
        String query = String.format("name='%s' and mimeType='application/vnd.google-apps.folder' and trashed=false", folderName);

        FileList result = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id)")
                .execute();

        List<File> files = result.getFiles();

        if (files != null && !files.isEmpty()) {
            // 폴더가 존재하는 경우 첫 번째 폴더 ID 반환
            return files.get(0).getId();
        } else {
            // 폴더가 없는 경우 새로 생성
            File fileMetadata = new File();
            fileMetadata.setName(folderName);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            File folder = driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();

            if (folder == null || folder.getId() == null) {
                throw new IOException("구글 드라이브 폴더 생성에 실패했습니다.");
            }

            return folder.getId();
        }
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
    /*
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
     */

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

