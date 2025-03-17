package cs.project.TextToSpeech.infra.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cs.project.TextToSpeech.models.WorkspaceModel;

@Repository
public interface WorkspaceRepository extends MongoRepository<WorkspaceModel, String> {
}
