package cs.project.TextToSpeech.controller;

import java.util.List;

import cs.project.TextToSpeech.models.DTO.user.UpdateUserDTO;
import cs.project.TextToSpeech.models.DTO.user.UserWithImageUrl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.services.UserService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserModel> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserWithImageUrl> updateUser(
            @PathVariable String id,
            @Valid @RequestParam(value = "name", required = false) String name,
            @Valid @RequestParam(value = "profileImgPath", required = false) MultipartFile profileImgPath
    ) {
        try {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(name, profileImgPath);
            return ResponseEntity.ok(userService.putUser(id, updateUserDTO));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
