package cs.project.TextToSpeech.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.infra.repository.UserRepository;
import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.models.UserRequest;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by ID
    public UserModel getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    // Create a new user
    public UserModel createUser(UserRequest userRequest) {
        UserModel user = new UserModel();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setWorkspaceIds(userRequest.getWorkspaceIds());
        return userRepository.save(user);
    }

    // Delete a user by ID
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // Update a user by ID
    public UserModel updateUser(String id, UserRequest userRequest) {
        UserModel user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setWorkspaceIds(userRequest.getWorkspaceIds());
        return userRepository.save(user);
    }
}
