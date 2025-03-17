package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.enums.UserPermission;
import cs.project.TextToSpeech.infra.enums.WorkspaceMemberStatus;
import cs.project.TextToSpeech.infra.repository.WorkspaceMemberRepository;
import cs.project.TextToSpeech.models.DTO.workspaceMember.CreateWorkspaceMemberDTO;
import cs.project.TextToSpeech.models.DTO.workspaceMember.UpdateWorkspaceMemberDTO;
import cs.project.TextToSpeech.models.DTO.workspaceMember.WorkspaceMemberWithUserDTO;
import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.models.WorkspaceMemberModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class WorkspaceMemberService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserService userService;
    private final EmailService emailService;

    public WorkspaceMemberService(WorkspaceMemberRepository workspaceMemberRepository, UserService userService, EmailService emailService) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    public UserModel getUserByWorkspaceMemberId(String workspaceMemberId) {
        try {
            WorkspaceMemberModel workspaceMember = this.workspaceMemberRepository.findById(workspaceMemberId).orElse(null);
            Objects.requireNonNull(workspaceMember, "Workspace member not found");
            return userService.getUserByEmail(workspaceMember.getEmail());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WorkspaceMemberWithUserDTO getWorkspaceMemberWithUserDTOByWorkspaceMemberId(String workspaceMemberId) {
        try {
            UserModel user;
            try {
                user = getUserByWorkspaceMemberId(workspaceMemberId);
            } catch (Exception e) {
                user = null;
            }
            WorkspaceMemberModel workspaceMember = this.workspaceMemberRepository.findById(workspaceMemberId).orElse(null);

            return new WorkspaceMemberWithUserDTO(workspaceMember, user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /// Find by email
    public List<WorkspaceMemberModel> findByEmail(String email) {
        return workspaceMemberRepository.findByEmail(email);
    }

    ///  Find by workspace
    public List<WorkspaceMemberModel> findByWorkspaceId(String workspaceId) {
        return workspaceMemberRepository.findByWorkspaceId(workspaceId);
    }

    public WorkspaceMemberModel addOwner(String workspaceId, String email) {
        try {
            WorkspaceMemberModel workspaceMemberModel = new WorkspaceMemberModel();
            workspaceMemberModel.setEmail(email);
            workspaceMemberModel.setWorkspaceId(workspaceId);
            workspaceMemberModel.setPermission(UserPermission.OWNER);
            workspaceMemberModel.setStatus(WorkspaceMemberStatus.ACCEPTED);
            return workspaceMemberRepository.save(workspaceMemberModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WorkspaceMemberModel inviteMember(String workspaceId, CreateWorkspaceMemberDTO createWorkspaceMemberDTO) {
        try {
            WorkspaceMemberModel workspaceMember = new WorkspaceMemberModel();
            workspaceMember.setWorkspaceId(workspaceId);
            workspaceMember.setEmail(createWorkspaceMemberDTO.getEmail());
            workspaceMember.setPermission(createWorkspaceMemberDTO.getPermission());
            workspaceMember.setStatus(createWorkspaceMemberDTO.getStatus());
            workspaceMemberRepository.save(workspaceMember);

            // send email
            emailService.sendHtmlEmail(workspaceMember.getEmail());
            return workspaceMember;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePermission(String id, UpdateWorkspaceMemberDTO updateWorkspaceMemberDTO) {
        try {
            WorkspaceMemberModel workspaceMember = workspaceMemberRepository.findById(id).orElse(null);
            Objects.requireNonNull(workspaceMember, "Workspace member not found");
            if (updateWorkspaceMemberDTO.getPermission() != null) workspaceMember.setPermission(updateWorkspaceMemberDTO.getPermission());
            workspaceMemberRepository.save(workspaceMember);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void acceptPendingInvitation(String id) {
        try {
            // Find the memberModel
            WorkspaceMemberModel invitation = workspaceMemberRepository.findById(id).orElse(null);
            Objects.requireNonNull(invitation, "WorkspaceMemberModel not found");
            // Set the status to ACCEPTED
            invitation.setStatus(WorkspaceMemberStatus.ACCEPTED);
            workspaceMemberRepository.save(invitation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMember(String id) {
        try {
            workspaceMemberRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteWorkspaceMemberByWorkspaceId(String workspaceId) {
        try {
            workspaceMemberRepository.deleteByWorkspaceId(workspaceId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
