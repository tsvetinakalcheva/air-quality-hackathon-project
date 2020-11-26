package bg.startit.hackathon.airquiality.model;


import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AirQuality {

  @Id
  //Auto-generate ID
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "airGenerator")
  @SequenceGenerator(name = "airGenerator", initialValue = 100)

  private Long id;

  // where
  @NotBlank
  @Size(min = 2, max = 2)
  private String country; // network_countrycode
  @NotBlank
  private String stationName; // station_name
  @NotBlank
  private String stationCode; // station_code

  // what
  @NotNull
  @Enumerated(EnumType.STRING)
  private Pollutant pollutant; // pollutant
  @NotBlank
  private String unit; // value_unit
  private double value; // value_numeric

  // when
  @NotNull
  private OffsetDateTime timestamp; // value_datetime_inserted = 2020-11-02 07:34:10+01:00

  public void getValueAndPollutant(double value_numeric,
      Pollutant pollutant) {
    Pollutant co = pollutant.CO;
    Pollutant o3 = pollutant.O3;
    Pollutant pm2_5 = pollutant.PM2_5;
    Pollutant pm10 = pollutant.PM10;
    Pollutant no2 = pollutant.NO2;
    Pollutant so2 = pollutant.SO2;
    Pollutant nh3 = pollutant.NH3;
//
    Map<Pollutant, Double> valueAndPollutant = new HashMap<>();

    valueAndPollutant.put(co, 0.);
    valueAndPollutant.put(o3, 0.);
    valueAndPollutant.put(pm2_5, 0.);
    valueAndPollutant.put(pm10, 0.);
    valueAndPollutant.put(no2, 0.);
    valueAndPollutant.put(so2, 0.);
    valueAndPollutant.put(nh3, 0.);

    if (valueAndPollutant.containsKey(pollutant.CO.name())){

    }
  }

  public enum Pollutant {
    O3, PM2_5, PM10,
    NO2, SO2, CO, NH3
  }

}
