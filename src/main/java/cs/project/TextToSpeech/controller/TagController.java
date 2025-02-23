package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.models.TagModel;
import cs.project.TextToSpeech.models.TagRequest;
import cs.project.TextToSpeech.services.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    // Get tags
    @GetMapping
    public List<TagModel> getAllEntries() { return tagService.getAllEntries(); }

    // Get a tag by ID
    @GetMapping("/{id}")
    public ResponseEntity<TagModel> getEntryById(@PathVariable String id) {
        TagModel tag = tagService.getTagById(id);
        if (tag != null) {
            return ResponseEntity.ok(tag);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new tag
    @PostMapping
    public ResponseEntity<TagModel> createEntry(@Valid @RequestBody TagRequest request) {
        TagModel savedTag = tagService.createEntry(request);
        return ResponseEntity.ok(savedTag);
    }

    // update tag
    @PatchMapping("/{id}")
    public ResponseEntity<TagModel> updateEntry(@PathVariable String id, @Valid @RequestBody TagRequest request) {
        TagModel tag = tagService.updateEntry(id, request);
        return ResponseEntity.ok(tag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TagModel> deleteEntry(@PathVariable String id) {
        tagService.deleteEntry(id);
        return ResponseEntity.ok().build();
    }
}
