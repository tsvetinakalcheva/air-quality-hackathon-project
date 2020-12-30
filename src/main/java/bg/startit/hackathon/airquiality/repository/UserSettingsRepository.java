package bg.startit.hackathon.airquiality.repository;

import bg.startit.hackathon.airquiality.model.User;
import bg.startit.hackathon.airquiality.model.UserSettings;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings,Long> {

  Optional<UserSettings> findByUser(User user);

  List<UserSettings> findByStationNamesContains(String stationName);
}
