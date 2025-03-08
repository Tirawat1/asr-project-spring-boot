package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.services.MinioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public Map<String, String> getDownloadUrl(@PathVariable String filename) throws Exception {
        String url = minioService.getAudioPresignedUrl(filename);
        return Map.of("url", url);
    }

    @GetMapping("/test")
    public String getMethodName(@RequestParam String param) {
        return "hello world!";
    }
}
