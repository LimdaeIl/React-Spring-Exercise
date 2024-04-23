package org.zerock.mallapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;
import org.zerock.mallapi.service.ProductService;
import org.zerock.mallapi.util.CustomFileUtil;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUtil fileUtil;

    private final ProductService productService;

//    @PostMapping("/")
//    public Map<String, String> register(ProductDTO productDTO) {
//
//        log.info("register: {}", productDTO);
//
//        List<MultipartFile> files = productDTO.getFiles();
//        List<String> uploadedFileNames = fileUtil.saveFiles(files);
//        productDTO.setUploadFileNames(uploadedFileNames);
//
//        log.info(uploadedFileNames);
//
//        return Map.of("RESULT","SUCCESS"); // Todo 처럼 Long 값 사용도 괜찮습니다.
//    }

    // 별도의 컨트롤러로 빼주는 것이 좋다고 합니다.
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName) {
        return fileUtil.getFile(fileName);
    }

    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        return productService.getList(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, Long> register(ProductDTO productDTO) {

        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadFileNames = fileUtil.saveFiles(files);

        productDTO.setUploadFileNames(uploadFileNames);
        log.info(uploadFileNames);

        Long pno = productService.register(productDTO);

        return Map.of("result", pno);
    }

    @GetMapping("/{pno}")
    public ProductDTO read(@PathVariable("pno") Long pno) {
        return productService.get(pno);
    }

    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable Long pno, ProductDTO productDTO) {
        // 이미지 업로드 저장
        productDTO.setPno(pno);

        // 이미 저장되고 데이터베이스에 존재하는 파일 -> old product
        ProductDTO oldProductDTO = productService.get(pno);

        // 파일 업로드
        List<MultipartFile> files = productDTO.getFiles(); // 새로운 파일들
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);

        // 수정이 되더라도 유지되는 파일 이름들
        List<String> uploadedFileNames = productDTO.getUploadFileNames();

        if (currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
            uploadedFileNames.addAll(currentUploadFileNames);
        }

        productService.modify(productDTO);

        List<String> oldFileNames = oldProductDTO.getUploadFileNames();
        if (oldFileNames != null && !oldFileNames.isEmpty()) {
            List<String> removeFiles = oldFileNames.stream().filter(fileName ->
                    !uploadedFileNames.contains(fileName)
            ).toList();

            fileUtil.deleteFiles(removeFiles); // 수정되면서 사라진 파일을 삭제

        } // end if

        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{pno}")
    public Map<String, String> remove(@PathVariable Long pno) {

        List<String> oldFileNames = productService.get(pno).getUploadFileNames();
        oldFileNames.remove(pno);
        fileUtil.deleteFiles(oldFileNames);

        return Map.of("RESULT", "SUCCESS");
    }
}
