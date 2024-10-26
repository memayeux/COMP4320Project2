// for Socket and ServerSocket
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;   // for server input

public class ServerTCP {

  public static void main(String args[]) throws Exception {

    // INITIALIZING SERVER //

    if (args.length != 1)  // Test for correct # of args
      throw new IllegalArgumentException("Parameter(s): <Port>");

    int port = Integer.parseInt(args[0]);   // Receiving Port
	
    ServerSocket servSock = new ServerSocket(port);
    Socket clntSock = servSock.accept();

    boolean cont = true;
    Scanner s = new Scanner(System.in);


    do {

      byte tempResponseID = 1;

      // RECIEVING REQUEST //

      // Receive and decode binary-encoded request
      RequestDecoder decoder = new RequestDecoderBin();
      Request receivedRequest = decoder.decode(clntSock.getInputStream());

      // Signal that request is received
      System.out.println("Received Request:");
      System.out.println(receivedRequest);


      // SENDING RESPONSE //

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

      // Create a new Response object
      Response response = new Response((short) 8, tempResponseID, result, tempErrorCode);

      // Display Response
      System.out.println("Display Response: ");
      System.out.println(response);

      // Encodes Response object
      ResponseEncoder encoder = new ResponseEncoderBin();
      byte[] codedResponse = encoder.encode(response);

      // Send Response to client
      /*
      InetAddress clientAddr = packet.getAddress();
      int clientPort = packet.getPort();
      DatagramPacket sendPacket = new DatagramPacket(codedResponse, codedResponse.length, clientAddr, clientPort);
      sock.send(sendPacket);
      */

      // Verifies keeping server open
      System.out.println("Keep server open? (y/n)");
      String yesNo = s.nextLine();
      if (!yesNo.equals("y")) {
          cont = false;
      }

      tempResponseID++;

  } while (cont);

    s.close();
    clntSock.close();
    servSock.close();

  }
}
