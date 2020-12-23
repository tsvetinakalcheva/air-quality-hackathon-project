package bg.startit.hackathon.airquiality.service;

import bg.startit.hackathon.airquiality.model.User;
import bg.startit.hackathon.airquiality.model.UserSettings;
import bg.startit.hackathon.airquiality.repository.UserRepository;
import bg.startit.hackathon.airquiality.repository.UserSettingsRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserSettingsRepository userSettingsRepository;

  public UserService(PasswordEncoder passwordEncoder,
      UserRepository userRepository,
      UserSettingsRepository userSettingsRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.userSettingsRepository = userSettingsRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(s)
        .orElseThrow(() -> new UsernameNotFoundException("not found"));

    return (UserDetails) user;
  }

  public Optional<UserSettings> getSettings(User user) {
    return userSettingsRepository.findByUser(user);
  }

  public UserSettings saveSettings(UserSettings userSettings) {
    return userSettingsRepository.save(userSettings);
  }

  public User updatePassword(User user, String currentPassword, String newPassword) {

    // 1. check current password
    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
      return null;
    }

    // 4. update password
    String encodedPassword = passwordEncoder.encode(newPassword);
    user.setPasswordHash(encodedPassword.toCharArray());
    return userRepository.save(user);

  }

  public User createUser(String name, String email, String password) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(password).toCharArray());
    return userRepository.save(user);
  }

  public void deleteUser(User user) {
    userRepository.delete(user);
  }
}
