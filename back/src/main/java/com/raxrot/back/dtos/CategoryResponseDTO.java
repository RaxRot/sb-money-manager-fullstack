package com.raxrot.back.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponseDTO {
    private Long id;

    private String name;

    private String type;

    private String icon;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
