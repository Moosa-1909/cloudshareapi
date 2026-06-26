package in.moosaviqar.cloudshareapi.controller;

import in.moosaviqar.cloudshareapi.dto.FileMetaDataDTO;
import in.moosaviqar.cloudshareapi.service.FileMetaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class FileController {

    private final FileMetaDataService fileMetaDataService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestPart("files") MultipartFile[] files) throws IOException {
        Map<String, Object> response = new HashMap<>();
        List<FileMetaDataDTO> list = fileMetaDataService.uploadFiles(files);
        response.put("files", list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getFilesForCurrentUser() {
        List<FileMetaDataDTO> files = fileMetaDataService.getFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicFile(@PathVariable String id) {
        FileMetaDataDTO file = fileMetaDataService.getPublicFile(id);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Void> download(@PathVariable String id) throws IOException {
        FileMetaDataDTO file = fileMetaDataService.getDownloadableFile(id);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, file.getFileLocation())
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        fileMetaDataService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-public")
    public ResponseEntity<?> togglePublic(@PathVariable String id) {
        FileMetaDataDTO file = fileMetaDataService.togglePublic(id);
        return ResponseEntity.ok(file);
    }
}