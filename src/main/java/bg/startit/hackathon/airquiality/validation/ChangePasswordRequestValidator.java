package bg.startit.hackathon.airquiality.validation;

import bg.startit.hackathon.airquiality.dto.ChangePasswordRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ChangePasswordRequestValidator implements Validator {

  static final PasswordValidator PASSWORD_VALIDATOR = new PasswordValidator(Arrays.asList(
      new LengthRule(8, 30),
      new CharacterRule(EnglishCharacterData.UpperCase, 1),
      new CharacterRule(EnglishCharacterData.LowerCase, 1),
      new CharacterRule(EnglishCharacterData.Digit, 1),
      new CharacterRule(EnglishCharacterData.Special, 1),
      new WhitespaceRule()));

  @Override
  public boolean supports(Class<?> clazz) {
    return ChangePasswordRequest.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors e) {
    ChangePasswordRequest request = (ChangePasswordRequest) target;
    if (!Objects.equals(request.getNewPassword(), request.getNewPasswordAgain())) {
      e.rejectValue("newPasswordAgain", "password.mismatch");
    }

    RuleResult result = PASSWORD_VALIDATOR.validate(new PasswordData(request.getNewPassword()));
    if (!result.isValid()) {
      List<String> messages = PASSWORD_VALIDATOR.getMessages(result);
      String messageTemplate = messages.stream()
          .collect(Collectors.joining(","));
      e.rejectValue("newPassword", messageTemplate);
    }
  }
}
