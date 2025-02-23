package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.DiaryRequest;
import cs.project.TextToSpeech.services.DiaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diaries")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @GetMapping
    public List<DiaryModel> getAllEntries() {
        return diaryService.getAllEntries();
    }

    @GetMapping("/{id}")
    public DiaryModel getEntryById(@PathVariable String id) {
        return diaryService.getEntryById(id);
    }

    @PostMapping
    public ResponseEntity<DiaryModel> createEntry(@Valid @RequestBody DiaryRequest request) {
        DiaryModel savedDiary = diaryService.createEntry(request);
        return ResponseEntity.ok(savedDiary);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DiaryModel> updateEntry(@PathVariable String id, @Valid @RequestBody DiaryRequest request) {
        DiaryModel diary = diaryService.updateEntry(id, request);
        return ResponseEntity.ok(diary);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DiaryModel> deleteEntry(@PathVariable String id) {
        diaryService.deleteEntry(id);
        return ResponseEntity.ok().build();
    }
}
