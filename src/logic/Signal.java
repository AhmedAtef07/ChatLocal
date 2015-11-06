package logic;

/**
 * Created by ahmedatef on 11/6/15.
 */
public enum Signal {
  USER_CONNECTED(0),
  USER_DISCONNECTED(1);

  private short value;

  Signal(int value) {
    this.value = (short) value;
  }

  // Assuming Signal value will be always assigned in the same order of addition.
  public static Signal getSignal(short value) {
    return Signal.values()[value];
  }

  public short getValue() {
    return value;
  }
}