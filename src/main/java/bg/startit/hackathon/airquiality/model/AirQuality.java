package bg.startit.hackathon.airquiality.model;


import java.time.OffsetDateTime;
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
    @Size(min = 2,max = 2)
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
        CO, C6H5_CH3, PM10,
        H2S, NO, PM2_5, SO2, C6H6,
        NOX, NM3, NO2, C6H4_CH3, NH3,
        NOX_as_NO2, O3
    }

}
