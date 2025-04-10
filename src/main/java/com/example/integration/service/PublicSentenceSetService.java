package com.example.integration.service;

import com.example.integration.common.util.SecurityUtil;
import com.example.integration.dto.sentenceSet.ListSentenceSetResponseDto;
import com.example.integration.dto.sentenceSet.PublicSentenceSetAndSentenceListResponseDto;
import com.example.integration.dto.sentenceSet.PublicSentenceSetResponseDto;
import com.example.integration.dto.sentenceSet.SentenceSetRequestDto;
import com.example.integration.entity.RoleType;
import com.example.integration.entity.Sentence;
import com.example.integration.entity.SentenceSet;
import com.example.integration.entity.User;
import com.example.integration.repository.SentenceRepository;
import com.example.integration.repository.SentenceSetRepository;
import com.example.integration.repository.UserRepository;
import com.example.integration.response.CustomException;
import com.example.integration.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PublicSentenceSetService {

    private final SentenceSetRepository sentenceSetRepository;
    private final SentenceRepository sentenceRepository;
    private final UserRepository userRepository;

    /**
     * 현재 사용자 조회
     * @return
     */
    private User currentUser() {
        return userRepository.findByEmail(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }

    /**
     * id를 통해 특정 문장 세트 조회
     * @param sentenceSetId
     * @return
     */
    private SentenceSet findSentenceSetWithId(Long sentenceSetId) {
        return sentenceSetRepository.findById(sentenceSetId)
                .orElseThrow(() -> new CustomException(ErrorCode.SENTENCE_SET_NOT_FOUND));
    }

    /**
     * 현재 로그인한 사용자가 관리자인지 확인
     * @param user
     */
    private void checkAdmin(User user) {
        if (!user.getRoleType().equals(RoleType.ADMIN)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 공용 문장 세트 목록 조회
     * @param offset
     * @param limit
     * @return
     */
    @Transactional(readOnly = true)
    public ListSentenceSetResponseDto getPublicSentenceSetList(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<SentenceSet> sentenceSetList = sentenceSetRepository.findAllWhichPublic(pageable);

        return new ListSentenceSetResponseDto(sentenceSetList);
    }

    /**
     * 키워드(title)를 바탕으로 공용 문장 세트 목록 조회
     * @param keyword
     * @param offset
     * @param limit
     * @return
     */
    @Transactional(readOnly = true)
    public ListSentenceSetResponseDto searchPublicSentenceSetList(String keyword, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<SentenceSet> sentenceSetList = sentenceSetRepository.findAllWithKeywordWhichPublic(keyword, pageable);

        return new ListSentenceSetResponseDto(sentenceSetList);
    }

    /**
     * 특정 공용 문장 세트와 포함된 문장 목록 조회
     * @param sentenceSetId
     * @param offset
     * @param limit
     * @return
     */
    @Transactional(readOnly = true)
    public PublicSentenceSetAndSentenceListResponseDto getPublicSentenceSetWithSentences(Long sentenceSetId, int offset, int limit) {
        SentenceSet sentenceSet = findSentenceSetWithId(sentenceSetId);

        // 개인 문장 세트 조회일 때
        if (!sentenceSet.isPublic()) {
            throw new CustomException(ErrorCode.PRIVATE_SENTENCE_SET);
        }

        // 10개 페이징
        Pageable pageable = PageRequest.of(offset / limit, limit);

        List<Sentence> sentenceList = sentenceRepository.findAllBySentenceSetId(sentenceSetId, pageable);

        return new PublicSentenceSetAndSentenceListResponseDto(sentenceSet, sentenceList);
    }

    /**
     * 새로운 공용 문장 세트 생성
     * @param sentenceSetRequestDto
     * @return
     */
    @Transactional
    public PublicSentenceSetResponseDto createPublicSentenceSet(SentenceSetRequestDto sentenceSetRequestDto) {
        // 사용자가 관리자인지 확인
        User user = currentUser();
        checkAdmin(user);

        SentenceSet sentenceSet = SentenceSet.builder()
                .title(sentenceSetRequestDto.title())
                .isPublic(true)
                .user(user)
                .build();

        SentenceSet savedSentenceSet = sentenceSetRepository.save(sentenceSet);

        return new PublicSentenceSetResponseDto(savedSentenceSet);
    }

    /**
     * 기존 문장 세트 수정
     * @param sentenceSetId
     * @param sentenceSetRequestDto
     * @return
     */
    @Transactional
    public PublicSentenceSetResponseDto updateSentenceSet(Long sentenceSetId, SentenceSetRequestDto sentenceSetRequestDto) {
        User user = currentUser();
        checkAdmin(user);

        SentenceSet sentenceSet = findSentenceSetWithId(sentenceSetId);

        // 개인 문장 세트인 경우 수정 X
        if (!sentenceSet.isPublic()) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        sentenceSet.updateSentenceSet(sentenceSetRequestDto.title());

        SentenceSet updatedSentenceSet = sentenceSetRepository.save(sentenceSet);

        return new PublicSentenceSetResponseDto(updatedSentenceSet);
    }

    /**
     * 기존 문장 세트 삭제
     * @param sentenceSetId
     */
    @Transactional
    public void deleteSentenceSet(Long sentenceSetId) {
        User user = currentUser();
        checkAdmin(user);

        SentenceSet sentenceSet = findSentenceSetWithId(sentenceSetId);

        // 개인 문장 세트인 경우 수정 X
        if (!sentenceSet.isPublic()) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        sentenceSetRepository.delete(sentenceSet);
    }
}
