package org.zerock.mallapi.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @PostConstruct // 생성자 대신하는 경우에 사용하는 어노테이션으로, 폴더 생성하기 위한 메서드
    public void init() {

        File tempFolder = new File(uploadPath);
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }

        uploadPath = tempFolder.getAbsolutePath();
        log.info("uploadPath > {}", uploadPath);

    }

    public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {

        if (files == null || files.isEmpty()) {
            return null;
        }

        List<String> uploadFileNames = new ArrayList<>(); // 파일들의 이름

        for (MultipartFile file : files) {
            String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path savedPath = Paths.get(uploadPath, savedName);

            try {
                Files.copy(file.getInputStream(), savedPath); // 원본 파일 업로드입니다.
                String contentType = file.getContentType(); // Mime type 입니다.

                if (contentType != null || contentType.startsWith("image")) { // 이미지 파일이라면 썸네일 생성
                    Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
                    Thumbnails.of(savedPath.toFile()).size(200, 200).toFile(thumbnailPath.toFile());
                }

                uploadFileNames.add(savedName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } // end for
        return uploadFileNames;
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        if (!resource.isReadable()) {
            resource = new FileSystemResource(uploadPath + File.separator + "default.jpeg");
        }

        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath())); // Content-Type 이 Mime 타입!
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    public void deleteFiles(List<String> fileNames) {

        if (fileNames == null || fileNames.isEmpty()) { return; }

        fileNames.forEach(fileName -> {
            // 썸네일 삭제
            String thumbNameFileName = "s_" + fileName;

            // 두 개의 경로가 필요합니다. 썸네일 경로와 원본 경로가 필요합니다.
            Path thumbnailPath = Paths.get(uploadPath, thumbNameFileName);
            Path filePath = Paths.get(uploadPath, fileName);

            try {
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(thumbnailPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

