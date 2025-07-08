package com.pawnder.entity;

import com.pawnder.constant.Role;
import com.pawnder.dto.UserSignUpDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Member;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String userId;
    private String email;
    private String password;
    private LocalDate birth;
    private String phoneNm;
    private boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    private Role role;

}
