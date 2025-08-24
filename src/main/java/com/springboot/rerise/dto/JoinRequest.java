package com.springboot.rerise.dto;

import com.springboot.rerise.config.UserRole;
import com.springboot.rerise.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    private String email;
    private String password;
    private String passwordCheck;
    private String nickname;
    private Date birth;


    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .birth(this.birth)
                .role(UserRole.USER)
                .build();
    }

    // 비밀번호 암호화
    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .birth(this.birth)
                .role(UserRole.USER)
                .build();
    }

}