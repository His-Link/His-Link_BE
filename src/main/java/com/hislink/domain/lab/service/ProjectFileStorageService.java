package com.hislink.domain.lab.service;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.config.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectFileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final UploadProperties uploadProperties;

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "빈 이미지 파일입니다.");
        }
        if (file.getSize() > uploadProperties.getMaxFileSizeBytes()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미지 파일 크기는 5MB 이하여야 합니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "지원하지 않는 이미지 형식입니다. (JPEG, PNG, WEBP, GIF)");
        }

        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        String storedFileName = UUID.randomUUID() + extension;

        try {
            Path directory = projectUploadDirectory();
            Files.createDirectories(directory);
            Path target = directory.resolve(storedFileName);
            file.transferTo(target.toFile());
            return storedFileName;
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미지 저장에 실패했습니다.");
        }
    }

    public void delete(String storedFileName) {
        if (!StringUtils.hasText(storedFileName)) {
            return;
        }
        try {
            Path path = projectUploadDirectory().resolve(storedFileName);
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // best-effort cleanup
        }
    }

    public String toPublicUrl(String storedFileName) {
        if (!StringUtils.hasText(storedFileName)) {
            return null;
        }
        String base = uploadProperties.getPublicBaseUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/uploads/projects/" + storedFileName;
    }

    private Path projectUploadDirectory() {
        return Paths.get(uploadProperties.getDir(), "projects").toAbsolutePath().normalize();
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            if (ext.matches("\\.(jpe?g|png|webp|gif)")) {
                return ext;
            }
        }
        switch (contentType) {
            case "image/png":
                return ".png";
            case "image/webp":
                return ".webp";
            case "image/gif":
                return ".gif";
            default:
                return ".jpg";
        }
    }
}
