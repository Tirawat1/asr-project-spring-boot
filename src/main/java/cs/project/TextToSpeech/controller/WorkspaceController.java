package cs.project.TextToSpeech.controller;

import java.util.List;

import cs.project.TextToSpeech.models.DTO.workspace.CreateWorkspaceDTO;
import cs.project.TextToSpeech.models.DTO.workspace.UpdateWorkspaceDTO;
import cs.project.TextToSpeech.models.DTO.workspace.WorkspaceWithMembersDTO;
import cs.project.TextToSpeech.models.DTO.workspaceMember.CreateWorkspaceMemberDTO;
import cs.project.TextToSpeech.services.WorkspaceMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cs.project.TextToSpeech.models.WorkspaceModel;
import cs.project.TextToSpeech.services.WorkspaceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private WorkspaceMemberService workspaceMemberService;

    @GetMapping
    public ResponseEntity<List<WorkspaceModel>> getAllWorkspaces() {
        return ResponseEntity.ok(workspaceService.getAllWorkspaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceModel> getWorkspaceById(@PathVariable String id) {
        return ResponseEntity.ok(workspaceService.getWorkspaceById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WorkspaceWithMembersDTO>> getWorkspacesByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(workspaceService.getUserWorkspacesWithMembers(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<WorkspaceWithMembersDTO> createWorkspace(@PathVariable String userId, @Valid @RequestBody CreateWorkspaceDTO createWorkspaceDTO) {
        return ResponseEntity.ok(workspaceService.createWorkspace(userId, createWorkspaceDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WorkspaceWithMembersDTO> updateWorkspace(@PathVariable String id, @RequestBody UpdateWorkspaceDTO updateWorkspaceDTO) {
        return ResponseEntity.ok(workspaceService.updateWorkspace(id, updateWorkspaceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable String memberId) {
        workspaceMemberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<WorkspaceWithMembersDTO> inviteMembers(@PathVariable String id, @Valid @RequestBody List<CreateWorkspaceMemberDTO> createWorkspaceMemberDTOs) {
        return ResponseEntity.ok(workspaceService.inviteMembers(id, createWorkspaceMemberDTOs));
    }
}
    
