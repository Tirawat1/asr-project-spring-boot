package cs.project.TextToSpeech.infra.repository;

import cs.project.TextToSpeech.models.TagModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TagRepository extends MongoRepository<TagModel, String> {
    Optional<TagModel> findByName(String name);
}