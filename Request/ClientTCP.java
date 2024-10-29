import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientTCP {

  public static void main(String args[]) throws Exception {
    boolean cont = true; // Used in the loop to continue sending requests
    ArrayList<Long> rttTimes = new ArrayList<>(); // List to store RTT times

    // Verifying args
    if (args.length != 2) {
      throw new IllegalArgumentException("Parameters: <servername> <portnumber>");
    }
    String serverName = args[0];
    int port = Integer.parseInt(args[1]);

    // Initialize user input scanner outside loop
    Scanner s = new Scanner(System.in);
    byte tempRequestID = 0;

    try (Socket socket = new Socket(serverName, port);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream()) {

      do {
        // Asking the user for the opcode
        System.out.println("Opcode: div=0, mul=1, and=2, or=3, add=4, sub=5");
        System.out.print("What is the operation you want to perform? ");
        byte tempOpCode = s.nextByte();

        // Validate opcode within range [0, 5]
        while (tempOpCode < 0 || tempOpCode > 5) {
          System.out.print("Not a valid opcode, try again: ");
          tempOpCode = s.nextByte();
        }

        // Asking the user for the operands
        System.out.print("What is the first operand? ");
        short tempOp1 = s.nextShort();
        System.out.print("What is the second operand? ");
        short tempOp2 = s.nextShort();

        // Determine operation name
        String tempOpName;
        switch (tempOpCode) {
          case 0: tempOpName = "div"; break;
          case 1: tempOpName = "mul"; break;
          case 2: tempOpName = "and"; break;
          case 3: tempOpName = "or"; break;
          case 4: tempOpName = "add"; break;
          default: tempOpName = "sub"; break;
        }

        // TML: Adds size of opName to static size of the rest of Request
        short tempTml = (short) (9 + tempOpName.getBytes(StandardCharsets.UTF_16).length);

        // Creates new request object to be sent
        Request request = new Request(tempTml, tempOpCode, tempOp1, tempOp2, tempRequestID,
                                      (byte) tempOpName.getBytes(StandardCharsets.UTF_16).length, tempOpName);

        // Encode request object
        RequestEncoder encoder = new RequestEncoderBin();
        byte[] codedRequest = encoder.encode(request);

        // Print request object
        for (int i=0; i<codedRequest.length; i++) {
          System.out.print(String.format("0x%02X", codedRequest[i]) + " ");
        }
        System.out.println();

        // Record start time (send time)
        long startTime = System.nanoTime();

        // Send request to the server
        out.write(codedRequest);
        out.flush();

        // Record end time (receive time)
        long endTime = System.nanoTime();

        // Calculate round-trip time (RTT)
        long rtt = endTime - startTime;
        rttTimes.add(rtt);

        // Decode response from InputStream directly
        ResponseDecoder decoder = new ResponseDecoderBin();
        Response receivedResponse = decoder.decode(in);

        // Signal that response is received
        System.out.println("Received Binary-Encoded Response");
        System.out.println(receivedResponse);

        // Ask the user to continue or quit
        System.out.print("Continue sending? (y/n) ");
        s.nextLine(); // Consume newline
        String yesNo = s.nextLine().trim().toLowerCase();
        if (!yesNo.equals("y")) {
          cont = false; // Ends the do-while loop

          // Calculate min, max, and average RTT after loop ends
          long minRTT = rttTimes.stream().min(Long::compare).orElse(0L);
          long maxRTT = rttTimes.stream().max(Long::compare).orElse(0L);
          double avgRTT = rttTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

          // Output RTT statistics
          System.out.println("Minimum RTT (ns): " + minRTT);
          System.out.println("Maximum RTT (ns): " + maxRTT);
          System.out.println("Average RTT (ns): " + avgRTT);
        }

        // Increment request ID, wrapping back to 0 if it overflows
        tempRequestID = (byte) ((tempRequestID + 1) & 0xFF);

      } while (cont);
    }
    finally {
      s.close(); // Close scanner to release resources
    }
  }
}
