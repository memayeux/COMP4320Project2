import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ResponseEncoderBin implements ResponseEncoder, BinConst{
    
    public byte[] encode(Response response) throws Exception {

        // Writing out response
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        out.writeShort(response.tml);
        out.writeByte(response.responseID);
        out.writeInt(response.result);
        out.writeByte(response.errorCode);

        // Returning byteArray of a response object
        out.flush();
        return buf.toByteArray();
    }
}
