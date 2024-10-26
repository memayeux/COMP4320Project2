import java.io.*;   // for Input/OutputStream
import java.net.*;  // for Socket
import java.nio.charset.StandardCharsets;
import java.util.Scanner;   // for getting client input

public class ClientTCP {

  public static void main(String args[]) throws Exception {

    // INITIALIZING CLIENT //

    if (args.length != 2)  // Test for correct # of args
      throw new IllegalArgumentException("Parameter(s): <Destination> <Port>");

    InetAddress destAddr = InetAddress.getByName(args[0]);  // Destination address
    int destPort = Integer.parseInt(args[1]);               // Destination port

    Socket sock = new Socket(destAddr, destPort);

    Scanner s = new Scanner(System.in);   // Scanner for getting client input
    boolean cont = true;   // used in the do-while loop


    do {

      byte tempRequestID = 0;


      // SENDING REQUEST //

      // Asking the user for the opcode
      System.out.println("Opcode: div=0, mul=1, and=2, or=3, add=4, sub=5");
      System.out.println("What is the operation you want to perform?");
      byte tempOpCode = s.nextByte();
      while (!(tempOpCode >= 0 && tempOpCode < 6)) {
        System.out.println("Not a valid opcode, try again.");
        tempOpCode = s.nextByte();
      }

      // Asking the user for the operands
      System.out.println("What is the first operand?");
      short tempOp1 = s.nextShort();
      System.out.println("What is the second operand?");
      short tempOp2 = s.nextShort();

      // Determines operation name
      String tempOpName;
      switch (tempOpCode) {
        case 0: tempOpName = "div"; break;
        case 1: tempOpName = "mul"; break;
        case 2: tempOpName = "and"; break;
        case 3: tempOpName = "or"; break;
        case 4: tempOpName = "add"; break;
        default: tempOpName = "sub"; break;
      }

      // TML calculation: Adds size of opName to static size of the rest of Request
      short tempTml = (short) (9 + tempOpName.getBytes(StandardCharsets.UTF_16).length);

      // Creates new request object to be sent
      Request request = new Request(tempTml, tempOpCode, tempOp1, tempOp2, tempRequestID,
        (byte) tempOpName.getBytes(StandardCharsets.UTF_16).length, tempOpName);

      // Display Request just to check for correctness
      System.out.println("Display Request: ");
      System.out.println(request);

      // Encodes request object
      RequestEncoder encoder = new RequestEncoderBin();
      byte[] codedRequest = encoder.encode(request);

      // Send request
      System.out.println("Sending Request (Binary)");
      OutputStream out = sock.getOutputStream(); // Get a handle onto Output Stream
      out.write(codedRequest); // Sending


      // RECIEVING RESPONSE //

      // Wait to receive response from server
      

      // Decode response
      /*
      ResponseDecoder decoder = new ResponseDecoderBin();
      Response receivedResponse = decoder.decode(packet);
      */

      // Signal that response is received
      /*
      System.out.println("Received Response:");
      System.out.println(receivedResponse);
      */

      // Asks the user for continue/quit
      System.out.println("Continue sending? (y/n)");
      s.nextLine();  // Consume leftover newline
      String yesNo = s.nextLine();
      if (!yesNo.equals("y")) {
        cont = false;   // This signals the end of the do-while loop
      }

      tempRequestID++;

    } while (cont);

    s.close();
    sock.close();

  }
}
