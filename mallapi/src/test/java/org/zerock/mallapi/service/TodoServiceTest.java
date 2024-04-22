package org.zerock.mallapi.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.mallapi.dto.TodoDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class TodoServiceTest {

    @Autowired
    TodoService todoService;

    @Test
    public void testGet() {
        Long tno = 50L;

        log.info(todoService.get(tno));
    }

    @Test
    public void testRegister() {
        TodoDTO todoDTO = TodoDTO.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .dueDate(LocalDate.of(2024,4,22))
                .build();

        log.info(todoService.register(todoDTO));
    }
}