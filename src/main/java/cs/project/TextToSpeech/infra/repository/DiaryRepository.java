package cs.project.TextToSpeech.infra.repository;

import cs.project.TextToSpeech.models.DiaryModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiaryRepository extends MongoRepository<DiaryModel, String> {
}
