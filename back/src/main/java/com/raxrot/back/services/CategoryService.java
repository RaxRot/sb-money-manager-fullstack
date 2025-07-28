package com.raxrot.back.services;

import com.raxrot.back.dtos.CategoryRequestDTO;
import com.raxrot.back.dtos.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO create(CategoryRequestDTO category);
    List<CategoryResponseDTO> getAllCategories();
    List<CategoryResponseDTO> getAllCategoriesByType(String type);
    CategoryResponseDTO updateCategory(Long categoryId,CategoryRequestDTO category);
    void deleteCategory(Long categoryId);
}
