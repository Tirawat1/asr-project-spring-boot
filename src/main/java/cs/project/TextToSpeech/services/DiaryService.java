package cs.project.TextToSpeech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.infra.repository.DiaryRepository;
import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.Request.DiaryRequest;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service
public class DiaryService {
    @Autowired
    private final DiaryRepository repository;

    private final RestTemplate restTemplate;

    public DiaryService(DiaryRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public List<DiaryModel> getAllEntries() {
        try{
            return repository.findAll();
        }catch (Exception e){
            throw new RuntimeException("Error getting all entries: " + e.getMessage());
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

//     private List<Map<String, Object>> processContent(List<Map<String, Object>> content) {
//     try {
//         if (content == null || content.isEmpty()) {
//             throw new IllegalArgumentException("Content cannot be null or empty");
//         }

//         for (Map<String, Object> item : content) {
//             if (item.containsKey("insert") && item.get("insert") instanceof Map) {
//                 Map<String, Object> insertMap = (Map<String, Object>) item.get("insert");
//                 if (insertMap.containsKey("custom") && insertMap.get("custom") instanceof Map) {
//                     Map<String, Object> customMap = (Map<String, Object>) insertMap.get("custom");
//                     if (customMap.containsKey("audio")) {
//                         String audioUrl = customMap.get("audio").toString();
//                         if (audioUrl.trim().isEmpty()) {
//                             throw new IllegalArgumentException("Audio URL cannot be empty");
//                         }

//                         String transcribedText = sendAudioToEnhanceService(audioUrl);
//                         item.put("transcription", transcribedText);
//                     }
//                 }
//             }
//         }
//         return content;
//     } catch (IllegalArgumentException e) {
//         throw new RuntimeException("Validation failed: " + e.getMessage());
//     } catch (Exception e) {
//         throw new RuntimeException("Error processing content: " + e.getMessage());
//     }
// }

//     private String sendAudioToEnhanceService(String audioUrl) {
//     try {
//         if (audioUrl == null || audioUrl.trim().isEmpty()) {
//             throw new IllegalArgumentException("Audio URL cannot be null or empty");
//         }

//         Map<String, String> requestBody = new HashMap<>();
//         requestBody.put("audioUrl", audioUrl);

//         return restTemplate.postForObject("http://192.168.1.38:5114/enhance_audio", requestBody, String.class);
//     } catch (IllegalArgumentException e) {
//         throw new RuntimeException("Validation failed: " + e.getMessage());
//     } catch (RestClientException e) {
//         throw new RuntimeException("Failed to communicate with the audio enhancement service: " + e.getMessage());
//     } catch (Exception e) {
//         throw new RuntimeException("Unexpected error during audio processing: " + e.getMessage());
//     }
// }


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
            diary.setContent(processContent(request.getContent()));

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

    public DiaryModel updateEntry(@PathVariable String id, DiaryRequest request) {
        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            if (request.getContent() == null || request.getContent().isEmpty()) {
                throw new IllegalArgumentException("Content cannot be empty");
            }
            DiaryModel diary = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Diary with id " + id + " not found"));
                        diary.setTitle(request.getTitle());
            diary.setContent(request.getContent());

            if (request.getTagIds() == null) {
                diary.setTagIds(new ArrayList<>());
            } else {
                diary.setTagIds(request.getTagIds());
            }
            return repository.save(diary);
        }
        catch(IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
        catch(Exception e ){
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