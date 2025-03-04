package cs.project.TextToSpeech.infra.repository;

import cs.project.TextToSpeech.models.TagModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends MongoRepository<TagModel, String> {
    Optional<TagModel> findByTagName(String tagName);

    @Query("{ 'ownerId': ?0 }")
    List<TagModel> getAllEntriesByOwnerId(String ownerId);
}