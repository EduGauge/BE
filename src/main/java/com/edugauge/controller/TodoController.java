package com.edugauge.controller;

import com.edugauge.dto.TodoCreateRequest;
import com.edugauge.dto.TodoResponse;
import com.edugauge.dto.TodoUpdateRequest;
import com.edugauge.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.edugauge.dto.TodoCompleteResponse;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService todoService;
    @PostMapping
    public void createTodo(
            @AuthenticationPrincipal Long userId,
            @RequestBody TodoCreateRequest request
            ){
        todoService.createTodo(userId, request);
    }

    @GetMapping
    public List<TodoResponse> getTodos(
            @AuthenticationPrincipal Long userId){
        return todoService.getTodos(userId);
    }

    @PatchMapping("/{todoId}")
    public void updateTodo(
            @PathVariable Long todoId,
            @AuthenticationPrincipal Long userId,
            @RequestBody TodoUpdateRequest request
    ){
        todoService.updateTodo(userId, todoId, request);
    }

    @DeleteMapping("/{todoId}")
    public void deleteTodo(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long todoId
    ){
        todoService.deleteTodo(userId, todoId);
    }

    @PatchMapping("/{todoId}/complete")
    public TodoCompleteResponse completeTodo(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long todoId
    ){
        return todoService.completeTodo(userId, todoId);
    }
}
