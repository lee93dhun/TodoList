package com.test_project.todoList.service;

import com.test_project.todoList.model.TodoEntity;
import com.test_project.todoList.persistence.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {

    @Autowired
    private TodoRepository repository;

    public String testService(){
        // TodoEntity 생성
        TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
        // TodoEntity 저장
        repository.save(entity);
        // TodoEntity 검색
        TodoEntity savedEntity = repository.findById(entity.getId()).get();

        return savedEntity.getTitle();
    }

    public List<TodoEntity> create(final TodoEntity entity) {
        // Validations
        validate(entity);

        repository.save(entity);

        log.info("Entity Id : {} is saved.",entity.getId());

        return repository.findByUserId(entity.getUserId());
    }

    public List<TodoEntity> retrieve(final String userId) {
        log.info("Entity Id : {} is searched.",userId);
        return repository.findByUserId(userId);
    }

    public List<TodoEntity> update(final TodoEntity entity) {
        // 1. 저장할 엔티티가 유효한지 확인한다. 이 메서드는 2.3.1 Create Todo에서 구현했음.
        validate(entity);
        // 2. 넘겨받은 엔티티 id 를 이용해 TodoEntity 를 가져온다. 존재하지 않는 엔티티는 업데이트 할 구 없기 떄문.
        final Optional<TodoEntity> original = repository.findById(entity.getId());
        // Optional 은 값이 존재하거나 비어있을  수 있는 컨테이너

        original.ifPresent( todo -> {   // original에 값이 존재하면 {…} 가 실행됨
            // 3. 반환된 TodoEntity 가 존재하면 값을 새 entity 값으로 덮어 씌운다.
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
            // 4. 데이터 베이스에 새 값을 저장한다.
            repository.save(todo);
        });

        log.info("Entity Id : updated of {}s.",entity.getId());

        // 2.3.2 Retrieve Todo에서 만든 메서드를 이용해 사용자의 모든 Todo리스트를 리턴한다.
        return retrieve(entity.getUserId());
    }

    public List<TodoEntity> delete(final TodoEntity entity) {
        // 1. 저장 할 엔티티가 유효한지 확인
        validate(entity);

        try {
            // 2. 엔티티 삭제
            repository.delete(entity);
        } catch (Exception e) {
            // 3. exception 발생 시 id 와 exception 을 로깅한다.
            log.error("error deleting entity", entity.getId(), e);
            // 4. 컨트롤러로 exception 을 보낸다. 데이터베이스 내부 로직을 캡슐화 하려면 e를 리턴하지 않고
            // 새 exception 오브제트를 리턴한다.
            throw new RuntimeException("error deleting entity " + entity.getId());
        }
        // 5. 새 Todo리스트를 가져와 리턴한다.
        return retrieve(entity.getUserId());
    }


    private void validate(final TodoEntity entity) {
        if (entity == null) {
            log.warn("Entity can't be null.");
            throw new RuntimeException("Entity can't be null");
        }
        if (entity.getUserId() == null) {
            log.warn("Unknown user");
            throw new RuntimeException("Unknown user");
        }
    }
}
