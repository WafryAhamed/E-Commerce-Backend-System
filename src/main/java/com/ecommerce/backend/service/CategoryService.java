package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.category.CategoryRequest;
import com.ecommerce.backend.dto.category.CategoryResponse;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CategoryRequest request) {
        categoryRepository.findByName(request.getName()).ifPresent(existing -> {
            throw new BadRequestException("Category already exists");
        });

        Category category = Category.builder().name(request.getName()).build();
        Category saved = categoryRepository.save(category);
        log.info("Category created: {}", saved.getName());
        return toResponse(saved);
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getEntityById(id);
        category.setName(request.getName());
        Category updated = categoryRepository.save(category);
        log.info("Category updated: {}", updated.getId());
        return toResponse(updated);
    }

    public void delete(Long id) {
        Category category = getEntityById(id);
        categoryRepository.delete(category);
        log.info("Category deleted: {}", id);
    }

    public Category getEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}

