package bg.startit.hackathon.airquiality.validation;

import static bg.startit.hackathon.airquiality.validation.ChangePasswordRequestValidator.PASSWORD_VALIDATOR;

import bg.startit.hackathon.airquiality.dto.CreateUserRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.passay.PasswordData;
import org.passay.RuleResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CreateUserRequestValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return CreateUserRequest.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors e) {
    CreateUserRequest request = (CreateUserRequest) target;
    if (!Objects.equals(request.getPassword(), request.getPasswordAgain())) {
      e.rejectValue("passwordAgain", "password.mismatch");
    }

    RuleResult result = PASSWORD_VALIDATOR.validate(new PasswordData(request.getPassword()));
    if (!result.isValid()) {
      List<String> messages = PASSWORD_VALIDATOR.getMessages(result);
      String messageTemplate = messages.stream()
          .collect(Collectors.joining(","));
      e.rejectValue("password", messageTemplate);
    }
  }
}
