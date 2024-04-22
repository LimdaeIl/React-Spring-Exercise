package org.zerock.mallapi.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.mallapi.domain.Todo;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@Log4j2
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void test1() {
        Assertions.assertNotNull(todoRepository);
        log.info(todoRepository.getClass().getName());
    }

    @Test
    public void testInsert() {

        for (int i = 0; i < 100; i++) {


            Todo todo = Todo.builder()
                    .title("Title")
                    .content("Content...")
                    .dueDate(LocalDate.of(2024, 4, 30))
                    .build();

            Todo save = todoRepository.save(todo);

            log.info(save);

        }
    }

    @Test
    public void testRead() {
        Long tno = 1L;

        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        log.info(todo);

    }

    @Test
    public void testUpdate() {

        // 먼저 로딩하고 엔티티 객체를 변경 /setter
        Long tno = 1L;

        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        todo.changeTitle("Update Title");
        todo.changeContent("updated content");
        todo.changeComplete(true);

        todoRepository.save(todo);

    }

    /**
     * 10건 이상 조회가 안된다면, count 쿼리를 만들지 않습니다. 1 건만 저장된 경우에는 limit 쿼리만 수행됩니다.
     */
    @Test
    public void testPaging() {

        // 페이지 시작 번호: 0
        Pageable pageable = PageRequest.of(0, 10, Sort.by("tno").descending());

        Page<Todo> result = todoRepository.findAll(pageable);

        log.info(result.getTotalElements());

        log.info(result.getContent());

    }

    @Test
    public void testSearch1() {

        todoRepository.search1();

    }
}
