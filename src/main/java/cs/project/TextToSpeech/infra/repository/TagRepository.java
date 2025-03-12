package cs.project.TextToSpeech.infra.repository;

import cs.project.TextToSpeech.models.PersonalTagModel;
import cs.project.TextToSpeech.models.TagModel;
import cs.project.TextToSpeech.models.WorkspaceTagModel;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends MongoRepository<TagModel, String> {
    @Query("{ '_class': 'personalTag', 'userId': ?0 }")
    List<TagModel> getAllPersonalTagsByUserId(String userId);

    @Query("{ '_class': 'workspaceTag', 'workspaceId': ?0 }")
    List<TagModel> getAllWorkspaceTagsByWorkspaceId(String workspaceId);

}