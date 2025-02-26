package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.models.DiaryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.infra.repository.DiaryRepository;
import cs.project.TextToSpeech.infra.repository.TagRepository;
import cs.project.TextToSpeech.models.DiaryModel;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.web.client.RestTemplate;


@Service
public class DiaryService {
    @Autowired
    private final DiaryRepository repository;

    @Autowired
    private final TagRepository tagRepository;
    @Autowired
    private TagService tagService;

    

    private final RestTemplate restTemplate;

    public DiaryService(DiaryRepository repository , TagRepository tagRepository , RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.tagRepository = tagRepository;
    }

    public List<DiaryModel> getAllEntries() {
        return repository.findAll();
    }

    public DiaryModel getEntryById(String id) {
        return repository.findById(id).orElse(null);
    }

    private List<Map<String, Object>> processContent(List<Map<String, Object>> content) {
        for (Map<String, Object> item : content) {
            if (item.containsKey("insert") && item.get("insert") instanceof Map) {
                Map<String, Object> insertMap = (Map<String, Object>) item.get("insert");
                if (insertMap.containsKey("custom") && insertMap.get("custom") instanceof Map) {
                    Map<String, Object> customMap = (Map<String, Object>) insertMap.get("custom");
                    if (customMap.containsKey("audio")) {
                        String audioUrl = customMap.get("audio").toString();
                        String transcribedText = sendAudioToEnhanceService(audioUrl);
                        item.put("transcription", transcribedText);
                    }
                }
            }
        }
        return content;
    }

    private String sendAudioToEnhanceService(String audioUrl) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("audioUrl", audioUrl);
        
        try {
            return restTemplate.postForObject("http://192.168.1.38:5114/enhance_audio", requestBody, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "Transcription failed";
        }
    }


    public DiaryModel createEntry(DiaryRequest request) {
        DiaryModel diary = new DiaryModel();
        diary.setTitle(request.getTitle());
        diary.setContent(processContent(request.getContent()));

        if (request.getTagIds() == null) {
            diary.setTags(new ArrayList<>());
        } else {
            diary.setTags(
                    tagService.getTagsByIds(request.getTagIds()));
        }

        return repository.save(diary);
    }

    public DiaryModel updateEntry(@PathVariable String id, DiaryRequest request) {
        DiaryModel diary = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Diary with id " + id + " not found"));
        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());

        if (request.getTagIds() == null) {
            diary.setTags(new ArrayList<>());
        } else {
            diary.setTags(
                    tagService.getTagsByIds(request.getTagIds()));
        }

        return repository.save(diary);
    }

    public void deleteEntry(@PathVariable String id) {
        try {
            repository.deleteById(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Diary with id " + id + " not found");
        }
    }
}