package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.models.DiaryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.infra.repository.DiaryRepository;
import cs.project.TextToSpeech.infra.repository.TagRepository;
import cs.project.TextToSpeech.models.DiaryModel;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service

public class DiaryService {
    @Autowired
    private final DiaryRepository repository;

    @Autowired
    private final TagRepository tagRepository;
    @Autowired
    private TagService tagService;

    public DiaryService(DiaryRepository repository , TagRepository tagRepository) {
        this.repository = repository;
        this.tagRepository = tagRepository;
    }

    public List<DiaryModel> getAllEntries() {
        return repository.findAll();
    }

    public DiaryModel getEntryById(String id) {
        return repository.findById(id).orElse(null);
    }


    public DiaryModel createEntry(DiaryRequest request) {
        DiaryModel diary = new DiaryModel();
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