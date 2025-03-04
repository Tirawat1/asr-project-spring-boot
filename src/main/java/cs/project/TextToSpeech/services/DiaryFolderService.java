package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.repository.DiaryRepository;
import cs.project.TextToSpeech.models.DTO.PersonalFolderWithDiariesDTO;
import cs.project.TextToSpeech.models.DTO.WorkspaceFolderWithDiariesDTO;
import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.PersonalDiaryFolderModel;
import cs.project.TextToSpeech.models.Request.DiaryRequest;
import cs.project.TextToSpeech.models.WorkspaceDiaryFolderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import cs.project.TextToSpeech.infra.repository.DiaryFolderRepository;
import cs.project.TextToSpeech.models.DiaryFolderModel;
import cs.project.TextToSpeech.models.Request.DiaryFolderRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiaryFolderService {

    @Autowired
    private DiaryFolderRepository folderRepository;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryFolderRepository diaryFolderRepository;
    @Autowired
    private DiaryService diaryService;

    // Personal Folder
    public DiaryFolderModel createPersonalDiaryFolder(String userId, DiaryFolderRequest diaryFolderRequest) {
        try {
            Objects.requireNonNull(userId, "User ID cannot be null");

            List<String> diaryIds = diaryFolderRequest.getDiaryIds() != null ? diaryFolderRequest.getDiaryIds() : new ArrayList<>();

            PersonalDiaryFolderModel personalDiaryFolderModel = new PersonalDiaryFolderModel();
            personalDiaryFolderModel.setFolderName(diaryFolderRequest.getFolderName());
            personalDiaryFolderModel.setDiaryIds(diaryIds);
            personalDiaryFolderModel.setUserId(userId);
            return folderRepository.save(personalDiaryFolderModel);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<PersonalFolderWithDiariesDTO> getAllPersonalDiaryFoldersWithDiaries(String userId) {
        try {
            Objects.requireNonNull(userId, "User ID cannot be null");

            // Find all workspace diary folders
            List<PersonalDiaryFolderModel> personalFolders = folderRepository.findPersonalFoldersByUserId(userId);

            // Convert each folder to DTO
            return personalFolders.stream().map(folder -> {
                List<DiaryModel> diaries = diaryRepository.findAllById(folder.getDiaryIds());

                return new PersonalFolderWithDiariesDTO(folder, diaries);
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Workspace Folder
    public DiaryFolderModel createWorkspaceDiaryFolder(String workspaceId, DiaryFolderRequest diaryFolderRequest) {
        try {
            Objects.requireNonNull(workspaceId, "Workspace ID cannot be null");

            List<String> diaryIds = diaryFolderRequest.getDiaryIds() != null ? diaryFolderRequest.getDiaryIds() : new ArrayList<>();

            WorkspaceDiaryFolderModel workspaceDiaryFolderModel = new WorkspaceDiaryFolderModel();
            workspaceDiaryFolderModel.setFolderName(diaryFolderRequest.getFolderName());
            workspaceDiaryFolderModel.setWorkspaceId(workspaceId);
            workspaceDiaryFolderModel.setDiaryIds(diaryIds);
            return folderRepository.save(workspaceDiaryFolderModel);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<WorkspaceFolderWithDiariesDTO> getAllWorkspaceDiaryFoldersWithDiaries(String workspaceId) {
        try {
            Objects.requireNonNull(workspaceId, "Workspace ID cannot be null");

            // Find all workspace diary folders
            List<WorkspaceDiaryFolderModel> workspaceFolders = folderRepository.findWorkspaceFoldersByWorkspaceId(workspaceId);

            // Convert each folder to DTO
            return workspaceFolders.stream().map(folder -> {
                List<DiaryModel> diaries = diaryRepository.findAllById(folder.getDiaryIds());

                return new WorkspaceFolderWithDiariesDTO(folder, diaries);
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Get a folder by ID
    public Optional<DiaryFolderModel> getFolderById(String id) {
        try {
            Objects.requireNonNull(id, "Diary folder ID cannot be null");
            return folderRepository.findById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Update a folder
    public DiaryFolderModel updateFolder(String id, DiaryFolderRequest diaryFolderRequest) {
        try {
            Objects.requireNonNull(id, "Diary folder ID cannot be null");

            DiaryFolderModel diaryFolderModel = diaryFolderRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found"));

            diaryFolderModel.setFolderName(diaryFolderRequest.getFolderName());
            diaryFolderModel.setDiaryIds(diaryFolderRequest.getDiaryIds());
            return folderRepository.save(diaryFolderModel);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Delete a folder
    public void deleteFolder(String id) {
        try {
            Objects.requireNonNull(id, "Diary folder ID cannot be null");

            DiaryFolderModel diaryFolderModel = diaryFolderRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found"));

            diaryRepository.deleteAllById(diaryFolderModel.getDiaryIds());

            diaryFolderRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete diary folder: " + id);
        }
    }

    public DiaryModel addDiaryToFolder(String folderId, DiaryRequest diaryRequest) {
        try {
            Objects.requireNonNull(folderId, "Folder ID cannot be null");

            DiaryModel diaryModel = diaryService.createEntry(diaryRequest);

            DiaryFolderModel diaryFolderModel = diaryFolderRepository.findById(folderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found"));

            diaryFolderModel.getDiaryIds().add(diaryModel.getDiaryId());
            folderRepository.save(diaryFolderModel);
            return diaryModel;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete diary folder: " + folderId);
        }
    }


    // Add a subfolder to an existing folder
    public DiaryFolderModel addSubFolder(String parentFolderId, String subFolderId) {
        DiaryFolderModel parentFolder = folderRepository.findById(parentFolderId)
                .orElseThrow(() -> new RuntimeException("Parent folder not found"));

        DiaryFolderModel subFolder = folderRepository.findById(subFolderId)
                .orElseThrow(() -> new RuntimeException("Subfolder not found"));

        // subFolder.setParentFolder(parentFolder);
        // parentFolder.getSubFolders().add(subFolder);

        folderRepository.save(parentFolder);
        return folderRepository.save(subFolder);
    }

    // //  Get subfolders of a folder
    // public List<DiaryFolderModel> getSubfolders(String parentFolderId) {
    //     return folderRepository.findByParentFolderId(parentFolderId);
    // }

    //Find parent folder by ID
    public Optional<DiaryFolderModel> findParentFolderById(String id) {
        return folderRepository.findById(id);
    }

    //  Add a Diary  to a Folder
}
