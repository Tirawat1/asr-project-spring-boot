package cs.project.TextToSpeech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.infra.repository.DiaryRepository;
import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.Request.DiaryRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class DiaryService {
    @Autowired
    private final DiaryRepository repository;

    @Autowired
    private final MinioService minioService;


    public DiaryService(DiaryRepository repository, MinioService minioService) {
        this.repository = repository;
        this.minioService = minioService;
    }

    public List<DiaryModel> getAllEntries() {
        try{
            return repository.findAll();
        }catch (Exception e){
            throw new RuntimeException("Error getting all entries: " + e.getMessage());
        }
    }

    public List<DiaryModel> getDiariesByUserId(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("User ID cannot be empty");
            }
            return repository.findAllDiariesByUserId(userId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error getting diaries by user ID: " + e.getMessage());
        }
    }

    public DiaryModel getEntryById(String id) {
        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
                    return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Diary entry not found with ID: " + id));

        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());

        }catch (Exception e){
            throw new RuntimeException("Error getting entry: " + e.getMessage());
        }

    }


    public DiaryModel createEntry(DiaryRequest request) {
        try {
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            if (request.getContent() == null || request.getContent().isEmpty()) {
                throw new IllegalArgumentException("Content cannot be empty");
            }
            if (request.getUserId() == null || request.getUserId().isEmpty()) {
                throw new IllegalArgumentException("User Id cannot be empty");
            }
            DiaryModel diary = new DiaryModel();
            diary.setUserId(request.getUserId());
            diary.setTitle(request.getTitle());
            diary.setContent(request.getContent());

            if (request.getTagIds() == null) {
                diary.setTagIds(new ArrayList<>());
            } else {
                diary.setTagIds(request.getTagIds());
            }

            return repository.save(diary);
        
        }catch(IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
        catch(Exception e ){
            throw new RuntimeException("Error creating entry: " + e.getMessage());
        }
       
    }
    private List<String> extractAudioFilenames(List<Map<String, Object>> content) {
        List<String> audioFiles = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Map<String, Object> entry : content) {
            if (entry.containsKey("insert") && entry.get("insert") instanceof Map) {
                Map<String, Object> insert = (Map<String, Object>) entry.get("insert");
                if (insert.containsKey("custom") && insert.get("custom") instanceof String) {
                    String customJson = (String) insert.get("custom");
                    try {
                        // Parse custom JSON
                        Map<String, Object> customData = objectMapper.readValue(customJson, new TypeReference<Map<String, Object>>() {});

                        if (customData.containsKey("audio") && customData.get("audio") instanceof String) {
                            // Parse audio JSON
                            Map<String, Object> audioData = objectMapper.readValue((String) customData.get("audio"), new TypeReference<Map<String, Object>>() {});
                            Object audioUrlObject = audioData.get("audioUrl");

                            if (audioUrlObject != null) {
                                if (audioUrlObject instanceof String) {
                                    // Single audio file
                                    String audioUrl = (String) audioUrlObject;
                                    if (audioUrl != null && !audioUrl.trim().isEmpty()) {
                                        audioFiles.add(audioUrl);
                                    }
                                } else if (audioUrlObject instanceof List) {
                                    // Multiple audio files (list)
                                    List<String> audioUrls = (List<String>) audioUrlObject;
                                    for (String audioUrl : audioUrls) {
                                        if (audioUrl != null && !audioUrl.trim().isEmpty()) {
                                            audioFiles.add(audioUrl);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    return audioFiles;
}



public DiaryModel updateEntry(@PathVariable String id, @RequestBody DiaryRequest request) {
    try {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (request.getContent() == null || request.getContent().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        // Find existing diary entry
        DiaryModel diary = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Diary with id " + id + " not found"));

        List<String> oldAudioFiles = extractAudioFilenames(diary.getContent());
        List<String> newAudioFiles = extractAudioFilenames(request.getContent());

        System.out.println("Old audio files: " + oldAudioFiles);
        System.out.println("New audio files: " + newAudioFiles);
        // Handle renaming of old and new audio files in minio
        if (!oldAudioFiles.isEmpty() && !newAudioFiles.isEmpty()) {
            for (int i = 0; i < Math.min(oldAudioFiles.size(), newAudioFiles.size()); i++) {
                if (!oldAudioFiles.get(i).equals(newAudioFiles.get(i))) {
                    minioService.renameFile(oldAudioFiles.get(i), newAudioFiles.get(i));
                }
            }
        }

        // Delete old audio files that are not in the new list
        for (String oldAudioFile : oldAudioFiles) {
            if (!newAudioFiles.contains(oldAudioFile)) {
                minioService.deleteAudioFile(oldAudioFile);
            }
        }

        // Update diary entry
        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());
        diary.setTagIds(request.getTagIds() != null ? request.getTagIds() : new ArrayList<>());

        return repository.save(diary);
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Validation failed: " + e.getMessage());
    } catch (Exception e) {
        throw new RuntimeException("Error updating entry: " + e.getMessage());
    }
}

    public void deleteEntry(@PathVariable String id) {
        try {
            repository.deleteById(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Diary with id " + id + " not found");
        }
    }

    public List<DiaryModel> getDiariesByTagId(String tagId) {
        try {
            if (tagId == null || tagId.trim().isEmpty()) {
                throw new IllegalArgumentException("Tag ID cannot be empty");
            }
            return repository.findAllByTagIdsContains(tagId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error getting diaries by tag ID: " + e.getMessage());
        }
    }

    // // delete all diaries of user
    // public void deleteAllDiariesOfUser(String userId){
    //     try{
    //         if (userId == null || userId.trim().isEmpty()) {
    //             throw new IllegalArgumentException("User ID cannot be empty");
    //         }
    //         repository.deleteAllByUserId(userId);
    //     }catch(Exception e){
    //         throw new RuntimeException("Error deleting all diaries of user: " + e.getMessage());
    //     }
    // }

}