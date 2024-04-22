package org.zerock.mallapi.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoDTO {

    private Long tno;

    private String title;

    private String content;

    private boolean complete;

    private LocalDate dueDate;
}
