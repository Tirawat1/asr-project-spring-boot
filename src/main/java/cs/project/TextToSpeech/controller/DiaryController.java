package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.DiaryRequest;
import cs.project.TextToSpeech.services.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @GetMapping
    public List<DiaryModel> getAllEntries() {
        return diaryService.getAllEntries();
    }

    @PostMapping
    public ResponseEntity<DiaryModel> createEntry(@RequestBody DiaryRequest request) {
        DiaryModel savedDiary = diaryService.createEntry(request.getDiary(), request.getTags());
        return ResponseEntity.ok(savedDiary);
    }

    @GetMapping("/{id}")
    public DiaryModel getEntryById(@PathVariable String id) {
        return diaryService.getEntryById(id);
    }
}
