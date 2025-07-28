package com.raxrot.back.controllers;

import com.raxrot.back.dtos.CategoryRequestDTO;
import com.raxrot.back.dtos.CategoryResponseDTO;
import com.raxrot.back.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO>createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        CategoryResponseDTO categoryResponseDTO=categoryService.create(categoryRequestDTO);
        return new ResponseEntity<>(categoryResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categoryResponseDTOS=categoryService.getAllCategories();
        return new ResponseEntity<>(categoryResponseDTOS, HttpStatus.OK);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByType(@PathVariable String type) {
      List<CategoryResponseDTO>categoryResponseDTOS=categoryService.getAllCategoriesByType(type);
      return new ResponseEntity<>(categoryResponseDTOS, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id,@Valid @RequestBody CategoryRequestDTO categoryDTO) {
        CategoryResponseDTO categoryResponseDTO=categoryService.updateCategory(id,categoryDTO);
        return new ResponseEntity<>(categoryResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
