package com.springboot.rerise.service;

import com.springboot.rerise.dto.DailyRecordDto;
import com.springboot.rerise.entity.DailyRecord;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.repository.DailyRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final DailyRecordRepository recordRepository;
    private final UserService userService;

    @Transactional
    public DailyRecordDto createOrUpdateRecord(DailyRecordDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        // 1. Principal에서 이메일(String)을 가져옵니다.
        String email = (String) authentication.getPrincipal();

        // 2. 이메일로 실제 User 엔티티를 DB에서 조회합니다.
        Optional<User> optionalUser = userService.findByEmail(email);

        User currentUser = optionalUser
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        DailyRecord record = recordRepository
                .findByUserAndRecordedAt(currentUser, dto.getRecordedAt())
                .orElseGet(DailyRecord::new);

        record.setUser(currentUser);
        record.setEmotion_level(dto.getEmotion_level());
        record.setKeywords(dto.getKeywords());
        record.setMemo(dto.getMemo());
        record.setRecordedAt(dto.getRecordedAt() != null ? dto.getRecordedAt() : LocalDate.now());

        DailyRecord savedRecord = recordRepository.save(record);
        return convertToDto(savedRecord);
    }
    @Transactional
    public DailyRecordDto getRecordByDate(LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        // 1. Principal에서 이메일(String)을 가져옵니다.
        String email = (String) authentication.getPrincipal();

        // 2. 이메일을 사용해 데이터베이스에서 실제 User 엔티티를 조회합니다.
        User currentUser = userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        // 3. User 엔티티를 사용하여 기록을 조회합니다.
        return recordRepository.findByUserAndRecordedAt(currentUser, date)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("No record found for the given date"));
    }


    private DailyRecordDto convertToDto(DailyRecord record) {
        if (record == null) {
            return null;
        }
        DailyRecordDto dto = new DailyRecordDto();
        dto.setRecord_id(record.getRecord_id());
        dto.setEmotion_level(record.getEmotion_level());
        dto.setKeywords(record.getKeywords());
        dto.setMemo(record.getMemo());
        dto.setRecordedAt(record.getRecordedAt());
        return dto;
    }

}