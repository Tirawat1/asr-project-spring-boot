package cs.project.TextToSpeech.services;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import io.minio.messages.Item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String audioBucket = "audio-bucket";

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    public MinioService(@Value("${minio.url}") String minioUrl,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey) {
        // Initializing MinioClient using injected values
        this.minioUrl = minioUrl;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        
        this.minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    // List audio files from the bucket
    public List<String> listAudioFiles() throws Exception {
        List<String> audioFiles = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(audioBucket).build());
        for (Result<Item> result : results) {
            Item item = result.get();
            audioFiles.add(item.objectName());
        }
        return audioFiles;
    }

    // Check if the file exists in the bucket
    private boolean fileExists(String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(audioBucket)
                            .object(fileName)
                            .build()
            );
            return true; 
        } catch (Exception e) {
            return false; 
        }
    }

    // Upload an audio file to Minio
    public String uploadAudioFile(MultipartFile file) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        String baseName = originalFileName;

        // Extract file extension
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex != -1) {
            fileExtension = originalFileName.substring(dotIndex);
            baseName = originalFileName.substring(0, dotIndex);
        }

        String newFileName = originalFileName;
        int count = 1;

        // Check if the file exists, and increment the file name if it does
        while (fileExists(newFileName)) {
            newFileName = baseName + "_" + count + fileExtension;
            count++;
        }

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(audioBucket)
                            .object(newFileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
        return newFileName;
    }

    // Get an audio file from Minio
    public InputStream getAudioFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(audioBucket)
                        .object(fileName)
                        .build()
        );
    }

    // public String getFileUrl(String fileName) throws Exception {
    //     return minioClient.getObject(
    //             GetObjectArgs.builder()
    //                     .bucket(audioBucket)
    //                     .object(fileName)
    //                     .build()
    //     ).url();
    // }


    public String getAudioPresignedUrl(String fileName) throws Exception {
        try {
            // String minioUrl = System.getenv("MINIO_URL");

            // Log environment details for debugging
            
            System.out.println("Access Key: " + accessKey);
            System.out.println("Secret Key: " + secretKey);
            System.out.println("Generating Presigned URL for file: " + fileName);

            // Generate presigned URL for the object
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(audioBucket)
                            .object(fileName)
                            .expiry(1, TimeUnit.HOURS)  
                            .build()
            );

            System.out.println("Generated Presigned URL: " + url);

            if (minioUrl != null && minioUrl.contains("minio:9000")) {
                url = url.replace("http://minio:9000", "http://localhost:9000");
            }

            return url;
        } catch (Exception e) {
            System.err.println("Error generating presigned URL: " + e.getMessage());
            throw new RuntimeException("Error generating presigned URL: " + e.getMessage(), e);
        }
    }

    // Delete an audio file from Minio
    public void deleteAudioFile(String fileName) throws Exception {
        try {
            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                            .bucket(audioBucket)
                            .object(fileName)
                            .build()
            );
            System.out.println("Deleted file: " + fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
}
