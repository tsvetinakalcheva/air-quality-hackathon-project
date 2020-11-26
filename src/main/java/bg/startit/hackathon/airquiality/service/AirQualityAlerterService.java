package bg.startit.hackathon.airquiality.service;

import bg.startit.hackathon.airquiality.model.AirQuality;
import bg.startit.hackathon.airquiality.model.AirQuality.Pollutant;
import bg.startit.hackathon.airquiality.repository.AirQualityRepository;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AirQualityAlerterService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AirQualityService.class);
  // how often to download and process the files (30 minutes)
  private static final long DOWNLOAD_PERIOD = 60L/*min*/ * 60L /*s*/ * 1000L /*ms*/;
  private final AirQualityRepository airQualityRepository;

  public AirQualityAlerterService(
      AirQualityRepository airQualityRepository) {
    this.airQualityRepository = airQualityRepository;
  }

  @Scheduled(fixedDelay = DOWNLOAD_PERIOD)
  public void analyzeData() {
    LOGGER.info("Started data analysis");
    final OffsetDateTime when = OffsetDateTime.now(ZoneId.of("UTC"))
        .minus(DOWNLOAD_PERIOD, ChronoUnit.MILLIS);
    airQualityRepository.findByTimestampAfterOrderByTimestamp(when)
        .stream()
        .collect(Collectors.groupingBy(AirQuality::getStationCode)) // split by station code
        .forEach((stationCode, data) -> {
          Map<AirQuality.Pollutant, Double> pollutants = new HashMap<>();
          data.stream()
              // sort them, so newer entries will override the older ones
              .sorted(Comparator.comparing(AirQuality::getTimestamp))
              .forEachOrdered(e -> pollutants.put(e.getPollutant(), e.getValue()));
          processStationData(pollutants);
        });
  }

  private void processStationData(Map<Pollutant, Double> pollutants) {
    // TODO: analyze pollutants for that station. Here are sample pollutants:
    // {PM10=108.03}
    // {NO2=146.6, H2S=0.0074, CO=1.78, PM10=22.15, NO=248.35, NH3=4.36}
    // {NO2=79.01, SO2=2.42, H2S=8.0E-4, PM10=27.37, O3=18.47, CO=0.7, NO=90.18}
    // {NO2=36.9, CO=1.09, O3=7.97, NO=15.83}
    for (Entry<Pollutant, Double> entry : pollutants.entrySet()) {
      switch (entry.getKey()) {
        case SO2:
          if (entry.getValue() < 120) {
            System.out.println("Breathe Valio :D");
          } else {
            System.out.println("BAD Air");
          }
          break;

        case CO:
          if (entry.getValue() < 8) {
            System.out.println("Breathe Valio :D");
          } else {
            System.out.println("BAD Air");
          }
          break;

        case PM2_5:
          if (entry.getValue() < 30) {
            System.out.println("Breathe Valio :D");
          } else {
            System.out.println("BAD Air");
          }
          break;

        case PM10:
          if (entry.getValue() < 50) {
            System.out.println("Breathe Valio :D");
          } else {
            System.out.println("BAD Air");
          }
          break;

        case NH3:
          if (entry.getValue() < 37.5) {
            System.out.println("Breathe Valio :D");
          } else {
            System.out.println("BAD Air");
          }
          break;

        case NO2:
          if (entry.getValue() < 100) {
            System.out.println("Breathe Valio :D");
          } else {
            System.out.println("BAD Air");
          }
          break;

        case O3:
          if (entry.getValue() <= 119) {
            System.out.println("Breathe Valio :D");
          } else {
            System.out.println("BAD Air");
          }
          break;
      }
    }
    // TODO: it is important to know, that not all stations supports the complete set of pollutants
    // this has to be handled gracefully.
    // TODO: we should think of a DB table, where we store (stationCode/unique, alertOn-boolean)
    // so if the data shows there is a problem
    //   if the alert in not on (in db)
    //    - post alert on
    //    - save (stationCode=true) in DB
    // else
    //   if the alert is on (in db)
    //     - post alert off
    //     - save (stationCode=false) in DB
  }
}