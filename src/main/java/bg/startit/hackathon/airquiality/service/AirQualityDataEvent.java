package bg.startit.hackathon.airquiality.service;

import bg.startit.hackathon.airquiality.model.AirQuality;
import java.util.List;
import org.springframework.context.ApplicationEvent;

public class AirQualityDataEvent extends ApplicationEvent {

  final List<AirQuality> data;

  public AirQualityDataEvent(List<AirQuality> source) {
    super(source);
    this.data = source;
  }
}
