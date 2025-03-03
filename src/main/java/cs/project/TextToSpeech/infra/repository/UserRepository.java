package cs.project.TextToSpeech.infra.repository;

// import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import cs.project.TextToSpeech.models.UserModel;

public interface UserRepository extends MongoRepository<UserModel, String> {
    // List<UserModel> findByWorkspaceIdsContaining(String workspaceId); 
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByName(String name);
}
