package com.edugauge.service;


import com.edugauge.domain.Category;
import com.edugauge.domain.user.User;
import com.edugauge.dto.CategoryCreateRequest;
import com.edugauge.dto.CategoryResponse;
import com.edugauge.repositiry.CategoryRepository;
import com.edugauge.repositiry.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.edugauge.domain.Todo;
import com.edugauge.dto.CategoryUpdateRequest;
import com.edugauge.repositiry.TodoRepository;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

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
    public void updateCategory(
            Long userId,
            Long categoryId,
            CategoryUpdateRequest request
    ) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));

        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 카테고리만 수정할 수 있습니다");
        }

        category.updateName(request.getName());
    }

    public void deleteCategory(
            Long userId,
            Long categoryId
    ) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));

        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 카테고리만 삭제할 수 있습니다");
        }

        List<Todo> todos = todoRepository.findByCategory_IdAndUser_Id(categoryId, userId);
        todoRepository.deleteAll(todos);

        categoryRepository.delete(category);
    }
}
