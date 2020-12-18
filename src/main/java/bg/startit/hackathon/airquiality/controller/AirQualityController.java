package bg.startit.hackathon.airquiality.controller;

import bg.startit.hackathon.airquiality.api.AirQualityApi;
import bg.startit.hackathon.airquiality.dto.AirQualityPage;
import bg.startit.hackathon.airquiality.repository.AirQualityRepository;
import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping
public class AirQualityController implements AirQualityApi {

  @Autowired
  private AirQualityRepository airQualityRepository;

  @Override
  public ResponseEntity<List<String>> listCities(@Valid String city, @Valid String country) {
    return ResponseEntity.ok(airQualityRepository.findStations(city));
    //TODO implement country
  }

  @Override
  public ResponseEntity<AirQualityPage> readAirQualityData(@Valid String city,
      @Valid String country, @Valid OffsetDateTime since, @Valid OffsetDateTime until,
      @Min(0) @Valid Integer page, @Min(0) @Max(100) @Valid Integer size) {
    return null;
  }
}
