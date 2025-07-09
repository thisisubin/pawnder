package com.pawnder.dto;

import com.pawnder.constant.Gender;
import com.pawnder.constant.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PetProfileDto {
    //이름
    private String name;

    //입양여부
    private boolean adopt;

    //종류 (치와와/푸들/등등)
    private String type;

    //몸무게
    private double weight;

    //사이즈 (대형/중형/소형)
    private Size size;

    //나이
    private int age;

    //성별
    private Gender gender;
}
