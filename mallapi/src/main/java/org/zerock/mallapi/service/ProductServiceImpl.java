package org.zerock.mallapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.domain.ProductImage;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;
import org.zerock.mallapi.repository.ProductRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        // Object[] => 0 product 1 productImage
        // Object[] => 0 product 1 productImage
        // Object[] => 0 product 1 productImage

        List<ProductDTO> dtoList = result.get().map(arr -> {

            ProductDTO productDTO = null;

            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];

            productDTO = ProductDTO.builder()
                    .pno(product.getPno())
                    .pdesc(product.getPdesc())
                    .pname(product.getPname())
                    .price(product.getPrice())
                    .build();

            String imageStr = productImage.getFileName();
            productDTO.setUploadFileNames(List.of(imageStr));

            return productDTO; // 프로젝션으로도 처리하는 방법이 있습니다.
        }).toList();

        long totalCount = result.getTotalElements();

        return PageResponseDTO.<ProductDTO>withAll()
                .dtoList(dtoList)
                .total(totalCount)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    @Override
    public Long register(ProductDTO productDTO) {

        Product product = dtoToEntity(productDTO);

        log.info("-------------------------------");
        log.info(product);
        log.info(product.getImageList());

        Long pno = productRepository.save(product).getPno();

        return pno;
    }

    @Override
    public ProductDTO get(Long pno) {

        Optional<Product> result = productRepository.findById(pno);

        Product product = result.orElseThrow();

        return entityToDTO(product);
    }

    @Override
    public void modify(ProductDTO productDTO) {
        // 문제는 파일이 변경 된 건지, 아닌지 알 수가 없습니다.

        // 조회
        Optional<Product> result = productRepository.findById(productDTO.getPno());

        Product product = result.orElseThrow();

        // 변경 내용을 반영
        product.changePrice(productDTO.getPrice());
        product.changeName(productDTO.getPname());
        product.changeDesc(productDTO.getPdesc());
        product.changeDel(productDTO.isDelFlag());

        // 이미지를 처리하기 위해서 목록을 비워야 합니다.
        List<String> uploadFileNames = productDTO.getUploadFileNames(); // 이전에 저장된 이미지 이름들
        product.clearList(); // 이미지 이름 리스트 초기화

        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(product::addImageString);
        }

        // 저장
        productRepository.save(product);
    }

    @Override
    public void remove(Long pno) {

        productRepository.deleteById(pno);

    }

    private ProductDTO entityToDTO(Product product) {

        ProductDTO productDTO = ProductDTO.builder()
                .pno(product.getPno())
                .pdesc(product.getPdesc())
                .pname(product.getPname())
                .price(product.getPrice())
                .delFlag(product.isDelFlag())
                .build();

        List<ProductImage> imageList = product.getImageList();

        if (imageList == null || imageList.isEmpty()) {
            return productDTO;
        }

        List<String> fileNameList = imageList.stream().map(ProductImage::getFileName).toList();

        productDTO.setUploadFileNames(fileNameList);

        return productDTO;
    }

    private Product dtoToEntity(ProductDTO productDTO) {
        Product product = Product.builder()
                .pno(productDTO.getPno())
                .pdesc(productDTO.getPdesc())
                .pname(productDTO.getPname())
                .price(productDTO.getPrice())
                .build();

        List<String> uploadFileNames = productDTO.getUploadFileNames();

        if (uploadFileNames == null || uploadFileNames.size() == 0) {
            return product;
        }

        uploadFileNames.forEach(product::addImageString);

        return product;
    }

}
