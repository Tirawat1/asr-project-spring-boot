package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.models.DiaryFolderModel;
import cs.project.TextToSpeech.services.DiaryFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/folders")
@CrossOrigin(origins = "*") 
public class DiaryFolderController {

    @Autowired
    private DiaryFolderService diaryFolderService;

    // Create a new folder
    @PostMapping
    public ResponseEntity<DiaryFolderModel> createFolder(@RequestBody DiaryFolderModel folder) {
        DiaryFolderModel savedFolder = diaryFolderService.createFolder(folder);
        return ResponseEntity.ok(savedFolder);
    }

    // Get a folder by ID
    @GetMapping("/{id}")
    public ResponseEntity<DiaryFolderModel> getFolderById(@PathVariable String id) {
        Optional<DiaryFolderModel> folder = diaryFolderService.getFolderById(id);
        return folder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get subfolders of a folder
    @GetMapping("/{id}/subfolders")
    public ResponseEntity<List<DiaryFolderModel>> getSubfolders(@PathVariable String id) {
        List<DiaryFolderModel> subFolders = diaryFolderService.getSubfolders(id);
        return ResponseEntity.ok(subFolders);
    }

    // Update a folder
    @PatchMapping("/{id}")
    public ResponseEntity<DiaryFolderModel> updateFolder(@PathVariable String id, @RequestBody DiaryFolderModel updatedData) {
        Optional<DiaryFolderModel> updatedFolder = diaryFolderService.updateFolder(id, updatedData);
        return updatedFolder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a folder
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolder(@PathVariable String id) {
        boolean deleted = diaryFolderService.deleteFolder(id);
        return deleted ? ResponseEntity.ok("Folder deleted successfully.") : ResponseEntity.notFound().build();
    }

    // Add a subfolder
    @PostMapping("/{parentFolderId}/subfolder/{subFolderId}")
    public ResponseEntity<DiaryFolderModel> addSubFolder(@PathVariable String parentFolderId, @PathVariable String subFolderId) {
        DiaryFolderModel subFolder = diaryFolderService.addSubFolder(parentFolderId, subFolderId);
        return ResponseEntity.ok(subFolder);
    }

    // Find parent folder by ID
    @GetMapping("/{id}/parent")
    public ResponseEntity<DiaryFolderModel> findParentFolderById(@PathVariable String id) {
        Optional<DiaryFolderModel> parentFolder = diaryFolderService.findParentFolderById(id);
        return parentFolder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a Diary to a Folder
    @PostMapping("/{folderId}/diary/{diaryId}")
    public ResponseEntity<DiaryFolderModel> addDiaryToFolder(@PathVariable String folderId, @PathVariable String diaryId) {
        DiaryFolderModel updatedFolder = diaryFolderService.addDiaryToFolder(folderId, diaryId);
        return ResponseEntity.ok(updatedFolder);
    }
}
