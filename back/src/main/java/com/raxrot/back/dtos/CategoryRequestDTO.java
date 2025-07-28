package com.raxrot.back.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequestDTO {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "type is required")
    private String type;//latterEnum

    private String icon;
}