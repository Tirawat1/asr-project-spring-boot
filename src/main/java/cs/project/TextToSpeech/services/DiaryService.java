package cs.project.TextToSpeech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.infra.repository.DiaryRepository;
import cs.project.TextToSpeech.infra.repository.TagRepository;
import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.TagModel;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service

public class DiaryService {
    @Autowired
    private final DiaryRepository repository;

    @Autowired
    private final TagRepository tagRepository;

    public DiaryService(DiaryRepository repository , TagRepository tagRepository) {
        this.repository = repository;
        this.tagRepository = tagRepository;
    }

    public DiaryModel createEntry(DiaryModel diary, List<TagModel> tags) {
        // check if exist
        for (TagModel tag : tags) {
            Optional<TagModel> existingTagOpt = tagRepository.findByName(tag.getName());
            
            // if tag not exit
            if (existingTagOpt.isEmpty()) {
                tagRepository.save(tag);
            } else {
                tag = existingTagOpt.get();
            }
        }

        diary.setCreatedAt(Instant.now());
        diary.setUpdatedAt(Instant.now());

        diary.setTags(tags);

        return repository.save(diary);
    }

    public List<DiaryModel> getAllEntries() {
        return repository.findAll();
    }

    public DiaryModel getEntryById(String id) {
        return repository.findById(id).orElse(null);
    }
}