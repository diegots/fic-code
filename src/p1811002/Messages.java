package p1811002;

public interface Messages {

  void printDoing ();

  class Void implements Messages {

    @Override
    public void printDoing() {}
  }

  class Dot implements Messages {
    @Override
    public void printDoing() {
      System.out.print(".");
    }
  }
}
