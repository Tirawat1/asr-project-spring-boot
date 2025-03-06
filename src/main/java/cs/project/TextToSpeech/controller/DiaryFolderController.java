package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.models.DTO.PersonalFolderWithDiariesDTO;
import cs.project.TextToSpeech.models.DTO.WorkspaceFolderWithDiariesDTO;
import cs.project.TextToSpeech.models.DiaryFolderModel;
import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.Request.DiaryFolderRequest;
import cs.project.TextToSpeech.models.Request.DiaryRequest;
import cs.project.TextToSpeech.services.DiaryFolderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/folders")
@CrossOrigin(origins = "*") 
public class DiaryFolderController {

    @Autowired
    private DiaryFolderService diaryFolderService;

    // Personal Folder
    @PostMapping("/personal/{userId}")
    public ResponseEntity<DiaryFolderModel> createPersonalDiaryFolder(@PathVariable String userId, @Valid @RequestBody DiaryFolderRequest diaryFolderRequest) {
        return ResponseEntity.ok(diaryFolderService.createPersonalDiaryFolder(userId, diaryFolderRequest));
    }

    @GetMapping("/personal/{userId}")
    public ResponseEntity<List<PersonalFolderWithDiariesDTO>> getAllPersonalDiaryFolders(@PathVariable String userId) {
        return ResponseEntity.ok(diaryFolderService.getAllPersonalDiaryFoldersWithDiaries(userId));
    }

    // Workspace Folder
    @PostMapping("/workspace/{workspaceId}")
    public ResponseEntity<DiaryFolderModel> createWorkspaceDiaryFolder(@PathVariable String workspaceId, @Valid @RequestBody DiaryFolderRequest diaryFolderRequest) {
        return ResponseEntity.ok(diaryFolderService.createWorkspaceDiaryFolder(workspaceId, diaryFolderRequest));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<WorkspaceFolderWithDiariesDTO>> getAllWorkspaceDiaryFolders(@PathVariable String workspaceId) {
        return ResponseEntity.ok(diaryFolderService.getAllWorkspaceDiaryFoldersWithDiaries(workspaceId));
    }

    // Get a folder by ID
    @GetMapping("/{id}")
    public ResponseEntity<DiaryFolderModel> getFolderById(@PathVariable String id) {
        Optional<DiaryFolderModel> folder = diaryFolderService.getFolderById(id);
        return folder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a folder
    @PatchMapping("/{id}")
    public ResponseEntity<DiaryFolderModel> updateFolder(@PathVariable String id, @Valid @RequestBody DiaryFolderRequest diaryFolderRequest) {
        return ResponseEntity.ok(diaryFolderService.updateFolder(id, diaryFolderRequest));
    }

    // Delete a folder
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolder(@PathVariable String id) {
        try {
            diaryFolderService.deleteFolder(id);
            return ResponseEntity.ok("Folder deleted successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }

    // Add a Diary to a Folder
    @PostMapping("/{folderId}/diary")
    public ResponseEntity<DiaryModel> addDiaryToFolder(@PathVariable String folderId, @Valid @RequestBody DiaryRequest diaryRequest) {
        return ResponseEntity.ok(diaryFolderService.addDiaryToFolder(folderId, diaryRequest));
    }


    // // Add a subfolder
    // @PostMapping("/{parentFolderId}/subfolder/{subFolderId}")
    // public ResponseEntity<DiaryFolderModel> addSubFolder(@PathVariable String parentFolderId, @PathVariable String subFolderId) {
    //     DiaryFolderModel subFolder = diaryFolderService.addSubFolder(parentFolderId, subFolderId);
    //     return ResponseEntity.ok(subFolder);
    // }

    // // Get subfolders of a folder
    // @GetMapping("/{id}/subfolders")
    // public ResponseEntity<List<DiaryFolderModel>> getSubfolders(@PathVariable String id) {
    //     List<DiaryFolderModel> subFolders = diaryFolderService.getSubfolders(id);
    //     return ResponseEntity.ok(subFolders);
    // }

    // // Find parent folder by ID
    // @GetMapping("/{id}/parent")
    // public ResponseEntity<DiaryFolderModel> findParentFolderById(@PathVariable String id) {
    //     Optional<DiaryFolderModel> parentFolder = diaryFolderService.findParentFolderById(id);
    //     return parentFolder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    // }
}
