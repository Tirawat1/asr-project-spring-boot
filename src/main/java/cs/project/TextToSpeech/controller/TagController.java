package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.models.TagModel;
import cs.project.TextToSpeech.models.Request.TagRequest;
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

    // Get tags by userId
    @GetMapping("/personal/{userId}")
    public ResponseEntity<List<TagModel>> getAllPersonalTags(@PathVariable String userId) {
        return ResponseEntity.ok(tagService.getAllPersonalTagByUserId(userId));
    }

    // Get tags by workspaceId
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<TagModel>> getAllWorkspaceTags(@PathVariable String workspaceId) {
        return ResponseEntity.ok(tagService.getAllWorkspaceTagsByWorkspaceId(workspaceId));
    }

    // create personal tag
    @PostMapping("/personal/{userId}")
    public ResponseEntity<TagModel> addPersonalTag(@PathVariable String userId, @RequestBody @Valid TagRequest tagRequest) {
        return ResponseEntity.ok(tagService.createPersonalTag(userId, tagRequest));
    }

    // create workspace tag
    @PostMapping("/workspace/{workspaceId}")
    public ResponseEntity<TagModel> addWorkspaceTag(@PathVariable String workspaceId, @RequestBody @Valid TagRequest tagRequest) {
        return ResponseEntity.ok(tagService.createWorkspaceTag(workspaceId, tagRequest));
    }

    // update tag
    @PatchMapping("/{id}")
    public ResponseEntity<TagModel> updateEntry(@PathVariable String id, @Valid @RequestBody TagRequest request) {
        return ResponseEntity.ok(tagService.updateTag(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TagModel> deleteEntry(@PathVariable String id) {
        tagService.deleteEntry(id);
        return ResponseEntity.ok().build();
    }
}
