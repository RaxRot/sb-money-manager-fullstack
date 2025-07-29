package com.raxrot.back.services.impl;


import com.raxrot.back.dtos.CategoryRequestDTO;
import com.raxrot.back.dtos.CategoryResponseDTO;
import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.entities.Category;
import com.raxrot.back.entities.User;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.repositories.CategoryRepository;
import com.raxrot.back.repositories.UserRepository;
import com.raxrot.back.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UserResponseDTO userResponseDTO;
    private User user;
    private CategoryRequestDTO categoryRequestDTO;
    private Category category;
    private CategoryResponseDTO categoryResponseDTO;

    @BeforeEach
    void setUp() {
        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .build();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .fullName("Test User")
                .build();

        categoryRequestDTO = CategoryRequestDTO.builder()
                .name("Food")
                .type("expense")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Food")
                .type("expense")
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        categoryResponseDTO = CategoryResponseDTO.builder()
                .id(1L)
                .name("Food")
                .type("expense")
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    @Test
    void create_shouldCreateCategory() {
        when(userService.getUserCurrentUser(null)).thenReturn(userResponseDTO);
        when(userRepository.findByEmail(userResponseDTO.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.existsByNameAndUser(categoryRequestDTO.getName(), user)).thenReturn(false);
        when(modelMapper.map(categoryRequestDTO, Category.class)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryResponseDTO.class)).thenReturn(categoryResponseDTO);

        CategoryResponseDTO result = categoryService.create(categoryRequestDTO);

        assertNotNull(result);
        assertEquals("Food", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void create_shouldThrowExceptionIfCategoryExists() {
        when(userService.getUserCurrentUser(null)).thenReturn(userResponseDTO);
        when(userRepository.findByEmail(userResponseDTO.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.existsByNameAndUser("Food", user)).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> categoryService.create(categoryRequestDTO));
        assertEquals("Category already exists", ex.getMessage());
    }

    @Test
    void getAllCategories_shouldReturnList() {
        when(userService.getUserCurrentUser(null)).thenReturn(userResponseDTO);
        when(userRepository.findByEmail(userResponseDTO.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.findByUser(user)).thenReturn(List.of(category));
        when(modelMapper.map(category, CategoryResponseDTO.class)).thenReturn(categoryResponseDTO);

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getName());
    }

    @Test
    void getAllCategoriesByType_shouldReturnFilteredList() {
        when(userService.getUserCurrentUser(null)).thenReturn(userResponseDTO);
        when(userRepository.findByEmail(userResponseDTO.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.findByTypeAndUser("expense", user)).thenReturn(List.of(category));
        when(modelMapper.map(category, CategoryResponseDTO.class)).thenReturn(categoryResponseDTO);

        List<CategoryResponseDTO> result = categoryService.getAllCategoriesByType("expense");

        assertEquals(1, result.size());
        assertEquals("expense", result.get(0).getType());
    }

    @Test
    void updateCategory_shouldUpdateSuccessfully() {
        when(userService.getUserCurrentUser(null)).thenReturn(userResponseDTO);
        when(userRepository.findByEmail(userResponseDTO.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryResponseDTO.class)).thenReturn(categoryResponseDTO);

        CategoryResponseDTO result = categoryService.updateCategory(1L, categoryRequestDTO);

        assertNotNull(result);
        assertEquals("Food", result.getName());
    }

    @Test
    void updateCategory_shouldThrowIfNotFound() {
        when(userService.getUserCurrentUser(null)).thenReturn(userResponseDTO);
        when(userRepository.findByEmail(userResponseDTO.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> categoryService.updateCategory(1L, categoryRequestDTO));
        assertEquals("Category not found", ex.getMessage());
    }

    @Test
    void deleteCategory_shouldDeleteSuccessfully() {
        when(userService.getUserCurrentUser(null)).thenReturn(userResponseDTO);
        when(userRepository.findByEmail(userResponseDTO.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_shouldThrowIfNotFound() {
        when(userService.getUserCurrentUser(null)).thenReturn(userResponseDTO);
        when(userRepository.findByEmail(userResponseDTO.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> categoryService.deleteCategory(1L));
        assertEquals("Category not found", ex.getMessage());
    }
}
