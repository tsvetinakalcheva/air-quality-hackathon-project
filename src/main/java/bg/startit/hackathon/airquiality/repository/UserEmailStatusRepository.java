package bg.startit.hackathon.airquiality.repository;

import bg.startit.hackathon.airquiality.model.User;
import bg.startit.hackathon.airquiality.model.UserEmailStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEmailStatusRepository extends JpaRepository<UserEmailStatus, Long> {

  Optional<UserEmailStatus> findByUserAndStation(User user, String station);
}
