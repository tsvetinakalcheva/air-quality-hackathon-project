package bg.startit.hackathon.airquiality.controller;

import bg.startit.hackathon.airquiality.api.UserApi;
import bg.startit.hackathon.airquiality.dto.ChangePasswordRequest;
import bg.startit.hackathon.airquiality.dto.ChangeSettingsRequest;
import bg.startit.hackathon.airquiality.dto.CreateUserRequest;
import bg.startit.hackathon.airquiality.dto.UserResponse;
import bg.startit.hackathon.airquiality.dto.UserSettings;
import bg.startit.hackathon.airquiality.model.User;
import bg.startit.hackathon.airquiality.repository.UserRepository;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Validated
@RestController
@RequestMapping
public class UserController implements UserApi {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Override
  public ResponseEntity<UserResponse> readUser() {
    User current = getCurrentUser();
    return ResponseEntity.ok(toResponse(current));
  }

  @Override
  public ResponseEntity<UserSettings> updateSettings(
      @Valid ChangeSettingsRequest changeSettingsRequest) {
    User user = getCurrentUser();
      user.setCity(changeSettingsRequest.getCity());
      userRepository.save(user);
    return ResponseEntity.ok(toResponseSettings(user));
  }

  private static UserResponse toResponse(User entity) {
    return new UserResponse()
        .username(entity.getName());
  }
  private static UserSettings toResponseSettings(User entity) {
    return new UserSettings()
        .city(entity.getCity());
  }
  //TODO  validate new password
  @Override
  public ResponseEntity<UserResponse> updatePassword(
      @RequestBody ChangePasswordRequest passwordRequest) {
    User toUpdate = getCurrentUser();
    // 1. check current password
    if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), toUpdate.getPassword())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // 2. check password validated
    if (!passwordRequest.getNewPassword().equals(passwordRequest.getNewPasswordAgain())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    // 3. update password
    String encodedPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
    toUpdate.setPasswordHash(encodedPassword.toCharArray());
    userRepository.save(toUpdate);

    return ResponseEntity.ok(toResponse(toUpdate));
  }

  //TODO:  validate username to not contain space in name and repeating characters
  @Override
  public ResponseEntity<Void> createUser(@Valid CreateUserRequest createUserRequest) {
    User user = new User();
    user.setName(createUserRequest.getUsername());
    user.setEmail(createUserRequest.getEmail());
    user.setPasswordHash(passwordEncoder.encode(createUserRequest.getPassword()).toCharArray());

    user = userRepository.save(user);

    // POST (data) -> return Redirect to GET link
    URI redirect = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/me") // link to /api/v1/users/me
        .build()
        .toUri();
    return ResponseEntity.created(redirect).build();
  }

  @Override
  public ResponseEntity<Void> deleteUser() {
    User user = getCurrentUser();
    userRepository.delete(user);
    return ResponseEntity.ok().build();
  }

  //TODO
  @Override
  public ResponseEntity<UserSettings> readSettings() {
    User user = getCurrentUser();
    return ResponseEntity.ok(toResponseSettings(user));
  }

  private User getCurrentUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
