package lv.ctco.springboottemplate.config;

import lv.ctco.springboottemplate.features.statistics.ResponseFormat;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ResponseFormatConverter implements Converter<String, ResponseFormat> {
  @Override
  public ResponseFormat convert(String value) {
    return ResponseFormat.valueOf(value.toUpperCase());
  }
}
