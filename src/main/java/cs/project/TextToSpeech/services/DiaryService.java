package cs.project.TextToSpeech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.infra.repository.DiaryRepository;
import cs.project.TextToSpeech.models.DiaryModel;

import java.time.Instant;
import java.util.List;

@Service

public class DiaryService {
    @Autowired
    private final DiaryRepository repository;

    public DiaryService(DiaryRepository repository) {
        this.repository = repository;
    }

    public DiaryModel createEntry(DiaryModel entry) {
        entry.setCreatedAt(Instant.now());
        entry.setUpdatedAt(Instant.now());
        return repository.save(entry);
    }

    public List<DiaryModel> getAllEntries() {
        return repository.findAll();
    }

    public DiaryModel getEntryById(String id) {
        return repository.findById(id).orElse(null);
    }
}