package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.models.DTO.workspaceMember.CreateWorkspaceMemberDTO;
import cs.project.TextToSpeech.models.DTO.workspaceMember.UpdateWorkspaceMemberDTO;
import cs.project.TextToSpeech.models.WorkspaceMemberModel;
import cs.project.TextToSpeech.services.WorkspaceMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspace_members")
public class WorkspaceMemberController {
    private final WorkspaceMemberService workspaceMemberService;

    public WorkspaceMemberController(WorkspaceMemberService workspaceMemberService) {
        this.workspaceMemberService = workspaceMemberService;
    }

    @PatchMapping("/{id}/update_permission")
    public ResponseEntity<String> updatePermission(@PathVariable String id, @RequestBody UpdateWorkspaceMemberDTO updateWorkspaceMemberDTO) {
        try {
            workspaceMemberService.updatePermission(id, updateWorkspaceMemberDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Update Permission");
    }

    @PatchMapping("/{id}/resend_invite")
    public ResponseEntity<String> resendInvite(@PathVariable String id) {
        try {
            workspaceMemberService.resendInvitation(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Resend Invitation");
    }

    @PatchMapping("/{pendingId}/accept")
    public ResponseEntity<String> acceptMember(@PathVariable String pendingId) {
        try {
            workspaceMemberService.acceptPendingInvitation(pendingId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("accepted");
    }

    @DeleteMapping("/{pendingId}/reject")
    public ResponseEntity<String> rejectMember(@PathVariable String pendingId) {
        try {
            workspaceMemberService.deleteMember(pendingId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("rejected");
    }
}
