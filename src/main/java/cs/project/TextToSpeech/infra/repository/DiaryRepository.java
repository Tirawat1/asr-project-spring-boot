package cs.project.TextToSpeech.infra.repository;

import cs.project.TextToSpeech.models.DiaryModel;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiaryRepository extends MongoRepository<DiaryModel, String> {

    List<DiaryModel> findAllByTagIdsContains(String tagId);
    List<DiaryModel> findAllDiariesByUserId(String userId);
}
