package com.pawnder.service;

import com.pawnder.dto.PetProfileDto;
import com.pawnder.dto.UserLoginDto;
import com.pawnder.entity.Pet;
import com.pawnder.entity.User;
import com.pawnder.repository.PetRepository;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    //반려동물 등록 함수
    public void enrollPet(String userId, PetProfileDto petProfileDto) {
        //1. 유저의 아이디를 가져와서 등록
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        //2. userId로 petProfile 등록 DTO->Entity 변환
        Pet pet = new Pet();
        pet.setName(petProfileDto.getName());
        pet.setAdopt(petProfileDto.isAdopt());
        pet.setType(petProfileDto.getType());
        pet.setWeight(petProfileDto.getWeight());
        pet.setSize(petProfileDto.getSize());
        pet.setAge(petProfileDto.getAge());
        pet.setGender(petProfileDto.getGender());

        //3. 연관관계 설정
        pet.setUser(user);

        //4. 저장
        petRepository.save(pet);
    }

    public List<PetProfileDto> getPetsByUserId(String userId) {
        List<Pet> pets = petRepository.findByUserUserId(userId);
        return pets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PetProfileDto convertToDto(Pet pet) {
        PetProfileDto dto = new PetProfileDto();
        dto.setName(pet.getName());
        dto.setAdopt(pet.isAdopt());
        dto.setType(pet.getType());
        dto.setWeight(pet.getWeight());
        dto.setSize(pet.getSize());
        dto.setAge(pet.getAge());
        dto.setGender(pet.getGender());
        return dto;
    }



}
