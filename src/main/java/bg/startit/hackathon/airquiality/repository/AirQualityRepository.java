package bg.startit.hackathon.airquiality.repository;

import bg.startit.hackathon.airquiality.model.AirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirQualityRepository extends JpaRepository<AirQuality,Long> {
}
