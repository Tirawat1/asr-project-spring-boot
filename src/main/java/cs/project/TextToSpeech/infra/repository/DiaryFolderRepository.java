package cs.project.TextToSpeech.infra.repository;

import cs.project.TextToSpeech.models.DiaryFolderModel;

// import java.util.List;

import cs.project.TextToSpeech.models.PersonalDiaryFolderModel;
import cs.project.TextToSpeech.models.WorkspaceDiaryFolderModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DiaryFolderRepository extends MongoRepository<DiaryFolderModel, String> {
    // List<DiaryFolderModel> findByParentFolderId(String parentFolderId);
    @Query("{ 'userId': ?0, '_class': 'personalDiaryFolder' }")
    List<DiaryFolderModel> findPersonalFoldersByUserId(String userId);

    @Query("{ 'workspaceId': ?0, '_class': 'workspaceDiaryFolder' }")
    List<DiaryFolderModel> findWorkspaceFoldersByWorkspaceId(String workspaceId);
}
