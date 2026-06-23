package in.moosaviqar.cloudshareapi.service;


import in.moosaviqar.cloudshareapi.document.FileMetaDataDocument;
import in.moosaviqar.cloudshareapi.document.ProfileDocument;
import in.moosaviqar.cloudshareapi.dto.FileMetaDataDTO;
import in.moosaviqar.cloudshareapi.repository.FileMetaDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileMetaDataService {
    private final ProfileService profileService;
    private final FileMetaDataRepository fileMetaDataRepository;

    public List<FileMetaDataDTO> uploadFiles(MultipartFile files[]) throws IOException {
       ProfileDocument currentProfile = profileService.getCurrentProfile();
       List<FileMetaDataDocument> savedFiles = new ArrayList<>();

       //yaha upload path aata
        Path uploadPath = Paths.get("upload").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        for(MultipartFile file : files){
            String filename = UUID.randomUUID()+"."+StringUtils.getFilename(file.getOriginalFilename());
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileMetaDataDocument fileMetaData = FileMetaDataDocument.builder()
                    .fileLocation(targetLocation.toString())
                    .name(file.getOriginalFilename())
                    .size(file.getSize())
                    .type(file.getContentType())
                    .clerkId(currentProfile.getClerkId())
                    .isPublic(false)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            savedFiles.add(fileMetaDataRepository.save(fileMetaData));
        }
            return savedFiles.stream().map(fileMetaDataDocument -> mapToDTO(fileMetaDataDocument))
            .collect(Collectors.toList());
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
}
