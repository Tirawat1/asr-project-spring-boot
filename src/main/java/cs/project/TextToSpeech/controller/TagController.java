package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.models.TagModel;
import cs.project.TextToSpeech.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    // Create a new tag
    @PostMapping
    public ResponseEntity<TagModel> createTag(@RequestBody TagModel tag) {
        TagModel savedTag = tagService.createTag(tag);
        return ResponseEntity.ok(savedTag);
    }

    // Get a tag by ID
    @GetMapping("/{id}")
    public ResponseEntity<TagModel> getTagById(@PathVariable String id) {
        TagModel tag = tagService.getTagById(id);
        if (tag != null) {
            return ResponseEntity.ok(tag);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
