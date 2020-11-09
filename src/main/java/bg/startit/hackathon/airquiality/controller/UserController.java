package bg.startit.hackathon.airquiality.controller;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

import bg.startit.hackathon.airquiality.api.UserApi;
import bg.startit.hackathon.airquiality.dto.ChangePasswordRequest;
import bg.startit.hackathon.airquiality.dto.CreateUserRequest;
import bg.startit.hackathon.airquiality.dto.UserResponse;
import bg.startit.hackathon.airquiality.dto.UserSettingsDTO;
import bg.startit.hackathon.airquiality.dto.UserSettingsDTO.EmailNotificationPeriodEnum;
import bg.startit.hackathon.airquiality.model.User;
import bg.startit.hackathon.airquiality.model.UserSettings;
import bg.startit.hackathon.airquiality.model.UserSettings.EmailNotificationPeriod;
import bg.startit.hackathon.airquiality.repository.UserRepository;
import bg.startit.hackathon.airquiality.repository.UserSettingsRepository;
import bg.startit.hackathon.airquiality.validation.ChangePasswordRequestValidator;
import bg.startit.hackathon.airquiality.validation.CreateUserRequestValidator;
import java.net.URI;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Optional;
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
  @Autowired
  private UserSettingsRepository userSettingsRepository;

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

  static DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
      .appendValue(HOUR_OF_DAY, 2)
      .appendLiteral(':')
      .appendValue(MINUTE_OF_HOUR, 2)
      .toFormatter();

  private static final OffsetTime toOffSetTime(String s) {
    if (s == null) {
      return null;
    }
    LocalTime localTime = LocalTime.parse(s, TIME_FORMATTER);
    OffsetTime time = OffsetTime.of(localTime, ZoneOffset.UTC);
    return time;
  }

  @Override
  public ResponseEntity<UserSettingsDTO> updateSettings(
      @RequestBody UserSettingsDTO changeSettingsRequest) {
    User user = getCurrentUser();
    UserSettings userSettings = userSettingsRepository.findByUser(user)
        .orElse(new UserSettings());
    EmailNotificationPeriodEnum enp = changeSettingsRequest.getEmailNotificationPeriod();
    userSettings.setEmailNotificationPeriod(EmailNotificationPeriod.valueOf(enp.name()));
    userSettings.setQuietHoursStart(toOffSetTime(changeSettingsRequest.getQuietHoursStart()));
    userSettings.setQuietHoursEnd(toOffSetTime(changeSettingsRequest.getQuietHoursEnd()));
    userSettings.setStationNames(changeSettingsRequest.getStationNames());
    userSettings.setUser(user);
    userSettings = userSettingsRepository.save(userSettings);

    return ResponseEntity.ok(toResponseSettings(userSettings));
  }

  private static UserResponse toResponse(User entity) {
    return new UserResponse()
        .username(entity.getName());
  }

  private static UserSettingsDTO toResponseSettings(UserSettings entity) {
    EmailNotificationPeriod enp = entity.getEmailNotificationPeriod();
    OffsetTime start = entity.getQuietHoursStart();
    OffsetTime end = entity.getQuietHoursEnd();
    return new UserSettingsDTO()
        .emailNotificationPeriod(EmailNotificationPeriodEnum.valueOf(enp.name()))
        .stationNames(entity.getStationNames())
        .quietHoursStart(start == null ? null : String.valueOf(start))
        .quietHoursEnd(end == null ? null : String.valueOf(end));
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


  @Override
  public ResponseEntity<UserSettingsDTO> readSettings() {
    User user = getCurrentUser();
    UserSettings userSettings = userSettingsRepository.findByUser(user)
        .orElse(new UserSettings());

    return ResponseEntity.ok(toResponseSettings(userSettings));
  }

  private User getCurrentUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
