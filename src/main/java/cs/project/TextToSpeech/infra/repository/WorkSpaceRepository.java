package cs.project.TextToSpeech.infra.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cs.project.TextToSpeech.models.WorkSpaceModel;

import java.util.List;

@Repository
public interface WorkSpaceRepository extends MongoRepository<WorkSpaceModel, String> {
    List<WorkSpaceModel> findAllByOwnerId(String ownerId);
    List<WorkSpaceModel> findAllByMembersContaining(String userId);
}
