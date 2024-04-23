package org.zerock.mallapi.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
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

        if (files == null || files.size() == 0) {
            return null;
        }

        List<String> uploadFileNames = new ArrayList<>(); // 파일들의 이름

        for (MultipartFile file : files) {

            String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path savedPath = Paths.get(uploadPath, savedName);

            try {
                Files.copy(file.getInputStream(), savedPath);
                uploadFileNames.add(savedName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } // end for


        return uploadFileNames;
    }
}
