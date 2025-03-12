package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.enums.PermissionUser;
import cs.project.TextToSpeech.infra.repository.UserRepository;
import cs.project.TextToSpeech.infra.repository.WorkSpaceRepository;
import cs.project.TextToSpeech.models.Request.DiaryFolderRequest;
import cs.project.TextToSpeech.models.Request.RemovedMemberRequest;
import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.models.WorkSpaceModel;
import cs.project.TextToSpeech.models.DTO.WorkspaceWithUsersDTO;
import cs.project.TextToSpeech.models.Request.WorkSpaceRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkSpaceService {
    private final WorkSpaceRepository workSpaceRepository;
    private final UserRepository userRepository;

    @Autowired
    private DiaryFolderService diaryFolderService;

    @Autowired
    public WorkSpaceService(WorkSpaceRepository workSpaceRepository, UserRepository userRepository) {
        this.workSpaceRepository = workSpaceRepository;
        this.userRepository = userRepository;
    }


    // Get all workspaces
    public List<WorkSpaceModel> getAllWorkspaces() {
        try {
            return workSpaceRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all workspaces: " + e.getMessage());
        }
    }
    // Get all workspaces Data by user email
    public List<WorkspaceWithUsersDTO> getWorkspacesByUserId(String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                throw new IllegalArgumentException("userId is null or empty");
            }

            if (!userRepository.existsById(userId)) {
                throw new IllegalArgumentException("user not found");
            }

            // Get workspaces where the user is a member (owner , editor, viewer)
            List<WorkSpaceModel> workspaces = workSpaceRepository.findAll()
                    .stream()
                    .filter(workspace -> workspace.getMembers() != null)
                    .toList();

            return workspaces.stream().map(workspace -> {
                // Get all members' details (excluding owner)
                List<UserModel> members = workspace.getMembers().keySet().stream() // <String, PermissinUser> (email, permission)
                    .map(memberId -> userRepository.findById(memberId).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

                return new WorkspaceWithUsersDTO(workspace, members);
            }).toList();

        } catch (Exception e) {
            throw new RuntimeException("Error getting all workspaces", e);
        }
    }

    // Get a workspace by ID
    public WorkSpaceModel getWorkspaceById(String id) {
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

    // Create a new workspace
    public WorkspaceWithUsersDTO createWorkspace(String userId, WorkSpaceRequest workspaceRequest) {
    try {
        // Validate workspace name
        if (workspaceRequest.getName() == null || workspaceRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name cannot be empty");
        }

        // Create the workspace
        WorkSpaceModel workspace = new WorkSpaceModel();
        workspace.setName(workspaceRequest.getName());
        if (workspaceRequest.getDescription() != null){
            workspace.setDescription(workspaceRequest.getDescription());
        }

        if (workspaceRequest.getIcon() != null) {
            workspace.setIcon(workspaceRequest.getIcon());
        }

        // Set the owner in the members map
        Map<String, PermissionUser> updatedMembers = new HashMap<>();
        updatedMembers.put(userId, PermissionUser.OWNER); // Set the user as the owner

        if (workspaceRequest.getInvitedMemberEmails() != null && !workspaceRequest.getInvitedMemberEmails().isEmpty()) {
            for (String email : workspaceRequest.getInvitedMemberEmails()) {
                if (userRepository.existsByEmail(email)) {
                    UserModel userModel = userRepository.findByEmail(email).get();
                    updatedMembers.put(userModel.getId(), PermissionUser.VIEWER);
                }
            }
        }

        workspace.setMembers(updatedMembers);
        workspace = workSpaceRepository.save(workspace);

        DiaryFolderRequest diaryFolderRequest = new DiaryFolderRequest();
        diaryFolderRequest.setFolderName("Default");
        diaryFolderRequest.setDiaryIds(new ArrayList<>());

        diaryFolderService.createWorkspaceDiaryFolder(workspace.getId(), diaryFolderRequest);

        return new WorkspaceWithUsersDTO(workspace, userRepository.findAllById(workspace.getMembers().keySet()));

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

            // Delete the workspace
            workSpaceRepository.deleteById(id);
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting workspace: " + e.getMessage());
        }
    }

    public WorkspaceWithUsersDTO updateWorkspace(String id, WorkSpaceRequest workspaceRequest) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }

            WorkSpaceModel workspace = getWorkspaceById(id);

            if (workspaceRequest.getName() != null) {
                if (workspaceRequest.getName().trim().isEmpty()) {
                    throw new IllegalArgumentException("Workspace name cannot be empty");
                } else {
                    workspace.setName(workspaceRequest.getName());
                }
            }

            if (workspaceRequest.getDescription() != null) {
                workspace.setDescription(workspaceRequest.getDescription());
            }

            if (workspaceRequest.getIcon() != null) {
                workspace.setIcon(workspaceRequest.getIcon());
            }

            Map<String, PermissionUser> updatedMembers = workspace.getMembers();
            if (workspaceRequest.getMembers() != null && !workspaceRequest.getMembers().isEmpty()) {
                for (String memberId : workspaceRequest.getMembers().keySet()) {
                    updatedMembers.put(memberId, workspaceRequest.getMembers().get(memberId));
                }
                workspace.setMembers(updatedMembers);
            }

            if (workspaceRequest.getInvitedMemberEmails() != null && !workspaceRequest.getInvitedMemberEmails().isEmpty()) {
                for (String email : workspaceRequest.getInvitedMemberEmails()) {
                    if (userRepository.existsByEmail(email)) {
                        UserModel userModel = userRepository.findByEmail(email).get();
                        updatedMembers.put(userModel.getId(), PermissionUser.VIEWER);
                    }
                }
            }

            workspace = workSpaceRepository.save(workspace);

            return new WorkspaceWithUsersDTO(workspace, userRepository.findAllById(workspace.getMembers().keySet()));


        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

    public void removeMember(String id, RemovedMemberRequest request) {
        try {
            Objects.requireNonNull(id, "ID cannot be empty");
            Objects.requireNonNull(request.getRemovedUserId(), "UserId cannot be empty");

            WorkSpaceModel workspace = getWorkspaceById(id);
            if (workspace.getMembers().containsKey(request.getRemovedUserId())) {
                workspace.getMembers().remove(request.getRemovedUserId());
                workSpaceRepository.save(workspace);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }
}
