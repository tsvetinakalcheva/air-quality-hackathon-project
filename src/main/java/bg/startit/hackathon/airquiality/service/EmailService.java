package bg.startit.hackathon.airquiality.service;

import bg.startit.hackathon.airquiality.config.EmailServiceConfig;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import sendinblue.ApiCallback;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.SmtpApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

@Slf4j
@Service
@Validated
public class EmailService {

  @Autowired
  private EmailServiceConfig config;

  public void send(@Valid @Email String email, String subject, String content) throws ApiException {
    // Configure API key authorization: api-key
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
    apiKey.setApiKey(config.getApiKey());

    // build e-mail data
    SendSmtpEmail sendSmtpEmail = new SendSmtpEmail()
        .addToItem(new SendSmtpEmailTo().email(email))
        .subject(subject)
        .htmlContent(content)
        .sender(new SendSmtpEmailSender()
            .email(config.getSenderEmail())
            .name(config.getSenderName()));

    log.debug("Sending email to {}", email);

    new SmtpApi().sendTransacEmailAsync(sendSmtpEmail, new ApiCallback<CreateSmtpEmail>() {
      @Override
      public void onFailure(ApiException e, int i, Map<String, List<String>> map) {
        log.error("Failed to send e-mail to {}", email, e);
      }

      @Override
      public void onSuccess(CreateSmtpEmail createSmtpEmail, int i, Map<String, List<String>> map) {
        log.info("Successfully send e-mail to {}", email);
      }

      @Override
      public void onUploadProgress(long l, long l1, boolean b) {
        // we don't need that
      }

      @Override
      public void onDownloadProgress(long l, long l1, boolean b) {
        // we don't need that
      }
    });

  }
}
