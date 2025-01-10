package com.project.popupmarket.service.popup;

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

    // 파일 삭제
    public boolean deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (Files.exists(path)) {
            try {
                Files.delete(path);
                return true;
            } catch (IOException e) {
                System.err.println("Failed to delete file " + filePath + ": " + e.getMessage());
                return false; // 파일 삭제 실패
            }
        } else {
            System.err.println("File does not exist: " + filePath);
            return false; // 파일이 존재하지 않음
        }
    }
}
