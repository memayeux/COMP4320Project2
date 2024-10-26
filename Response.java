/*
 * This is the server's Response to the client.
 */
public class Response {
    public short tml = 8;       // Total message length = length in bytes of outgoing message
    public byte responseID;   // Identifies which response this is
    public int result;        // Result of the server's computation
    public byte errorCode;    // Whether the TML actually matched the message length

    public Response(short tml, byte responseID, int result, byte errorCode) {
        this.tml = tml;
        this.responseID = responseID;
        this.result = result;
        this.errorCode = errorCode;
        // TML should always be 8 bytes because there are no
        // variable data types in Response.
    }

    public String toString() {
        final String EOLN = java.lang.System.getProperty("line.separator");
        String value = "The result of your request is " + EOLN + result + EOLN;
        return value;
    }
}
