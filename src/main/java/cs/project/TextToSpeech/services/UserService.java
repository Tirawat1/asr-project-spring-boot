package cs.project.TextToSpeech.services;

import java.util.*;

import cs.project.TextToSpeech.infra.repository.*;
import cs.project.TextToSpeech.models.Request.DiaryFolderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.models.Request.UserRequest;

import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private DiaryFolderService diaryFolderService;



    public UserService(UserRepository userRepository ) {
        this.userRepository = userRepository;
    }
    // login

    public String login(String email, String password) {
    Optional<UserModel> optionalUser = userRepository.findByEmail(email);
    if (optionalUser.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    UserModel user = optionalUser.get();
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    // Check if the provided password matches the stored hashed password
    if (!encoder.matches(password, user.getPassword())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
    }
    return user.getId();
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
            return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID: " + id));
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

    public UserModel getUserByEmail(String email) {
        try {
            UserModel user = userRepository.findByEmail(email).orElse(null);
            Objects.requireNonNull(user, "User not found");
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Error getting user by email: " + e.getMessage());
        }
    }

//    public UserWithFoldersAndDiaries getUserWithFoldersAndDiaries(String userId) {
//        UserModel user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
////        List<DiaryFolderModel> folders = diaryFolderRepository.findAllById(user.getDiaryFolderIds());
//
//        Map<String, List<DiaryModel>> diariesMap = new HashMap<>();
//        for (DiaryFolderModel folder : folders) {
//            List<DiaryModel> diaries = diaryRepository.findAllById(folder.getDiaryIds());
//            diariesMap.put(folder.getDiaryFolderId(), diaries);
//        }
//
//        return new UserWithFoldersAndDiaries(user, folders, diariesMap);
//    }

    public UserModel createUser(UserRequest userRequest) {
        try {
            // check exist
            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }

            if (userRepository.findByName(userRequest.getName()).isPresent()) {
                throw new IllegalArgumentException("Name already exists");
            }

            // Create user
            UserModel user = new UserModel();
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());

            PasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(userRequest.getPassword()));
            user = userRepository.save(user);


            DiaryFolderRequest diaryFolderRequest = new DiaryFolderRequest();
            diaryFolderRequest.setFolderName("Default");
            diaryFolderRequest.setDiaryIds(new ArrayList<>());

            diaryFolderService.createPersonalDiaryFolder(user.getId(), diaryFolderRequest);
            return user;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error creating user: " + e.getMessage());
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

    // Delete a user by ID
    public void deleteUser(String id) {
        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }

            UserModel user = userRepository.findById(id).orElse(null);
            if (user == null) {
                throw new IllegalArgumentException("User not found with ID: " + id);
            }

            // Delete all diary folders and diaries of user
            diaryFolderService.deletePersonalFolder(id);
            

            userRepository.deleteById(id);

        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

//    public DiaryFolderModel addUserDiaryFolder(String userId, DiaryFolderRequest diaryFolderRequest) {
//        if (userId == null || userId.trim().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID cannot be empty");
//        }
//
//        UserModel user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));
//
//        DiaryFolderModel diaryFolderModel = diaryFolderService.createFolder(diaryFolderRequest);
//
//        user.getDiaryFolderIds().add(diaryFolderModel.getDiaryFolderId());
//        userRepository.save(user);
//
//        return diaryFolderModel;
//    }
//
//    public boolean removeUserDiaryFolder(String userId, String diaryFolderId) {
//        Objects.requireNonNull(userId, "User ID cannot be null");
//        Objects.requireNonNull(diaryFolderId, "Diary folder ID cannot be null");
//
//        UserModel user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found with ID: " + userId));
//
//        boolean removed = user.getDiaryFolderIds().removeIf(folderId -> folderId.equals(diaryFolderId));
//        userRepository.save(user);
//
//        if (!removed) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diary folder not found or does not belong to user: " + diaryFolderId);
//        }
//
//        if (!diaryFolderRepository.existsById(diaryFolderId)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diary folder does not exist with ID: " + diaryFolderId);
//        }
//
//        try {
//            return diaryFolderService.deleteFolder(diaryFolderId);
//        } catch (EmptyResultDataAccessException e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete diary folder: " + diaryFolderId);
//        }
//    }
}
