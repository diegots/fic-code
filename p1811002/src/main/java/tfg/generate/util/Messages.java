package tfg.generate.util;

public interface Messages {

  void printDoing ();
  void printMessage (String message);
  void printMessage (int message);
  void printMessageln (String message);
  void printMessageln (int message);
  void printErrln (String message);

  class Void implements Messages {

    @Override
    public void printDoing() {}

    @Override
    public void printMessage(String message) {}

    @Override
    public void printMessage(int message) {

    }

    @Override
    public void printMessageln(String message) {}

    @Override
    public void printMessageln(int message) {

    }

    @Override
    public void printErrln(String message) {}
  }

  class Symbol implements Messages {

    private final String symbol;

    public Symbol(String symbol) {
      this.symbol = symbol;
    }

    @Override
    public void printDoing() {
      System.out.print(symbol);
    }

    @Override
    public void printMessage(String message) {
      System.out.print(message);
    }

    @Override
    public void printMessage(int message) {
      System.out.print(message);
    }

    @Override
    public void printMessageln(String message) {
      System.out.println(message);
    }

    @Override
    public void printMessageln(int message) {
      System.out.println(message);
    }

    @Override
    public void printErrln(String message) {
      System.err.println(message);
    }
  }
}
