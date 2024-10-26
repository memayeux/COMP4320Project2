import java.io.*;  // for ByteArrayInputStream
import java.net.*; // for DatagramPacket

public class RequestDecoderBin implements RequestDecoder, BinConst {

  public Request decode(InputStream wire) throws IOException {
    DataInputStream src = new DataInputStream(wire);
    
    short tml = src.readShort();
    byte opCode = src.readByte();
    short op1 = src.readShort();
    short op2 = src.readShort();
    byte requestID = src.readByte();
    byte opNameLength = src.readByte();
    
    // Deal with the operation name
    if (opNameLength == -1) throw new EOFException();
    byte[] stringBuf = new byte[opNameLength];
    src.readFully(stringBuf);
    String opName = new String(stringBuf, DEFAULT_ENCODING);
  
    // return new request
    return new Request(tml, opCode, op1, op2, requestID, opNameLength, opName);
  }

  public Request decode(DatagramPacket p) throws IOException {
    ByteArrayInputStream payload =
      new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
    return decode(payload);
  }
}
