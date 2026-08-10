package com.edugauge.service;


import com.edugauge.domain.Category;
import com.edugauge.domain.user.User;
import com.edugauge.dto.CategoryCreateRequest;
import com.edugauge.dto.CategoryResponse;
import com.edugauge.repositiry.CategoryRepository;
import com.edugauge.repositiry.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    public void createCategory(
            Long userId,
            CategoryCreateRequest request
    ){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다")
        );
        Category category = new Category(
                request.getName(), user
        );
        categoryRepository.save(category);


    }
    public List<CategoryResponse> getCategories(Long userId){
        return categoryRepository.findByUser_Id(userId)
                .stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .toList();
    }
}
