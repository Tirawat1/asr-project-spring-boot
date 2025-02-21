package cs.project.TextToSpeech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cs.project.TextToSpeech.infra.repository.DiaryFolderRepository;
import cs.project.TextToSpeech.infra.repository.DiaryRepository;
import cs.project.TextToSpeech.models.DiaryFolderModel;
import cs.project.TextToSpeech.models.DiaryModel;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DiaryFolderService {

    @Autowired
    private DiaryFolderRepository folderRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    // Create a new folder (Fixed to match Controller)
    public DiaryFolderModel createFolder(DiaryFolderModel folder) {
        folder.setCreatedAt(Instant.now());
        folder.setUpdatedAt(Instant.now());
        return folderRepository.save(folder);
    }

    // Get a folder by ID
    public Optional<DiaryFolderModel> getFolderById(String id) {
        return folderRepository.findById(id);
    }

    //  Get subfolders of a folder
    public List<DiaryFolderModel> getSubfolders(String parentFolderId) {
        return folderRepository.findByParentFolderId(parentFolderId);
    }

    // Update a folder
    public Optional<DiaryFolderModel> updateFolder(String id, DiaryFolderModel updatedData) {
        Optional<DiaryFolderModel> folderOpt = folderRepository.findById(id);
        if (folderOpt.isPresent()) {
            DiaryFolderModel folder = folderOpt.get();
            if (updatedData.getFolderName() != null) {
                folder.setFolderName(updatedData.getFolderName());
            }
            folder.setUpdatedAt(Instant.now());
            return Optional.of(folderRepository.save(folder));
        }
        return Optional.empty();
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

        subFolder.setParentFolder(parentFolder);
        parentFolder.getSubFolders().add(subFolder);

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

        DiaryModel diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary entry not found"));

        folder.getDiary().add(diary);
        return folderRepository.save(folder);
    }
}
