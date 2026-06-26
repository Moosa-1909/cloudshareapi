package in.moosaviqar.cloudshareapi.service;

import com.cloudinary.Cloudinary;
import in.moosaviqar.cloudshareapi.document.FileMetaDataDocument;
import in.moosaviqar.cloudshareapi.document.ProfileDocument;
import in.moosaviqar.cloudshareapi.dto.FileMetaDataDTO;
import in.moosaviqar.cloudshareapi.repository.FileMetaDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileMetaDataService {
    private final ProfileService profileService;
    private final FileMetaDataRepository fileMetaDataRepository;
    private final Cloudinary cloudinary;

    public List<FileMetaDataDTO> uploadFiles(MultipartFile[] files) throws IOException {
        ProfileDocument currentProfile = profileService.getCurrentProfile();
        List<FileMetaDataDocument> savedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "resource_type", "auto",
                            "public_id", UUID.randomUUID().toString()
                    )
            );

            String fileUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            FileMetaDataDocument fileMetaData = FileMetaDataDocument.builder()
                    .fileLocation(fileUrl)
                    .name(file.getOriginalFilename())
                    .size(file.getSize())
                    .type(file.getContentType())
                    .clerkId(currentProfile.getClerkId())
                    .isPublic(false)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            savedFiles.add(fileMetaDataRepository.save(fileMetaData));
        }
        return savedFiles.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private FileMetaDataDTO mapToDTO(FileMetaDataDocument fileMetaDataDocument) {
        return FileMetaDataDTO.builder()
                .id(fileMetaDataDocument.getId())
                .fileLocation(fileMetaDataDocument.getFileLocation())
                .name(fileMetaDataDocument.getName())
                .size(fileMetaDataDocument.getSize())
                .type(fileMetaDataDocument.getType())
                .clerkId(fileMetaDataDocument.getClerkId())
                .isPublic(fileMetaDataDocument.getIsPublic())
                .uploadedAt(fileMetaDataDocument.getUploadedAt())
                .build();
    }

    public List<FileMetaDataDTO> getFiles() {
        ProfileDocument currentProfile = profileService.getCurrentProfile();
        List<FileMetaDataDocument> files = fileMetaDataRepository.findByClerkId(currentProfile.getClerkId());
        return files.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public FileMetaDataDTO getPublicFile(String id) {
        Optional<FileMetaDataDocument> fileOptional = fileMetaDataRepository.findById(id);
        if (fileOptional.isEmpty() || !fileOptional.get().getIsPublic()) {
            throw new RuntimeException("Unable to get file");
        }
        return mapToDTO(fileOptional.get());
    }

    public FileMetaDataDTO getDownloadableFile(String id) {
        FileMetaDataDocument file = fileMetaDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("file not found"));
        return mapToDTO(file);
    }

    public void deleteFile(String id) {
        try {
            ProfileDocument currentProfile = profileService.getCurrentProfile();
            FileMetaDataDocument file = fileMetaDataRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            if (!file.getClerkId().equals(currentProfile.getClerkId())) {
                throw new RuntimeException("File does not belong to current user");
            }

            String fileUrl = file.getFileLocation();
            String publicId = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.lastIndexOf("."));
            cloudinary.uploader().destroy(publicId, Map.of("resource_type", "raw"));

            fileMetaDataRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting the file");
        }
    }

    public FileMetaDataDTO togglePublic(String id) {
        FileMetaDataDocument file = fileMetaDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
        file.setIsPublic(!file.getIsPublic());
        fileMetaDataRepository.save(file);
        return mapToDTO(file);
    }
}