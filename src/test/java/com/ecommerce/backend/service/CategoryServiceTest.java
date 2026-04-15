package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.category.CategoryRequest;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void updateShouldRejectDuplicateCategoryNameFromAnotherRecord() {
        Category existing = Category.builder().id(1L).name("Electronics").build();
        Category duplicate = Category.builder().id(2L).name("Books").build();

        CategoryRequest request = new CategoryRequest();
        request.setName("Books");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("Books")).thenReturn(Optional.of(duplicate));

        assertThrows(BadRequestException.class, () -> categoryService.update(1L, request));
    }
}

