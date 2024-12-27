package com.project.popupmarket.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.Paths;

@Service
public class PopupStoreFileStorageService {
    // 파일을 지정된 디렉토리에 저장
    public void storeFile(MultipartFile file, String directoryPath, String customName) throws IOException {
        // 디렉토리가 없으면 생성
        Path path = Paths.get(directoryPath);
        if ( !Files.exists(path) ) {
            Files.createDirectory(path);
        }

        Path filePath = path.resolve(customName);

        // 파일 저장
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to store file " + customName, e);
        }

    }

}
