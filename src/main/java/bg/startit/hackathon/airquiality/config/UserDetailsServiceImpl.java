package bg.startit.hackathon.airquiality.config;

import bg.startit.hackathon.airquiality.model.User;
import bg.startit.hackathon.airquiality.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(s)
        .orElseThrow(() -> new UsernameNotFoundException("not found"));

    return (UserDetails) user;
  }
}
