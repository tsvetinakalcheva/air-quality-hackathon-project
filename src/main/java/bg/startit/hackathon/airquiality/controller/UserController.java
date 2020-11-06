package bg.startit.hackathon.airquiality.controller;

import bg.startit.hackathon.airquiality.api.UserApi;
import bg.startit.hackathon.airquiality.dto.ChangePasswordRequest;
import bg.startit.hackathon.airquiality.dto.CreateUserRequest;
import bg.startit.hackathon.airquiality.dto.UserResponse;
import bg.startit.hackathon.airquiality.dto.UserSettingsDTO;
import bg.startit.hackathon.airquiality.model.User;
import bg.startit.hackathon.airquiality.repository.UserRepository;
import bg.startit.hackathon.airquiality.validation.ChangePasswordRequestValidator;
import bg.startit.hackathon.airquiality.validation.CreateUserRequestValidator;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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

  // This method adds our custom validators
  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(new ChangePasswordRequestValidator());
    binder.addValidators(new CreateUserRequestValidator());
  }

  @Override
  public ResponseEntity<UserResponse> readUser() {
    User current = getCurrentUser();
    return ResponseEntity.ok(toResponse(current));
  }

  @Override
  public ResponseEntity<UserSettingsDTO> updateSettings(
      @RequestBody UserSettingsDTO changeSettingsRequest) {
    User user = getCurrentUser();
      //TODO:
    return ResponseEntity.ok(toResponseSettings(user));
  }

  private static UserResponse toResponse(User entity) {
    return new UserResponse()
        .username(entity.getName());
  }

  //TODO:
  private static UserSettingsDTO toResponseSettings(User entity) {
    return new UserSettingsDTO();
  }

  @Override
  public ResponseEntity<UserResponse> updatePassword(
      @RequestBody ChangePasswordRequest passwordRequest) {
    User toUpdate = getCurrentUser();
    // 1. check current password
    if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), toUpdate.getPassword())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // 4. update password
    String encodedPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
    toUpdate.setPasswordHash(encodedPassword.toCharArray());
    userRepository.save(toUpdate);

    return ResponseEntity.ok(toResponse(toUpdate));
  }

  //TODO:  validate username to not contain space in name and repeating characters
  @Override
  public ResponseEntity<Void> createUser(CreateUserRequest createUserRequest) {
    User user = new User();
    user.setName(createUserRequest.getUsername());
    user.setEmail(createUserRequest.getEmail());
    user.setPasswordHash(passwordEncoder.encode(createUserRequest.getPassword()).toCharArray());

    userRepository.save(user);
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
  public ResponseEntity<UserSettingsDTO> readSettings() {
    User user = getCurrentUser();
    return ResponseEntity.ok(toResponseSettings(user));
  }

  private User getCurrentUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
