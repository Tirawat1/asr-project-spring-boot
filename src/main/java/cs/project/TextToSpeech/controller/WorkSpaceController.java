package cs.project.TextToSpeech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cs.project.TextToSpeech.models.WorkSpaceModel;
import cs.project.TextToSpeech.models.DTO.GetWorkSpaceByUserIdDto;
import cs.project.TextToSpeech.models.Request.WorkSpaceRequest;
import cs.project.TextToSpeech.services.WorkSpaceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/workspace")
@CrossOrigin(origins = "*")
public class WorkSpaceController {
    @Autowired
    private WorkSpaceService workSpaceService;

    @GetMapping
    public ResponseEntity<List<WorkSpaceModel>> getAllWorkspaces() {
        return ResponseEntity.ok(workSpaceService.getAllWorkspaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkSpaceModel> getWorkspaceById(@PathVariable String id) {
        return ResponseEntity.ok(workSpaceService.getWorkspaceById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GetWorkSpaceByUserIdDto>> getWorkspacesByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(workSpaceService.getWorkspacesByUserId(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<WorkSpaceModel> createWorkspace(@PathVariable String userId,@Valid @RequestBody WorkSpaceRequest workspaceRequest) {
        return ResponseEntity.ok(workSpaceService.createWorkspace(userId,workspaceRequest));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<WorkSpaceModel> addMemberToWorkspace(@PathVariable String userId, @RequestBody WorkSpaceRequest workspaceRequest) {
        return ResponseEntity.ok(workSpaceService.updateWorkspaceByUserId(userId, workspaceRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkSpaceModel> updateWorkspace(@PathVariable String id, @RequestBody WorkSpaceRequest workspaceRequest) {
        return ResponseEntity.ok(workSpaceService.updateWorkspace(id, workspaceRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String id) {
        workSpaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }
}
    
