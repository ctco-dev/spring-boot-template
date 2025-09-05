package lv.ctco.springboottemplate.features.todo_statistics;

import com.mongodb.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TodoStatisticsFormatEnumConverter
    implements Converter<String, TodoStatisticsFormatEnum> {
  @Override
  public TodoStatisticsFormatEnum convert(@Nullable String source) {
    if (source == null || source.isBlank()) {
      return TodoStatisticsFormatEnum.SUMMARY;
    }
    return TodoStatisticsFormatEnum.fromString(source);
  }
}
