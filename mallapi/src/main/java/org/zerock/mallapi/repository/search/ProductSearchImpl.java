package org.zerock.mallapi.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.domain.QProduct;
import org.zerock.mallapi.domain.QProductImage;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;

import java.util.List;
import java.util.Objects;

@Log4j2
public class ProductSearchImpl extends QuerydslRepositorySupport implements ProductSearch {

    public ProductSearchImpl() {
        super(Product.class);
    }

    @Override
    public PageResponseDTO<ProductDTO> searchList(PageRequestDTO pageRequestDTO) {

        log.info("---------------------------searchList-------------------------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending());

        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;

        JPQLQuery<Product> query = from(product); // Product 에 대한 JPQL 쿼리 작성
        query.leftJoin(product.imageList, productImage); // product 안의 이미지 리스트를 productImage 로 간주한다는 의미입니다. 엘리먼트 컬렉션으로 querydsl 사용법입니다.

        query.where(productImage.ord.eq(0));



        Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);



        List<Tuple> productList = query
                .select(product, productImage)
                .fetch();

        long count = query.fetchCount();

        log.info("============================================");
        log.info(productList);

        return null;
    }
}
