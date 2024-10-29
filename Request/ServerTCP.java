import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerTCP {
    public static void main(String[] args) throws Exception {
        boolean cont = true;

        // Verifying args
        if (args.length != 1) {
            throw new IllegalArgumentException("Parameter: <portnumber>");
        }
        int port = Integer.parseInt(args[0]);   // Receiving Port

        // Create TCP socket
        ServerSocket serverSocket = new ServerSocket(port);

        Scanner s = new Scanner(System.in);  // Scanner for server termination prompt

        // Response ID:
        byte tempResponseID = 1;

        // Accept incoming connection
        try (Socket clientSocket = serverSocket.accept()) {
            // Create input and output streams for communication with the client
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            do {
                // Decode binary-encoded request
                RequestDecoder decoder = new RequestDecoderBin();
                Request receivedRequest = decoder.decode(inputStream);

                // Signal that request is received
                System.out.println("Received Request:");
                System.out.println(receivedRequest);

                // Server calculations (result):
                int result;
                switch (receivedRequest.opCode) {
                    case 0: result = receivedRequest.op1 / receivedRequest.op2; break;
                    case 1: result = receivedRequest.op1 * receivedRequest.op2; break;
                    case 2: result = receivedRequest.op1 & receivedRequest.op2; break;
                    case 3: result = receivedRequest.op1 | receivedRequest.op2; break;
                    case 4: result = receivedRequest.op1 + receivedRequest.op2; break;
                    case 5: result = receivedRequest.op1 - receivedRequest.op2; break;
                    default: throw new IllegalArgumentException("Unknown operand");
                }

                // Determine error code
                byte tempErrorCode = (receivedRequest.tml == (short) (9 + receivedRequest.opNameLength)) ? (byte) 0 : 127;

                // Create response
                Response response = new Response((short) 8, tempResponseID, result, tempErrorCode);

                // Encode response
                ResponseEncoder encoder = new ResponseEncoderBin();
                byte[] codedResponse = encoder.encode(response);

                // Print response object
                for (int i=0; i<codedResponse.length; i++) {
                    System.out.print(String.format("0x%02X", codedResponse[i]) + " ");
                }
                System.out.println();

                // Send response to client
                outputStream.write(codedResponse);
                outputStream.flush();

                tempResponseID++;

            } while (cont);

            }

        serverSocket.close();
        s.close();
    }
}
