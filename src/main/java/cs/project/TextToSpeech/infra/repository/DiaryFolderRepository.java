package cs.project.TextToSpeech.infra.repository;

import cs.project.TextToSpeech.models.DiaryFolderModel;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiaryFolderRepository extends MongoRepository<DiaryFolderModel, String> {
    List<DiaryFolderModel> findByParentFolderId(String parentFolderId);
}
