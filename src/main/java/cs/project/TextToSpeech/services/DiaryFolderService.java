package cs.project.TextToSpeech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cs.project.TextToSpeech.infra.repository.DiaryFolderRepository;
import cs.project.TextToSpeech.infra.repository.DiaryRepository;
import cs.project.TextToSpeech.models.DiaryFolderModel;
import cs.project.TextToSpeech.models.Request.FolderDiaryRequest;

import java.time.Instant;
import java.util.Optional;

@Service
public class DiaryFolderService {

    @Autowired
    private DiaryFolderRepository folderRepository;

    // Create a new folder (Fixed to match Controller)
    public DiaryFolderModel createFolder(FolderDiaryRequest folder) {
        DiaryFolderModel folderModel = new DiaryFolderModel();
        try{
            if(folder.getFolderName() == null || folder.getFolderName().trim().isEmpty()){
                throw new IllegalArgumentException("FolderName cannot be empty");
            }
            folderModel.setFolderName(folder.getFolderName());
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
        catch (Exception e){
            throw new RuntimeException("Error creating folder: " + e.getMessage());

        }
        folderModel.setCreatedAt(Instant.now());
        folderModel.setFolderName(folder.getFolderName());
        return folderRepository.save(folderModel);
    }

    // Get a folder by ID
    public Optional<DiaryFolderModel> getFolderById(String id) {
        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
            return folderRepository.findById(id);
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());

        }catch (Exception e){
            throw new RuntimeException("Error getting folder: " + e.getMessage());
        }
    }

    // //  Get subfolders of a folder
    // public List<DiaryFolderModel> getSubfolders(String parentFolderId) {
    //     return folderRepository.findByParentFolderId(parentFolderId);
    // }

    // Update a folder
    public Optional<DiaryFolderModel> updateFolder(String id, FolderDiaryRequest updatedData) {
        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
            if(updatedData.getFolderName() == null || updatedData.getFolderName().trim().isEmpty()){
                throw new IllegalArgumentException("FolderName cannot be empty");
            }
            if(updatedData.getDiaryIds() == null || updatedData.getDiaryIds().isEmpty()){
                throw new IllegalArgumentException("DiaryIds cannot be empty");
            }
            Optional<DiaryFolderModel> model = folderRepository.findById(id);
            if(model.isPresent()){
                DiaryFolderModel folder = model.get();
                folder.setFolderName(updatedData.getFolderName());
                folder.setUpdatedAt(Instant.now());
                return Optional.of(folderRepository.save(folder));
            }
            return Optional.empty();
        }catch (IllegalArgumentException e){
            return Optional.empty();
        }
        catch (Exception e){
            throw new RuntimeException("Error updating folder: " + e.getMessage());
        }

        // if (folderOpt.isPresent()) {
        //     DiaryFolderModel folder = folderOpt.get();
        //     if (updatedData.getFolderName() != null) {
        //         folder.setFolderName(updatedData.getFolderName());
        //     }
        //     folder.setUpdatedAt(Instant.now());
        //     return Optional.of(folderRepository.save(folder));
        // }
        // return Optional.empty();
    }

    // Delete a folder
    public boolean deleteFolder(String id) {
        if (folderRepository.existsById(id)) {
            folderRepository.deleteById(id);
            return true;
        }
        return false;
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

    //Find parent folder by ID
    public Optional<DiaryFolderModel> findParentFolderById(String id) {
        return folderRepository.findById(id);
    }

    //  Add a Diary  to a Folder
    public DiaryFolderModel addDiaryToFolder(String folderId, String diaryId) {
        DiaryFolderModel folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        // DiaryModel diary = diaryRepository.findById(diaryId)
        //         .orElseThrow(() -> new RuntimeException("Diary entry not found"));

        // folder.getDiary().add(diary);
        return folderRepository.save(folder);
    }
}
