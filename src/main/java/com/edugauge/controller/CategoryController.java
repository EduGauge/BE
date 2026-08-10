package com.edugauge.controller;

import com.edugauge.dto.CategoryCreateRequest;
import com.edugauge.dto.CategoryResponse;
import com.edugauge.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

}
