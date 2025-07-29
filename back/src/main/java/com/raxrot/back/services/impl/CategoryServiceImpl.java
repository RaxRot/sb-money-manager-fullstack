package com.raxrot.back.services.impl;

import com.raxrot.back.configurations.AppConstants;
import com.raxrot.back.dtos.CategoryRequestDTO;
import com.raxrot.back.dtos.CategoryResponseDTO;
import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.entities.Category;
import com.raxrot.back.entities.User;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.repositories.CategoryRepository;
import com.raxrot.back.repositories.UserRepository;
import com.raxrot.back.services.CategoryService;
import com.raxrot.back.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ModelMapper modelMapper,
                              UserService userService,
                               UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public CategoryResponseDTO create(CategoryRequestDTO categoryDTO) {
        UserResponseDTO currentUser =getUserDTO();
        User user = getUser(currentUser);

        if (categoryRepository.existsByNameAndUser(categoryDTO.getName(), user)) {
            throw new ApiException(AppConstants.CATEGORY_ALREADY_EXISTS);
        }

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryResponseDTO.class);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        UserResponseDTO currentUser = getUserDTO();
        User user = getUser(currentUser);
        List<Category> categories = categoryRepository.findByUser(user);
        List<CategoryResponseDTO> categoryResponseDTOS=categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
        return categoryResponseDTOS;
    }

    @Override
    public List<CategoryResponseDTO> getAllCategoriesByType(String type) {
        UserResponseDTO currentUser = getUserDTO();
        User user = getUser(currentUser);
        List<Category>categories=categoryRepository.findByTypeAndUser(type, user);
        List<CategoryResponseDTO>categoryResponseDTOS=categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
        return categoryResponseDTOS;
    }

    @Override
    public CategoryResponseDTO updateCategory(Long categoryId, CategoryRequestDTO category) {
        UserResponseDTO currentUser = getUserDTO();
        User user = getUser(currentUser);
        Category categoryToUpdate=categoryRepository.findByIdAndUser(categoryId,user)
                .orElseThrow(() -> new ApiException(AppConstants.CATEGORY_NOT_FOUND));
        categoryToUpdate.setName(category.getName());
        categoryToUpdate.setType(category.getType());
        Category savedCategory = categoryRepository.save(categoryToUpdate);
        return modelMapper.map(savedCategory, CategoryResponseDTO.class);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        UserResponseDTO currentUser = getUserDTO();
        User user = getUser(currentUser);
        Category category = categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new ApiException(AppConstants.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }

    private User getUser(UserResponseDTO currentUser) {
        return userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new ApiException(AppConstants.USER_NOT_FOUND));
    }

    private UserResponseDTO getUserDTO() {
        return userService.getUserCurrentUser(null);
    }

}
