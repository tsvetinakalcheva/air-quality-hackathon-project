package bg.startit.hackathon.airquiality.service;

import static java.util.stream.Collectors.groupingBy;

import bg.startit.hackathon.airquiality.model.AirQuality;
import bg.startit.hackathon.airquiality.model.AirQuality.Pollutant;
import bg.startit.hackathon.airquiality.repository.AirQualityRepository;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/*1. var station
    2. var polutants = list of Polutant
    3. Polutant
    - value
    - max
    - name
    - unit
    - description
    - ok (value <= max)*/
@Service
public class NotifierService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AirQualityService.class);

  // how often to download and process the files (30 minutes)
//  private static final long DOWNLOAD_PERIOD = 60L/*min*/ * 60L /*s*/ * 1000L /*ms*/;

  private final AirQualityRepository airQualityRepository;

  public NotifierService(
      AirQualityRepository airQualityRepository) {
    this.airQualityRepository = airQualityRepository;
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
    // TODO: analyze pollutants for that station. Here are sample pollutants:
    // {PM10=108.03}
    // {NO2=146.6, H2S=0.0074, CO=1.78, PM10=22.15, NO=248.35, NH3=4.36}
    // {NO2=79.01, SO2=2.42, H2S=8.0E-4, PM10=27.37, O3=18.47, CO=0.7, NO=90.18}
    // {NO2=36.9, CO=1.09, O3=7.97, NO=15.83}
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

    // is it ok, or not
    //   build email template
    //   for each user
    //     is mail already send
    //     or send email

  }

  private String buildWarningTemplate(String station, Map<Pollutant, AirQuality> pollutants)
      throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("email/template");

    Map<String, Object> context = new HashMap<>();
    context.put("station", "Sofia");
    context.put("pollutants", pollutants.entrySet().stream()
        .map(e -> {
          AirQuality value = e.getValue();
          Pollutant pollutant = e.getKey();
          return new Entry(value.getValue(), pollutant.getMax(), pollutant.name(), value.getUnit(),
              pollutant.getDescription());
        }).collect(Collectors.toList()));

    return template.apply(context);

  }

  public static void main(String[] args) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("email/template");

    Map<String, Object> context = new HashMap<>();
    context.put("station", "Sofia");
    context.put("pollutants", Arrays.asList(
        new Entry(10, 20, "SO2", "m3", "Hard"),
        new Entry(20, 30, "NM3", "m3", "Hard"),
        new Entry(7, 5, "PM10", "m3", "Alabala"),
        new Entry(17, 5, "PM2.5", "m3", "gdgfdgfdg")
    ));

    String templateString = template.apply(context);

    System.out.println(templateString);
  }

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

    public double getValue() {
      return value;
    }

    public double getMax() {
      return max;
    }

    public String getName() {
      return name;
    }

    public String getUnit() {
      return unit;
    }

    public String getDescription() {
      return description;
    }

    public boolean isOk() {
      return ok;
    }
  }
}
