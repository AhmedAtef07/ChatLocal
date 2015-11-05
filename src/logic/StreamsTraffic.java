package logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class StreamsTraffic {

  public static void writeMessage(DataOutputStream dout, byte[] msg, int msgLen)
          throws IOException {
    dout.writeInt(msgLen);
    dout.write(msg, 0, msgLen);
    dout.flush();
  }

  public static byte[] readMessage(DataInputStream din) throws IOException {
    int msgLen = din.readInt();
    byte[] msg = new byte[msgLen];
    din.readFully(msg);
    return msg;
  }

}
