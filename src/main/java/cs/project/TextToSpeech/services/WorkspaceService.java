package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.repository.UserRepository;
import cs.project.TextToSpeech.infra.repository.WorkspaceRepository;
import cs.project.TextToSpeech.models.DTO.workspace.CreateWorkspaceDTO;
import cs.project.TextToSpeech.models.DTO.workspace.UpdateWorkspaceDTO;
import cs.project.TextToSpeech.models.DTO.workspace.WorkspaceWithMembersDTO;
import cs.project.TextToSpeech.models.DTO.workspaceMember.CreateWorkspaceMemberDTO;
import cs.project.TextToSpeech.models.DTO.workspaceMember.WorkspaceMemberWithUserDTO;
import cs.project.TextToSpeech.models.WorkspaceIcon;
import cs.project.TextToSpeech.models.WorkspaceMemberModel;
import cs.project.TextToSpeech.models.Request.DiaryFolderRequest;
import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.models.WorkspaceModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkspaceService {
    private final WorkspaceRepository workSpaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberService workspaceMemberService;
    private final DiaryFolderService diaryFolderService;

    @Autowired
    public WorkspaceService(
            WorkspaceRepository workSpaceRepository,
            UserRepository userRepository,
            WorkspaceMemberService workspaceMemberService,
            DiaryFolderService diaryFolderService) {
        this.workSpaceRepository = workSpaceRepository;
        this.userRepository = userRepository;
        this.workspaceMemberService = workspaceMemberService;
        this.diaryFolderService = diaryFolderService;
    }

    // Get all workspaces
    public List<WorkspaceModel> getAllWorkspaces() {
        try {
            return workSpaceRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all workspaces: " + e.getMessage());
        }
    }

    // Find user workspaces
    public List<WorkspaceModel> getUserWorkspaces(String userId) {
        try {
            // Find user
            UserModel user = userRepository.findById(userId).orElse(null);
            Objects.requireNonNull(user, "User with id" + userId + "not found");

            // Find workspaceMemberModel of the user
            List<WorkspaceMemberModel> memberModels = workspaceMemberService.findByEmail(user.getEmail());

            // Find each workspace
            List<WorkspaceModel> workSpaceModels = new ArrayList<>();
            memberModels.forEach(e -> {
                workSpaceRepository.findById(e.getWorkspaceId()).ifPresent(workSpaceModels::add);
            });

            return workSpaceModels;
        } catch (Exception e) {
            throw new RuntimeException("Error getting all workspaces: " + e.getMessage());
        }
    }

    public List<WorkspaceWithMembersDTO> getUserWorkspacesWithMembers(String userId) {
        try {
            UserModel user = userRepository.findById(userId).orElse(null);
            Objects.requireNonNull(user, "User with id" + userId + "not found");

            List<WorkspaceMemberModel> memberModels = workspaceMemberService.findByEmail(user.getEmail());
            List<WorkspaceWithMembersDTO> workSpaceWithMembersDTOs = new ArrayList<>();
            memberModels.forEach(e -> {
                workSpaceWithMembersDTOs.add(getWorkspaceWithMembers(e.getWorkspaceId()));
            });

            return workSpaceWithMembersDTOs;
        } catch (Exception e) {
            throw new RuntimeException("Error getting all workspaces: " + e.getMessage());
        }
    }

    // Get a workspace by ID
    public WorkspaceModel getWorkspaceById(String id) {
        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
            return workSpaceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Workspace with id " + id + " not found"));
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Error getting workspace: " + e.getMessage());
        }

        
    }

    public WorkspaceWithMembersDTO getWorkspaceWithMembers(String id) {
        try {
            WorkspaceModel workspace = getWorkspaceById(id);
            List<WorkspaceMemberModel> memberModels = workspaceMemberService.findByWorkspaceId(workspace.getId());

            List<WorkspaceMemberWithUserDTO> workspaceMemberWithUserDTOs = new ArrayList<>();
            for (WorkspaceMemberModel memberModel : memberModels) {
                WorkspaceMemberWithUserDTO workspaceMemberWithUserDTO =
                        workspaceMemberService.getWorkspaceMemberWithUserDTOByWorkspaceMemberId(
                            memberModel.getId()
                        );

                workspaceMemberWithUserDTOs.add(workspaceMemberWithUserDTO);
            }

            return new WorkspaceWithMembersDTO(
                    workspace,
                    workspaceMemberWithUserDTOs
            );
        } catch (Exception e) {
            throw new RuntimeException("Error getting workspace: " + e.getMessage());
        }
    }

    // Create a new workspace
    public WorkspaceWithMembersDTO createWorkspace(String userId, CreateWorkspaceDTO createWorkspaceDTO) {
        try {

            UserModel user = userRepository.findById(userId).orElse(null);
            Objects.requireNonNull(user, "User with id" + userId + "not found");

            String name = createWorkspaceDTO.getName();
            String description = createWorkspaceDTO.getDescription();
            WorkspaceIcon icon = createWorkspaceDTO.getIcon();
            String ownerEmail = user.getEmail();
            List<CreateWorkspaceMemberDTO> members = createWorkspaceDTO.getMembers();

            // Create the workspace
            WorkspaceModel workspace = new WorkspaceModel();
            workspace.setName(name);
            workspace.setDescription(description);
            workspace.setIcon(icon);

            workspace = workSpaceRepository.save(workspace);

            WorkspaceMemberModel owner =  workspaceMemberService.addOwner(workspace.getId(), ownerEmail);

            List<WorkspaceMemberModel> memberModels = new ArrayList<>();
            memberModels.add(owner);
            for (CreateWorkspaceMemberDTO member : members) {
                memberModels.add(workspaceMemberService.inviteMember(workspace.getId(), member));
            }

            List<WorkspaceMemberWithUserDTO> workspaceMemberWithUserDTOs = new ArrayList<>();
            for (WorkspaceMemberModel memberModel : memberModels) {
                WorkspaceMemberWithUserDTO workspaceMemberWithUserDTO =
                        workspaceMemberService.getWorkspaceMemberWithUserDTOByWorkspaceMemberId(
                            memberModel.getId()
                        );

                workspaceMemberWithUserDTOs.add(workspaceMemberWithUserDTO);
            }

            DiaryFolderRequest diaryFolderRequest = new DiaryFolderRequest();
            diaryFolderRequest.setFolderName("Default");
            diaryFolderRequest.setDiaryIds(new ArrayList<>());

            diaryFolderService.createWorkspaceDiaryFolder(workspace.getId(), diaryFolderRequest);

            return new WorkspaceWithMembersDTO(
                    workspace,
                    workspaceMemberWithUserDTOs
            );

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error creating workspace: " + e.getMessage());
        }
    }


    // Delete a workspace by ID
    public void deleteWorkspace(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }

            // Delete a workspace diary folder (not finish yet)
            diaryFolderService.deleteWorkspaceDiaryFolder(id);
            // Delete each member
            workspaceMemberService.deleteWorkspaceMemberByWorkspaceId(id);
            // Delete the workspace
            workSpaceRepository.deleteById(id);
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting workspace: " + e.getMessage());
        }
    }

    public WorkspaceWithMembersDTO updateWorkspace(String id, UpdateWorkspaceDTO updateWorkspaceDTO) {
        try {
            WorkspaceModel workSpace = workSpaceRepository.findById(id).orElse(null);
            Objects.requireNonNull(workSpace, "Workspace with id " + id + " not found");
            if (updateWorkspaceDTO.getName() != null) workSpace.setName(updateWorkspaceDTO.getName());
            if (updateWorkspaceDTO.getDescription() != null) workSpace.setDescription(updateWorkspaceDTO.getDescription());
            if (updateWorkspaceDTO.getIcon() != null) workSpace.setIcon(updateWorkspaceDTO.getIcon());
            workSpaceRepository.save(workSpace);

            return getWorkspaceWithMembers(workSpace.getId());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public WorkspaceWithMembersDTO inviteMembers(String id, List<CreateWorkspaceMemberDTO> createWorkspaceMemberDTOs) {
        try {
            for (CreateWorkspaceMemberDTO member : createWorkspaceMemberDTOs) {
                workspaceMemberService.inviteMember(id, member);
            }
            return getWorkspaceWithMembers(id);
        } catch (Exception e) {
            throw new RuntimeException("Error inviting members: " + e.getMessage());
        }
    }
}
