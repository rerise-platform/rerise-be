package com.springboot.rerise.service;

import com.springboot.rerise.dto.*;
import com.springboot.rerise.entity.Missions;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserCharacter;
import com.springboot.rerise.entity.UserProofMissions;
import com.springboot.rerise.repository.MissionsRepository;
import com.springboot.rerise.repository.UserProofMissionsRepository;
import com.springboot.rerise.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProofMissionService {

    private final UserProofMissionsRepository userProofMissionsRepository;
    private final MissionsRepository missionsRepository;
    private final UserRepository userRepository;

    //인증자료 제출
    public void submitProof(Long userId, ProofSubmitRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Missions mission = missionsRepository.findById(requestDto.getMissionId())
                .orElseThrow(() -> new RuntimeException("미션을 찾을 수 없습니다."));

        UserProofMissions proofMission = new UserProofMissions();
        proofMission.setUser(user);
        proofMission.setMission(mission);
        proofMission.setProofData(requestDto.getProofData());
        proofMission.setStatus(UserProofMissions.WeeklyMissionStatus.PENDING_APPROVAL);

        userProofMissionsRepository.save(proofMission);
    }
    //승인 대기 중인 미션 목록 조회
    public List<SubmissionListDto> getPendingSubmissions() {
        List<UserProofMissions> pendingMissions = userProofMissionsRepository
                .findByStatus(UserProofMissions.WeeklyMissionStatus.PENDING_APPROVAL);

        return pendingMissions.stream()
                .map(mission -> SubmissionListDto.builder()
                        .userProofMissionId(mission.getUserWeeklyMissionId())
                        .nickname(mission.getUser().getNickname())
                        .email(mission.getUser().getEmail())
                        .submissionDate(mission.getCreatedAt().toLocalDate()) // 제출일
                        .build())
                .collect(Collectors.toList());
    }
    //승인 대기 중인 미션 상세 조회
    public SubmissionDetailDto getSubmissionDetails(Long userProofMissionId) {
        UserProofMissions mission = userProofMissionsRepository.findById(userProofMissionId)
                .orElseThrow(() -> new RuntimeException("해당 미션을 찾을 수 없습니다."));

        return SubmissionDetailDto.builder()
                .userId(mission.getUser().getUserId())
                .nickname(mission.getUser().getNickname())
                .email(mission.getUser().getEmail())
                .userProofMissionId(mission.getUserWeeklyMissionId())
                .missionContent(mission.getMission().getContent())
                .proofData(mission.getProofData())
                .submissionDate(mission.getCreatedAt())
                .build();
    }

    //미션 인증 처리
    public MissionRewardResponseDto approveMission(ProofApprovalRequestDto approvalDto) {
        UserProofMissions proofMission = userProofMissionsRepository.findById(approvalDto.getUserProofMissionId())
                .orElseThrow(() -> new RuntimeException("인증 미션 기록을 찾을 수 없습니다."));

        if (proofMission.getStatus() != UserProofMissions.WeeklyMissionStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("승인 대기 중인 미션이 아닙니다.");
        }
        //미션 인증 승인
        if (approvalDto.isApproved()) {
            proofMission.setStatus(UserProofMissions.WeeklyMissionStatus.COMPLETED);
            proofMission.setCompletedAt(LocalDateTime.now());
            userProofMissionsRepository.save(proofMission);

            updateUserExperience(proofMission.getUser().getUserId(), proofMission.getMission().getRewardExp());

            User updatedUser = userRepository.findById(proofMission.getUser().getUserId()).get();
            return createSuccessResponse(updatedUser, proofMission.getMission());
        } else { //미션 인증 거절
            proofMission.setStatus(UserProofMissions.WeeklyMissionStatus.REJECTED);
            userProofMissionsRepository.save(proofMission);
            // 컨트롤러에서 null을 받고 실패 응답을 보내도록 처리
            return null;
        }
    }

    private int calculateLevel(int experience) {
        return Math.max(1, (experience / 100) + 1);
    }

    private void updateUserExperience(Long userId, int expGain) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.getUserCharacter() != null) {
            UserCharacter userCharacter = user.getUserCharacter();
            int currentExp = userCharacter.getExperience();
            int newExp = currentExp + expGain;

            userCharacter.setExperience(newExp);

            int newLevel = calculateLevel(newExp);
            if (newLevel > userCharacter.getLevel()) {
                userCharacter.setLevel(newLevel);
                log.info("사용자 {}의 레벨이 {}로 상승했습니다!", userId, newLevel);
            }
            userRepository.save(user);
        }
    }

    private MissionRewardResponseDto createSuccessResponse(User user, Missions mission) {
        UserCharacter character = user.getUserCharacter();
        int userLevel = (character != null) ? character.getLevel() : 1;
        int userExp = (character != null) ? character.getExperience() : 0;

        return MissionRewardResponseDto.builder()
                .userId(user.getUserId())
                .missionId(mission.getMissionId())
                .isCompleted(true)
                .message("미션이 성공적으로 완료되었습니다!")
                .rewardedExp(mission.getRewardExp())
                .userNewLevel(userLevel)
                .userNewTotalExp(userExp)
                .build();
    }
}