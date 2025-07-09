package com.pawnder.controller;

import com.pawnder.dto.PetProfileDto;
import com.pawnder.dto.UserLoginDto;
import com.pawnder.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pet")
@Tag(name = "Pet", description = "반려동물 관련 API")
public class PetController {
    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    @Operation(summary = "나의 반려동물 등록")
    @PostMapping("/enroll")
    public ResponseEntity<String> enroll(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody PetProfileDto petProfileDto) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }//디버깅

        String userId = userDetails.getUsername(); // 또는 userDetails.getUserId() 등
        petService.enrollPet(userId, petProfileDto);
        return ResponseEntity.ok("반려동물 등록 성공!");
    }

    @Operation(summary = "나의 반려동물 조회")
    @GetMapping("/profile/pets")
    public ResponseEntity<List<PetProfileDto>> getUserPets(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        List<PetProfileDto> pets = petService.getPetsByUserId(userId);
        return ResponseEntity.ok(pets);
    }


}
