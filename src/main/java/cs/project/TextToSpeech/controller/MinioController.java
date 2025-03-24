package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.services.MinioService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/minio")
public class MinioController {
    private final MinioService minioService;

    public MinioController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/download-profile/{filename}")
    public String downloadProfile(@PathVariable String filename) {
        try {
            return minioService.getUserProfilePresignedObjectUrl(filename);
        } catch (Exception e) {
            return "Error generating presigned URL: " + e.getMessage();
        }
    }

    @PostMapping("/upload")
    public String uploadAudioFile(@RequestParam("audioFile") MultipartFile file) throws Exception {
        return minioService.uploadAudioFile(file);
    }

    @GetMapping("/downloadByUrl/{filename}")
    public String getFileByUrl(@PathVariable String filename) {
        try {
            String presignedUrl = minioService.getAudioPresignedObjectUrl(filename);

            return presignedUrl;
        } catch (Exception e) {
            return "Error generating presigned URL: " + e.getMessage();
        }
    }

    @GetMapping("/transcribe/{filename}")
    public String transcribeText(@PathVariable String filename){
        try{
            String transcribe = minioService.processTranscribe(filename);

            return transcribe;
        }catch(Exception e){
            return "Error generating a transcribe: " + e.getMessage(); 
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
