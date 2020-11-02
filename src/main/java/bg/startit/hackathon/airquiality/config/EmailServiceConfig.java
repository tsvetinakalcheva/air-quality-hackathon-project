package bg.startit.hackathon.airquiality.config;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Configuration
@ConfigurationProperties("air-quality.email")
public class EmailServiceConfig {

  @NotBlank
  private String apiKey;
  @NotBlank
  @Email
  private String senderEmail;

  private String senderName;

}
