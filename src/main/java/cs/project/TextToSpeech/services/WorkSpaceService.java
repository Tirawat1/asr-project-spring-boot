package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.enums.PermissionUser;
import cs.project.TextToSpeech.infra.repository.UserRepository;
import cs.project.TextToSpeech.infra.repository.WorkSpaceRepository;
import cs.project.TextToSpeech.models.Request.DiaryFolderRequest;
import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.models.WorkSpaceModel;
import cs.project.TextToSpeech.models.DTO.GetWorkSpaceByUserIdDto;
import cs.project.TextToSpeech.models.Request.WorkSpaceRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<GetWorkSpaceByUserIdDto> getWorkspacesByUserId(String userId) {
        try {
            // check if user exists
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

            // Get workspaces where the user is a member (owner , editor, viewer)
            List<WorkSpaceModel> workspaces = workSpaceRepository.findAll()
                    .stream()
                    .filter(workspace -> workspace.getMembers() != null && workspace.getMembers().containsKey(userId))
                    .toList();

            return workspaces.stream().map(workspace -> {

                // Get all members' details (excluding owner)
                List<UserModel> members = workspace.getMembers().keySet().stream() // <String, PermissinUser> (email, permission)
                .filter(memberId -> !memberId.equals(userId)) // Exclude a user that request that workspace
                .map(memberId -> userRepository.findById(memberId).orElse(null))
                .filter(Objects::nonNull)
                .toList();

                return new GetWorkSpaceByUserIdDto(workspace, user, members);
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
    public WorkSpaceModel createWorkspace(String userId, WorkSpaceRequest workspaceRequest) {
    try {
        // Validate workspace name
        if (workspaceRequest.getName() == null || workspaceRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name cannot be empty");
        }

        // Find the user by ID
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        // Create the workspace
        WorkSpaceModel workspace = new WorkSpaceModel();
        workspace.setName(workspaceRequest.getName());
        workspace.setDescription(workspaceRequest.getDescription());

        // Set the owner in the members map
        Map<String, PermissionUser> updatedMembers = new HashMap<>();
        updatedMembers.put(user.getEmail(), PermissionUser.OWNER); // Set the user as the owner

        // Add additional members if any
        if (workspaceRequest.getMembers() != null) {
            for (String memberEmail : workspaceRequest.getMembers().keySet()) {
                updatedMembers.put(memberEmail, workspaceRequest.getMembers().getOrDefault(memberEmail, PermissionUser.VIEWER));
            }
        }
        workspace.setMembers(updatedMembers);

        workspace = workSpaceRepository.save(workspace);

        DiaryFolderRequest diaryFolderRequest = new DiaryFolderRequest();
        diaryFolderRequest.setFolderName("Default");
        diaryFolderRequest.setDiaryIds(new ArrayList<>());

        diaryFolderService.createWorkspaceDiaryFolder(workspace.getId(), diaryFolderRequest);

        return workspace; 

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

        // Delete the workspace
        workSpaceRepository.deleteById(id);
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Validation failed: " + e.getMessage());
    } catch (Exception e) {
        throw new RuntimeException("Error deleting workspace: " + e.getMessage());
    }
}


    // Update workspace by userId 
    public WorkSpaceModel updateWorkspaceByUserId(String userId, WorkSpaceRequest workspaceRequest){
        try{
            if(workspaceRequest.getName() == null || workspaceRequest.getName().trim().isEmpty()){
                throw new IllegalArgumentException("Workspace name cannot be empty");
            }

            // Find the user by ID
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

            WorkSpaceModel workspace = workSpaceRepository.findAll()
                    .stream()
                    .filter(ws -> ws.getMembers() != null && ws.getMembers().containsKey(userId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Workspace not found"));

            if (workspaceRequest.getName() != null) {
                workspace.setName(workspaceRequest.getName());
            }

            if (workspaceRequest.getDescription() != null) {
                workspace.setDescription(workspaceRequest.getDescription());
            }

            // Update members
            Map<String, PermissionUser> updatedMembers = new HashMap<>();
            updatedMembers.put(user.getEmail(), PermissionUser.OWNER); // Set the user as the owner

            // Add additional members if any
            if (workspaceRequest.getMembers() != null) {
                for (String memberEmail : workspaceRequest.getMembers().keySet()) {
                    updatedMembers.put(memberEmail, workspaceRequest.getMembers().getOrDefault(memberEmail, PermissionUser.VIEWER));
                }
            }

            workspace.setMembers(updatedMembers);

            return workSpaceRepository.save(workspace);

        }catch(IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }catch(Exception e){
            throw new RuntimeException("Error updating workspace: " + e.getMessage());
        }
    }

    public WorkSpaceModel updateWorkspace(String id, WorkSpaceRequest workspaceRequest) {

        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
             WorkSpaceModel workspace = getWorkspaceById(id);

        if (workspaceRequest.getName() != null) {
            workspace.setName(workspaceRequest.getName());
        }

        if (workspaceRequest.getDescription() != null) {
            workspace.setDescription(workspaceRequest.getDescription());
        }

        if(workspaceRequest.getMembers() != null){
            Map<String, PermissionUser> updatedMembers = new HashMap<>();
            for (String memberEmail : workspaceRequest.getMembers().keySet()) {
                updatedMembers.put(memberEmail, workspaceRequest.getMembers().getOrDefault(memberEmail, PermissionUser.VIEWER));
            }
            workspace.setMembers(updatedMembers);
        }


        return workSpaceRepository.save(workspace);
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }
}
