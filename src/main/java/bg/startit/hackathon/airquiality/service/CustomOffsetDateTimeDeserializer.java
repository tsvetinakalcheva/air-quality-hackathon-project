package bg.startit.hackathon.airquiality.service;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

// Deserializes 2020-11-01 01:38:04+01:00 to OffsetDateTime
class CustomOffsetDateTimeDeserializer extends InstantDeserializer {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .append(DateTimeFormatter.ISO_LOCAL_DATE)
      .appendLiteral(' ')
      .append(DateTimeFormatter.ISO_LOCAL_TIME)
      .parseLenient()
      .appendOffsetId()
      .parseStrict()
      .toFormatter();

  CustomOffsetDateTimeDeserializer() {
    super(InstantDeserializer.OFFSET_DATE_TIME, DATE_TIME_FORMATTER);
  }

}
