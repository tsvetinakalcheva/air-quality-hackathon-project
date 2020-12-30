package bg.startit.hackathon.airquiality.service;

import bg.startit.hackathon.airquiality.model.AirQuality;
import bg.startit.hackathon.airquiality.model.AirQuality.Pollutant;
import bg.startit.hackathon.airquiality.model.User;
import bg.startit.hackathon.airquiality.model.UserEmailStatus;
import bg.startit.hackathon.airquiality.repository.UserEmailStatusRepository;
import bg.startit.hackathon.airquiality.repository.UserSettingsRepository;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import sendinblue.ApiException;

@Service
public class AirQualityAlertService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AirQualityService.class);

  private final UserSettingsRepository userSettingsRepository;
  private final UserEmailStatusRepository userEmailStatusRepository;
  private final EmailService emailSender;

  public AirQualityAlertService(
      UserSettingsRepository userSettingsRepository,
      UserEmailStatusRepository userEmailStatusRepository,
      EmailService emailSender) {
    this.userSettingsRepository = userSettingsRepository;
    this.userEmailStatusRepository = userEmailStatusRepository;
    this.emailSender = emailSender;
  }

  @EventListener
  public void onAirQualityData(AirQualityDataEvent event) {
    LOGGER.info("Started data analysis");

    event.data.stream()
        .collect(Collectors.groupingBy(AirQuality::getStationName)) // split by station code
        .forEach((station, data) -> {
          Map<AirQuality.Pollutant, AirQuality> pollutants = new HashMap<>();

          data.stream()
              // sort them, so newer entries will override the older ones
              .sorted(Comparator.comparing(AirQuality::getTimestamp))
              .forEachOrdered(e -> pollutants.put(e.getPollutant(), e));

          processStationData(station, pollutants);
        });
  }

  private void processStationData(String station, Map<Pollutant, AirQuality> pollutants) {
    //проверяваме дали има показатели с повишени норми
    boolean ok = pollutants.entrySet().stream()
        .noneMatch(e -> e.getValue().getValue() > e.getKey().getMax());

    try {
      String content = buildEmailContent(station, pollutants, ok);
      String title = ok
          ? String.format("Нормален въздух в %s", station)
          : String.format("Опасен въздух в %s", station);
      userSettingsRepository.findByStationNamesContains(station)
          .forEach(userSettings -> {
            Optional<UserEmailStatus> status = userEmailStatusRepository
                .findByUserAndStation(userSettings.getUser(), station);
            if (ok) {
              if (status.isPresent()) {
                sendEmail(userSettings.getUser(), title, content);
                // mark mail is send
                userEmailStatusRepository.delete(status.get());
              }
            } else {
              if (!status.isPresent()) {
                sendEmail(userSettings.getUser(), title, content);
                // mark mail is send
                userEmailStatusRepository.save(
                    new UserEmailStatus(null, userSettings.getUser(), station,
                        OffsetDateTime.now()));
              }
            }
          });
    } catch (IOException e) {
      LOGGER.error("Failed to build and sent email template", e);
    }
  }

  private void sendEmail(User user, String title, String content) {
    try {
      emailSender.send(user.getEmail(), title, content);
    } catch (ApiException e) {
      LOGGER.error("Failed to send email!", e);
    }
  }

  private String buildEmailContent(
      String station,
      Map<Pollutant, AirQuality> pollutants,
      boolean ok)
      throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile(ok ? "email/ok" : "email/warning");

    Map<String, Object> context = new HashMap<>();
    context.put("station", station);
    context.put("pollutants", pollutants.entrySet().stream()
        .map(e -> {
          AirQuality value = e.getValue();
          Pollutant pollutant = e.getKey();
          return new Entry(value.getValue(), pollutant.getMax(), pollutant.name(), value.getUnit(),
              pollutant.getDescription());
        }).collect(Collectors.toList()));

    return template.apply(context);

  }

  @Data
  private static class Entry {

    double value;
    double max;
    String name;
    String unit;
    String description;
    boolean ok;

    public Entry(double value, double max, String name, String unit, String description) {
      this.value = value;
      this.max = max;
      this.name = name;
      this.unit = unit;
      this.description = description;
      this.ok = value <= max;
    }
  }
}
