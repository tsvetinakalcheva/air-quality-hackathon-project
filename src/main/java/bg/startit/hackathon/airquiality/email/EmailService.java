package bg.startit.hackathon.airquiality.email;

import bg.startit.hackathon.airquiality.model.User;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.SmtpApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.ArrayList;
import java.util.List;

public class EmailService
{
  public boolean send(User user) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();

    // Configure API key authorization: api-key
    ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
    apiKey.setApiKey("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");
    SmtpApi apiInstance = new SmtpApi();

    SendSmtpEmail sendSmtpEmail = new SendSmtpEmail(); // SendSmtpEmail | Values to send a transactional email

    List<SendSmtpEmailTo> toList = new ArrayList<>();
    toList.add(new SendSmtpEmailTo().email(user.getEmail()));
    sendSmtpEmail.to(toList);

    sendSmtpEmail.subject("Air Quality notification! \uD83E\uDD84");

    sendSmtpEmail.htmlContent("Dear " + user.getUsername() + ",<p>Please see the latest air quality information");

    SendSmtpEmailSender sender = new SendSmtpEmailSender();
    sender.setEmail("codeacademy@gmail.com");
    sender.name("CodeAcademy");
    sendSmtpEmail.sender(sender);

    System.out.println(sendSmtpEmail);

    try {
      CreateSmtpEmail result = apiInstance.sendTransacEmail(sendSmtpEmail);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SmtpApi#sendTransacEmail " + e.getResponseBody());
    }
    return false;
  }
}
