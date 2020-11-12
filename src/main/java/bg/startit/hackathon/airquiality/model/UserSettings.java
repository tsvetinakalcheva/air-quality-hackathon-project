package bg.startit.hackathon.airquiality.model;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userGenerator")
  @SequenceGenerator(name = "userGenerator", initialValue = 100)
  private Long id;

  @OneToOne
  @NotNull
  private User user;

  @NotNull
  private EmailNotificationPeriod emailNotificationPeriod = EmailNotificationPeriod.NEVER;

  @ElementCollection
  private List<String> stationNames;

  private OffsetDateTime lastEmailSend;
  private OffsetTime quietHoursStart;
  private OffsetTime quietHoursEnd;


  public enum EmailNotificationPeriod {
    NEVER, THIRTY_MIN, ONE_HOUR, TWO_HOURS, THREE_HOURS, ONE_TIME_PER_DAY
  }
}


