package cs.project.TextToSpeech.services;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import io.minio.messages.Item;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final String imageBucket = "image-bucket";

    @Autowired
    private AsrService asrService;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    public MinioService(@Value("${minio.url}") String minioUrl,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey) {
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
    private boolean fileExists(String bucket, String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
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
        String name = file.getOriginalFilename();
        return uploadFile(audioBucket, file, name);
    }

    // Upload an image file to Minio
    public String uploadImageFile(MultipartFile file, String name) throws Exception {
        return uploadFile(imageBucket, file, name);
    }

    private String uploadFile(String bucket, MultipartFile file, String name) throws Exception {
        String fileExtension = "";

        // Extract file extension if present
        String originalFileName = file.getOriginalFilename();
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex != -1) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        // Use 'name' as the filename
        String newFileName = name + fileExtension;

        try (InputStream inputStream = file.getInputStream()) {
            // If file exists, delete it before uploading the new one
            if (fileExists(bucket, newFileName)) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucket)
                                .object(newFileName)
                                .build()
                );
            }

            // Upload the new file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(newFileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
        return newFileName;
    }


    public String renameFile(String oldFileName, String newFileName) throws Exception {
        if (!fileExists(audioBucket, oldFileName)) {
            throw new IllegalArgumentException("File does not exist: " + oldFileName);
        }
        if (fileExists(audioBucket, newFileName)) {
            throw new IllegalArgumentException("File already exists: " + newFileName);
        }

        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(audioBucket)
                            .object(newFileName)
                            .source(
                                    CopySource.builder()
                                            .bucket(audioBucket)
                                            .object(oldFileName)
                                            .build()
                            )
                            .build()
            );

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(audioBucket)
                            .object(oldFileName)
                            .build()
            );

            return "File renamed successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error renaming file in MinIO: " + e.getMessage(), e);
        }
    }

    public String getUserProfilePresignedObjectUrl(String fileName) throws Exception {
        return getPresignedObjectUrl(imageBucket, fileName, 7,TimeUnit.DAYS);
    }

    public String getAudioPresignedObjectUrl(String fileName) throws Exception {
        return getPresignedObjectUrl(audioBucket, fileName, 1, TimeUnit.HOURS);
    }

    public String getPresignedObjectUrl(String bucket, String fileName, int duration, TimeUnit timeUnit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(fileName)
                            .expiry(duration, timeUnit)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL: " + e.getMessage(), e);
        }
    }

    public String processTranscribe(String fileName) throws Exception {
        try {
            String url = getAudioPresignedObjectUrl(fileName);
            return asrService.sendAudioToEnhanceService(url);
        } catch (Exception e) {
            throw new RuntimeException("Error generating Transcript URL: " + e.getMessage(), e);
        }
    }

    public void deleteAudioFile(String fileName) throws Exception {
        deleteFile(audioBucket, fileName);
    }

    public void deleteImageFile(String fileName) throws Exception {
        deleteFile(imageBucket, fileName);
    }

    private void deleteFile(String bucket, String fileName) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
}
