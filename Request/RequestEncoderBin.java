// for ByteArrayOutputStream and DataOutputStream
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestEncoderBin implements RequestEncoder, BinConst {

  public byte[] encode(Request request) throws Exception {

    // Writing out most parts of request
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(buf);
    out.writeShort(request.tml);
    out.writeByte(request.opCode);
    out.writeShort(request.op1);
    out.writeShort(request.op2);
    out.writeByte(request.requestID);
    out.writeByte(request.opNameLength);

    // Writing out request string (opName)
    byte[] encodedOpName = request.opName.getBytes(DEFAULT_ENCODING);
    if (encodedOpName.length > MAX_OPNAME_LENGTH)
      throw new IOException("Opcode name exceeds encoded length limit");
    out.write(encodedOpName);
    out.flush();

    // Returns a byteArray of a request object
    return buf.toByteArray();
  }
}
