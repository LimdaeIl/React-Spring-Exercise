package org.zerock.mallapi.service;

import jakarta.transaction.Transactional;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;

@Transactional
public interface ProductService {

    PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO);

    Long register(ProductDTO productDTO);

    ProductDTO get(Long pno);

    void modify(ProductDTO productDTO);

    void remove(Long pno); // 엘리먼트 컬렉션은 무조건 지워지게 됩니다. 종속적이기 때문입니다. 엔티티가 아닙니다.
}
