package cs.project.TextToSpeech.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.infra.repository.UserRepository;
import cs.project.TextToSpeech.infra.repository.WorkSpaceRepository;
import cs.project.TextToSpeech.models.WorkSpaceModel;
import cs.project.TextToSpeech.models.WorkSpaceRequest;

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
        return workSpaceRepository.findAll();
    }

    // Get a workspace by ID
    public WorkSpaceModel getWorkspaceById(String id) {
        return workSpaceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Workspace with id " + id + " not found"));
    }

    // Create a new workspace
    public WorkSpaceModel createWorkspace(WorkSpaceRequest workspaceRequest) {
        WorkSpaceModel workspace = new WorkSpaceModel();
        workspace.setName(workspaceRequest.getName());
        workspace.setDescription(workspaceRequest.getDescription());
        workspace.setOwnerId(workspaceRequest.getOwnerId());
        workspace.setMembers(workspaceRequest.getMembers() != null ? workspaceRequest.getMembers() : new HashMap<>());
        workspace.setDiaryList(new ArrayList<>());
        return workSpaceRepository.save(workspace);
    }

    // Delete a workspace by ID
    public void deleteWorkspace(String id) {
        WorkSpaceModel workspace = getWorkspaceById(id);

        // Remove workspace from each user's list
        for (String userId : workspace.getMembers().keySet()) {
            userRepository.findById(userId).ifPresent(user -> {
                user.getWorkspaceIds().remove(id);
                userRepository.save(user);
            });
        }

        workSpaceRepository.deleteById(id);
    }

    // Update workspace
    public WorkSpaceModel updateWorkspace(String id, WorkSpaceRequest workspaceRequest) {
        WorkSpaceModel workspace = getWorkspaceById(id);

        if (workspaceRequest.getName() != null) {
            workspace.setName(workspaceRequest.getName());
        }

        if (workspaceRequest.getDescription() != null) {
            workspace.setDescription(workspaceRequest.getDescription());
        }

        if (workspaceRequest.getOwnerId() != null) {
            workspace.setOwnerId(workspaceRequest.getOwnerId());
        }

        if (workspaceRequest.getMembers() != null) {
            workspace.setMembers(workspaceRequest.getMembers());
        }

        return workSpaceRepository.save(workspace);
    }
}
