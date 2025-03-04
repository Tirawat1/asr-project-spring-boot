package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.enums.PermissionUser;
import cs.project.TextToSpeech.infra.repository.UserRepository;
import cs.project.TextToSpeech.infra.repository.WorkSpaceRepository;
import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.models.WorkSpaceModel;
import cs.project.TextToSpeech.models.Request.WorkSpaceRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WorkSpaceService {
    private final WorkSpaceRepository workSpaceRepository;
    private final UserRepository userRepository;

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

    public List<WorkSpaceModel> getWorkspacesByUserId(String userId) {
        try {
            Objects.requireNonNull(userId, "userId cannot be null");

            return Stream.concat(
                            Optional.ofNullable(workSpaceRepository.findAllByOwnerId(userId)).orElseGet(ArrayList::new).stream(),
                            Optional.ofNullable(workSpaceRepository.findAllByMembersContaining(userId)).orElseGet(ArrayList::new).stream()
                    )
                    .distinct()
                    .collect(Collectors.toList());


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
    public WorkSpaceModel createWorkspace(WorkSpaceRequest workspaceRequest) {

        try{
            if(workspaceRequest.getName() == null || workspaceRequest.getName().trim().isEmpty()){
                throw new IllegalArgumentException("Workspace name cannot be empty");
            }
            if(workspaceRequest.getOwnerId() == null || workspaceRequest.getOwnerId().trim().isEmpty()){
                throw new IllegalArgumentException("Owner ID cannot be empty");
            }
            WorkSpaceModel workspace = new WorkSpaceModel();
            workspace.setWorkspaceName(workspaceRequest.getName());
            workspace.setDescription(workspaceRequest.getDescription());
            workspace.setOwnerId(workspaceRequest.getOwnerId());
//            workspace.setFolderDiaryIds(new ArrayList<>());
            // Initialize members with roles
            Map<String, PermissionUser> members = new HashMap<>();
            members.put(workspaceRequest.getOwnerId(), PermissionUser.OWNER); // Set owner as OWNER
            if (workspaceRequest.getMembers() != null) {
                Map<String, PermissionUser> updatedMembers = new HashMap<>();
                for (String memberId : workspaceRequest.getMembers().keySet()) {
                    updatedMembers.put(memberId, workspaceRequest.getMembers().getOrDefault(memberId, PermissionUser.VIEWER));
                }
                workspace.setMembers(updatedMembers);
            }
            workspace.setMembers(members);

            return workSpaceRepository.save(workspace);
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Error creating workspace: " + e.getMessage());
        }
    }

    // Delete a workspace by ID
    public void deleteWorkspace(String id) {
        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
             WorkSpaceModel workspace = getWorkspaceById(id);
            // Remove workspace from each user's list
//            for (String userId : workspace.getMembers().keySet()) {
//                userRepository.findById(userId).ifPresent(user -> {
//                    user.getDiaryFolderIds().remove(id);
//                    userRepository.save(user);
//                });
//            }

            workSpaceRepository.deleteById(id);
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());

        }catch (Exception e){
            throw new RuntimeException("Error deleting workspace: " + e.getMessage());
        }

    }

    // Update workspace
    public WorkSpaceModel updateWorkspace(String id, WorkSpaceRequest workspaceRequest) {

        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
             WorkSpaceModel workspace = getWorkspaceById(id);

        if (workspaceRequest.getName() != null) {
            workspace.setWorkspaceName(workspaceRequest.getName());
        }

        if (workspaceRequest.getDescription() != null) {
            workspace.setDescription(workspaceRequest.getDescription());
        }

        if (workspaceRequest.getOwnerId() != null) {
            workspace.setOwnerId(workspaceRequest.getOwnerId());
        }

        if (workspaceRequest.getMembers() != null) {
            Map<String, PermissionUser> updatedMembers = new HashMap<>();
            for (String memberId : workspaceRequest.getMembers().keySet()) {
                if(memberId.equals(workspace.getOwnerId())) {
                    updatedMembers.put(memberId, PermissionUser.OWNER);
                } else
                {
                    updatedMembers.put(memberId, workspaceRequest.getMembers().getOrDefault(memberId, PermissionUser.VIEWER));
                }
            }
            workspace.setMembers(updatedMembers);
        }

        return workSpaceRepository.save(workspace);
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
       
    }
}
