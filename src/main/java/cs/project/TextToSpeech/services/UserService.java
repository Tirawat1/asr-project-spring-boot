package cs.project.TextToSpeech.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.infra.repository.UserRepository;
import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.models.Request.UserRequest;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users
    public List<UserModel> getAllUsers() {
        try{
            return userRepository.findAll();
        }catch (Exception e){
            throw new RuntimeException("Error getting all users: " + e.getMessage());
        }
    }

    // Get a user by ID
    public UserModel getUserById(String id) {
        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
            return userRepository.findById(id).orElse(null);
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

    public UserModel createUser(UserRequest userRequest) {
    try {
        // Validate request
        if (userRequest.getName() == null || userRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (userRequest.getEmail() == null || !userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        // check exist 
        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already exists");
        }
        if(userRepository.findByName(userRequest.getName()).isPresent()){
            throw new IllegalArgumentException("Name already exists");
        }
        // Create user
        UserModel user = new UserModel();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());

        return userRepository.save(user);
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Validation failed: " + e.getMessage());
    } catch (Exception e) {
        throw new RuntimeException("Error creating user: " + e.getMessage());
    }
}


    // Delete a user by ID
    public void deleteUser(String id) {
        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
            userRepository.deleteById(id);
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
        
    }

    public UserModel updateUser(String id, UserRequest userRequest) {
    try {
        // Validate ID
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        // Find user
        UserModel user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }

        // Validate request fields
        if (userRequest.getName() == null || userRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (userRequest.getEmail() == null || !userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Update user
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());

        return userRepository.save(user);
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Validation failed: " + e.getMessage());
    } catch (Exception e) {
        throw new RuntimeException("Error updating user: " + e.getMessage());
    }
}

}
