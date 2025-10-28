package com.springboot.rerise.dto;


import com.springboot.rerise.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;


public class MyPageResponseDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterResponse{
        private String nickname;
        private String characterType;
        private Integer characterStage;
        private Integer level;
        private Double growthRate; // 성장률 (퍼센트, 0.0 ~ 100.0)
    }

    //내 정보 조회
    @Getter
    public static class InfoResponse{
        private String email;
        private String nickname;
        private Date birth;

        private boolean activityPushEnabled;
        private boolean progressSmsEnabled;
        private boolean moodCheckEnabled;

        public InfoResponse(User user) {
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.birth = user.getBirth();
            this.activityPushEnabled = user.isActivityPushEnabled();
            this.progressSmsEnabled = user.isProgressSmsEnabled();
            this.moodCheckEnabled = user.isMoodCheckEnabled();
        }
    }

    //내 정보 변경
    @Getter
    public static class ProfileUpdateRequest {
        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickname;

        @NotNull(message = "생년월일은 필수입니다.")
        private Date birth;
    }

    //알림 설정 변경
    @Getter
    public static class NotificationSettingsRequest {
        @NotNull
        private Boolean activityPushEnabled;

        @NotNull
        private Boolean progressSmsEnabled;

        @NotNull
        private Boolean moodCheckEnabled;
    }
}
