import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;

public class ResponseDecoderBin implements ResponseDecoder, BinConst {

    public Response decode(InputStream wire) throws IOException {
        DataInputStream src = new DataInputStream(wire);

        // Read data from input stream
        short tml = src.readShort();
        byte responseID = src.readByte();
        int result = src.readInt();
        byte errorCode = src.readByte();

        // return new response
        return new Response(tml, responseID, result, errorCode);
    }

    public Response decode(DatagramPacket p) throws IOException {
        ByteArrayInputStream payload =
            new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
        return decode(payload);
    }
}