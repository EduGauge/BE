package com.edugauge.controller;

import com.edugauge.dto.CategoryCreateRequest;
import com.edugauge.dto.CategoryResponse;
import com.edugauge.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.edugauge.dto.CategoryUpdateRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping
    public void createCategory(
            @AuthenticationPrincipal Long userId,
            @RequestBody CategoryCreateRequest request
            ){
        categoryService.createCategory(userId, request);
    }
    @GetMapping
    public List<CategoryResponse> getCategories(
            @AuthenticationPrincipal Long userId){
        return categoryService.getCategories(userId);
    }

    @PatchMapping("/{categoryId}")
    public void updateCategory(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long categoryId,
            @RequestBody CategoryUpdateRequest request
    ) {
        categoryService.updateCategory(userId, categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(userId, categoryId);
    }

}
