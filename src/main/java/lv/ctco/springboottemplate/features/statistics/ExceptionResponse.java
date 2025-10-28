package lv.ctco.springboottemplate.features.statistics;

public class ExceptionResponse {
  private String message;

  public ExceptionResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
