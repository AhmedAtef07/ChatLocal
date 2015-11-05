package logic;

public abstract class DataConversion {

  public static String bytesToString(byte[] data) {
    String ret = new String(data);
    ret = ret.trim();
    return ret;
  }

  public static byte[] bytesFromString(String data) {
    return data.getBytes();
  }

}
