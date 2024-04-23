package org.zerock.mallapi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long pno;

    private String pname;

    private int price;

    private String pdesc;

    private boolean delFlag; // 많은 외래키가 걸리면서 구매 기록이 모두 지워지고 통계가 무너지는 상황이 발생한다.

    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>(); // 등록되는 파일 리스트

    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>(); // 저장된 파일 리스트

}
