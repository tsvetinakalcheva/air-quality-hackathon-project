package bg.startit.hackathon.airquiality.model;


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

    private String country;
    private Polluntant polluntant;
    private String stationName;
    private String dateInserted;
    private double stationAltitude;
    private String valueUnit;

    public enum Polluntant {
        CO, C6H5CH3, PM10,
        H2S, NO, PM25, SO2, C6H6,
        NOX, NM3, NO2, C6H4CH3
    }


}
