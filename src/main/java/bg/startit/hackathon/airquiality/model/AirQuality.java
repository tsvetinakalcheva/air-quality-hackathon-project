package bg.startit.hackathon.airquiality.model;


import java.time.OffsetDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Date;
import org.apache.tomcat.jni.Poll;

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

  public enum Pollutant {
    CO(2.1),
    C6H5_CH3(10), //TODO Не мога да намеря средната стойност
    //Dangerous values are above 10 ug/m3.
    PM10(101),
    H2S(10),//TODO Не мога да конвертирам 	0.0014 в базата
    NO(10),//TODO Fix me
    PM2_5(61),
    SO2(81),
    C6H6(10),//TODO Fix me
    NOX(10),//TODO Fix me
    NM3(10),//TODO Fix me
    NO2(81),
    NOX_as_NO2(10),//TODO Fix me
    O3(101);

    private final double max;

    Pollutant(double max) {
      this.max = max;
    }

    public double getMax() {
      return max;
    }
  }

}
