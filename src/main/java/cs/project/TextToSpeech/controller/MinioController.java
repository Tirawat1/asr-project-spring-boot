package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.services.MinioService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.net.HttpHeaders;

import java.io.InputStream;
import java.util.Map;


@RestController
@RequestMapping("/minio")
public class MinioController {
    private final MinioService minioService;

    public MinioController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public String uploadAudioFile(@RequestParam("audioFile") MultipartFile file) throws Exception {
        String fileName = minioService.uploadAudioFile(file);
        return fileName;
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename) throws Exception {
        try {
            InputStream fileInputStream = minioService.getAudioFile(filename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .body(new InputStreamResource(fileInputStream));

        } catch (RuntimeException e) {
            // If file retrieval fails, return an error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found or error retrieving file: " + e.getMessage());
        }
    }   

    @GetMapping("/downloadByUrl/{filename}")
    public String getFileByUrl(@PathVariable String filename) {
        try {
            String presignedUrl = minioService.getAudioPresignedUrl(filename);

            return presignedUrl;
        } catch (Exception e) {
            return "Error generating presigned URL: " + e.getMessage();
        }
    }
    

    @GetMapping("/list")
    public Map<String, Object> listAudioFiles() throws Exception {
        return Map.of("audioFiles", minioService.listAudioFiles());
    }

    @DeleteMapping("/delete/{filename}")
    public String deleteAudioFile(@PathVariable String filename) throws Exception {
        minioService.deleteAudioFile(filename);
        return "File deleted successfully";
    }

}
