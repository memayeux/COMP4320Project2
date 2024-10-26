/*
 * This is the client's Request to the server.
 */
public class Request {
    public short tml;            // Total message length (in bytes)
    public byte opCode;         // Desired operation code
    public short op1;            // First operand
    public short op2;            // Second operand
    public byte requestID;      // Differentiates Requests
    public byte opNameLength;   // Length of operation name (in bytes)
    public String opName;      // Operation name encoded with UTF-16
    
    
    public Request(short tml, byte opCode, short op1, short op2, byte requestID, byte opNameLength, String opName) {
        this.tml = tml;
        this.opCode = opCode;
        this.op1 = op1;
        this.op2 = op2;
        this.requestID = requestID;
        this.opNameLength = opNameLength;
        this.opName = opName;
        }

    public String toString() {
        final String EOLN = java.lang.System.getProperty("line.separator");
        String value = op1 + EOLN + opCodeTable(opCode)+ op2 + EOLN;
        return value;
    }


    public char opCodeTable(int opCode) {
        char operator;
        switch (opCode) {
            case 0:
                operator = '/';
                break;
            case 1:
                operator = '*';
                break;
            case 2:
                operator = '&';
                break;
            case 3:
                operator = '|';
                break;
            case 4:
                operator = '+';
                break;
            case 5:
                operator = '-';
                break;
            default:
                throw new IllegalArgumentException("Unknown operand");
        }
        return operator;
    }

}
