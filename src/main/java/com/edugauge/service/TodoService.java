package com.edugauge.service;

import com.edugauge.domain.Category;
import com.edugauge.domain.DailyProgress;
import com.edugauge.domain.Todo;
import com.edugauge.domain.user.User;
import com.edugauge.dto.TodoCompleteResponse;
import com.edugauge.dto.TodoCreateRequest;
import com.edugauge.dto.TodoResponse;
import com.edugauge.dto.TodoUpdateRequest;
import com.edugauge.repositiry.CategoryRepository;
import com.edugauge.repositiry.DailyProgressRepository;
import com.edugauge.repositiry.TodoRepository;
import com.edugauge.repositiry.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DailyProgressRepository dailyProgressRepository;
    private final StudyDateService studyDateService;
    private static final int TODO_COMPLETE_EXPERIENCE = 10;
    private static final int DAILY_COMPLETION_BONUS_EXPERIENCE = 50;

    public void createTodo(Long userId, TodoCreateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));
        if(!category.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("본인의 카테고리만 사용할 수 있습니다");
        }
        Todo todo = new Todo(
                category,
                user,
                request.getTitle(),
                request.getRepeatType(),
                request.getEndDate()
        );
        todoRepository.save(todo);

        LocalDate today = studyDateService.getCurrentStudyDate();

        Optional<DailyProgress> progress =
                dailyProgressRepository.findByUser_IdAndProgressDate(
                        userId,
                        today
                );

        if (progress.isEmpty()) {
            DailyProgress newProgress = new DailyProgress(
                    user,
                    today,
                    1
            );

            dailyProgressRepository.save(newProgress);
            return;
        }

        DailyProgress dailyProgress = progress.get();

        if (dailyProgress.calculateGauge() < 100) {
            dailyProgress.addBaseTodo();
        }

    }
    public List<TodoResponse> getTodos(Long userId){
        List<Todo> todos = todoRepository.findByUser_Id(userId);

        return todos.stream()
                .map(todo -> new TodoResponse(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCategory().getId(),
                        todo.isCompleted(),
                        todo.getCreatedAt(),
                        todo.getUpdatedAt(),
                        todo.getEndDate(),
                        todo.getCategory().getName(),
                        todo.getRepeatType()

                ))
                .toList();
    }
    public void updateTodo(Long userId, Long todoId, TodoUpdateRequest request){
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("Todo를 찾을 수 없습니다"));
        if(!todo.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("본인의 todo만 수정할 수 있습니다");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));
        if(!category.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("본인의 카테고리만 사용할 수 있습니다");
        }
        if (todo.isCompleted()) {
            throw new IllegalArgumentException("완료된 Todo는 수정할 수 없습니다");
        }

        todo.update(category, request.getTitle(), request.getRepeatType(), request.getEndDate());

    }

    public void deleteTodo(Long userId, Long todoId){
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(()-> new IllegalArgumentException("Todo를 찾을 수 없습니다"));
        if(!todo.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("본인의 todo만 삭제할 수 있습니다");

        }
        if (todo.isCompleted()) {
            throw new IllegalArgumentException("완료된 Todo는 삭제할 수 없습니다");
        }
        LocalDate today = studyDateService.getCurrentStudyDate();

        Optional<DailyProgress> progress =
                dailyProgressRepository.findByUser_IdAndProgressDate(
                        userId,
                        today
                );

        if (progress.isPresent()) {
            DailyProgress dailyProgress = progress.get();

            if (dailyProgress.calculateGauge() < 100) {
                dailyProgress.removeBaseTodo();
            }
        }

        todoRepository.delete(todo);
    }


    public TodoCompleteResponse completeTodo(Long userId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Todo를 찾을 수 없습니다")
                );

        if (!todo.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 Todo만 완료할 수 있습니다");
        }

        if (todo.isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 Todo입니다");
        }

        todo.complete();

        int earnedExperience = TODO_COMPLETE_EXPERIENCE;
        int bonusExperience = 0;

        boolean levelUp = todo.getUser().addExperience(earnedExperience);

        LocalDate today = studyDateService.getCurrentStudyDate();

        Optional<DailyProgress> progress =
                dailyProgressRepository.findByUser_IdAndProgressDate(
                        userId,
                        today
                );

        DailyProgress dailyProgress;

        if (progress.isPresent()) {
            dailyProgress = progress.get();
            dailyProgress.completeTodo();

        } else {
            long todoCount = todoRepository.countByUser_Id(userId);

            dailyProgress = new DailyProgress(
                    todo.getUser(),
                    today,
                    (int) todoCount
            );

            dailyProgress.completeTodo();

            dailyProgressRepository.save(dailyProgress);
        }

        if (dailyProgress.receiveCompletionBonusIfPossible()) {
            bonusExperience = DAILY_COMPLETION_BONUS_EXPERIENCE;
            levelUp = todo.getUser().addExperience(bonusExperience) || levelUp;
        }

        int gauge = dailyProgress.calculateGauge();

        User user = todo.getUser();

        return new TodoCompleteResponse(
                todo.getId(),
                todo.isCompleted(),
                earnedExperience,
                bonusExperience,
                gauge,
                user.getLevel(),
                user.getExperience(),
                levelUp

        );
    }

}
