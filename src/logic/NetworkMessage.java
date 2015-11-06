package logic;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by ahmedatef on 11/6/15.
 */
public class NetworkMessage {
  private short length;
  private MessageType type;
  private Object content;
  private byte[] raw;

  public NetworkMessage(MessageType type, Object content) {
    encode(type, content);
  }

  public NetworkMessage(byte[] raw) {
    this.raw = raw;
    decode(raw);
  }

  public NetworkMessage(DataInputStream dataInputStream) throws IOException {
    read(dataInputStream);
    encode(type, content);
  }

  private void encode(MessageType messageType, Object object) {
    short length = 4;
    byte[] data = null;

    if(messageType.getTypeClass().equals(String.class)) {
      byte[] stringData = String.class.cast(object).getBytes();
      length += stringData.length;
      data = stringData;
    }
    if(messageType.getTypeClass().equals(Signal.class)) {
      length += 2;
      Signal signal = Signal.class.cast(object);
      ByteBuffer byteBuffer = ByteBuffer.allocate(2);
      byteBuffer.putShort(signal.getValue());
      data = byteBuffer.array();
    }

    ByteBuffer byteBuffer = ByteBuffer.allocate(length);
    byteBuffer.putShort(length);
    byteBuffer.putShort(messageType.getValue());
    byteBuffer.put(data);

    raw = byteBuffer.array();
  }

  private void decode(byte[] raw) {
    short length = ByteBuffer.wrap(raw, 0, 2).getShort();
    MessageType messageType = MessageType.getMessageType(ByteBuffer.wrap(raw, 2, 2).getShort());

    if(messageType.getTypeClass().equals(String.class)) {
      String content = new String(raw, 4, length - 4);
      setLocalVariables(length, messageType, content);
    }
    if(messageType.getTypeClass().equals(Signal.class)) {
      Signal signal = Signal.getSignal(ByteBuffer.wrap(raw, 4, 2).getShort());
      setLocalVariables(length, messageType, signal);
    }
  }

  private void read(DataInputStream dataInputStream) throws IOException {
    byte[] msg = new byte[length];
    dataInputStream.readFully(msg);

    short length = dataInputStream.readShort();
    MessageType messageType = MessageType.getMessageType(dataInputStream.readShort());
    byte[] byteContent = new byte[length - 4];
    dataInputStream.readFully(byteContent);

    if(messageType.getTypeClass().equals(String.class)) {
      String content = new String(byteContent);
      setLocalVariables(length, messageType, content);
    }
    if(messageType.getTypeClass().equals(Signal.class)) {
      Signal signal = Signal.getSignal(ByteBuffer.wrap(byteContent).getShort());
      setLocalVariables(length, messageType, signal);
    }
  }

  private void setLocalVariables(short length, MessageType type, Object content) {
    this.length = length;
    this.type = type;
    this.content = content;
  }

  public short getLength() {
    return length;
  }

  public MessageType getType() {
    return type;
  }

  public Object getContent() {
    return content;
  }

  public byte[] getRaw() {
    return raw;
  }

  public boolean send(DataOutputStream dataOutputStream) {
    try {
      dataOutputStream.write(raw, 0, length);
      dataOutputStream.flush();
      return true;
    } catch(IOException e) {
      return false;
    }
  }


}