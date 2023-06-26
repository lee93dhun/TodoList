package com.test_project.todoList.controller;

import com.test_project.todoList.dto.ResponseDTO;
import com.test_project.todoList.dto.TodoDTO;
import com.test_project.todoList.model.TodoEntity;
import com.test_project.todoList.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("todo")
public class TodoController {

    @Autowired
    private TodoService service;

    @GetMapping("test")
    public ResponseEntity<?> testTodo() {
        String str = service.testService(); // 테스트 서비스 사용
        List<String> list = new ArrayList<>();
        list.add(str);
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody TodoDTO dto) {
        try {
            // 일시적인 user id
            String temporaryUserId = "temporary-user";
            // 1. TodoEntity 로 변환
            TodoEntity entity = TodoDTO.toEntity(dto);
            // 2. id를 null 로 초기화. 생성 당시에는 id가 없어야 하기 때문
            entity.setId(null);
            // 3. 임시 사용자 id를 설정해 준다. // 4장 인증과 인가에서 수정함 인증과 인가가 없을시 temporary-user 사용
            entity.setUserId(temporaryUserId);
            // 4. 서비스를 이용해 Todo entity 를 생성
            List<TodoEntity> entities = service.create(entity);
            // 5. 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 반환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            // 6. 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
            // 7. ResPonseDTO 를 리턴한다.
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // 8. 혹시 예외가 있는 경우 dto 대신 error에 메시지를 넣어 리턴한다.
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveTodoList() {
        // 일시적인 user id
        String temporaryUserId = "temporary-user";
        // 1. 서비스 메서드의 retrieve()메소드를 사용해 TodoList 를 가져온다.
        List<TodoEntity> entities = service.retrieve(temporaryUserId);
        // 2. 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환.
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
        // 3. 변환된 TodoDTO 리스트를 이용해 ResponseDTO 를 초기화 한다.
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
        // 4. ResponseDTO 를 리턴한다.
        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@RequestBody TodoDTO dto) {
        String temporaryUserId = "temporary-user";

        // 1. dto 를 entity 로 변환
        TodoEntity entity = TodoDTO.toEntity(dto); // entity 로 변환 가능 이유 ?

        // 2. id를 temporaryUserId로 초기화한다.
        entity.setUserId(temporaryUserId);

        // 3. 서비스를 이용해 entity 를 업데이트 한다.
        List<TodoEntity> entities = service.update(entity);

        // 4. 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환한다.
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        // 5. 변환된 TodoDTO 리스트를 이용해 ResponseDTO 를 초기화.
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        // 6. ResponseDTO 를 리턴
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@RequestBody TodoDTO dto) {
        try {
            String temporaryUserId = "temporary-user";
            // 1. TodoEntity 로 변환한다.
            TodoEntity entity = TodoDTO.toEntity(dto);

            // 2. 임시 사용자 아이디를 설정해줌
            entity.setUserId(temporaryUserId);
            // 3. 서비스를 이용해 entity를 삭제
            List<TodoEntity> entities = service.delete(entity);
            // 4. 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            // 5. 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
            // 6. ResponseDTO 를 리턴
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // 7. 혹시 예외가 있는 경우 dto 대신 error 에 메세지를 넣어 리턴한다.
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
