package org.zerock.mallapi.repository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.zerock.mallapi.domain.Product;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Log4j2
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testInsert() {

        Product product = Product.builder()
                .pname("Test")
                .pdesc("TEst Desc")
                .price(10000)
                .build();

        product.addImageString(UUID.randomUUID() + "_" + "IMAGE1.jpg");
        product.addImageString(UUID.randomUUID() + "_" + "IMAGE2.jpg");

        productRepository.save(product);

    }

    @Test
    public void testRead() {
        Long pno = 1L;

        Optional<Product> result = productRepository.findById(pno);

        Product product = result.orElseThrow();

        log.info("product : {}", product);
        log.info("product.getImageList() : {}", product.getImageList());
    }


    @Test
    public void testRead2() {
        Long pno = 1L;

        Optional<Product> result = productRepository.selectOne(pno);

        Product product = result.orElseThrow();

        log.info("product : {}", product);
        log.info("product.getImageList() : {}", product.getImageList());

    }

    @Test
    @Transactional
    @Commit
    public void testDelete() {
        Long pno = 2L;

        productRepository.updateToDelete(pno, true);
    }

    @Test
    public void testUpdate() {

        Product product = productRepository.selectOne(1L).orElseThrow();
        product.changePrice(3000);

        product.clearList();
        product.addImageString(UUID.randomUUID() + "_" + "IMAGE1.jpg");
        product.addImageString(UUID.randomUUID() + "_" + "IMAGE2.jpg");
        product.addImageString(UUID.randomUUID() + "_" + "IMAGE3.jpg");
        productRepository.save(product);
    }
}
