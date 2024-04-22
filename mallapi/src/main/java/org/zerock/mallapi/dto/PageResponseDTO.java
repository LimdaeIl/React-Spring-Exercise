package org.zerock.mallapi.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResponseDTO<E> {

    private List<E> dtoList; // DTO 목록

    private List<Integer> pageNumList; // 페이지 번호

    private PageRequestDTO pageRequestDTO; // 페이지 요청 DTO

    private boolean prev, next;

    private int totalCount, prevPage, nextPage, totalPage, current; // current 페이지는 이미 PageRequestDTO 안에 있지만 생성했습니다.

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(List<E> dtoList, PageRequestDTO pageRequestDTO, long total) {

        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int) total;

        // 마지막 페이지부터 계산하기
        int end = (int) (Math.ceil(pageRequestDTO.getPage() / 10.0) * 10);// (현재 페이지 / 10.0) * 10 = 마지막 페이지

        // 시작 페이지
        int start = end - 9;

        // 진짜 마지막
        int last = (int) (Math.ceil(totalCount / (double) pageRequestDTO.getSize()));

        end = Math.min(end, last);

        this.prev = start > 1;

        this.next = totalCount > end * pageRequestDTO.getSize();

        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

        this.prevPage = start > 1 ? start - 1 : 0;

        this.nextPage = next ? end + 1 : 0;

    }
}
