import java.io.*;  // For ByteArrayOutputStream and DataOutputStream

// Placeholder interface definitions, replace with actual implementations
interface FriendEncoder {
    byte[] encode(Friend friend) throws Exception;
}

interface FriendBinConst {
    String DEFAULT_ENCODING = "UTF-8";  // Default encoding
    byte SINGLE = 1;  // Single flag value
    byte RICH = 2;    // Rich flag value
    byte FEMALE = 4;  // Female flag value
    int MAX_LASTNAME_LEN = 50;  // Maximum lastname length
}

// Placeholder Friend class definition, replace with the actual Friend class
class Friend {
    long ID;
    short streetNumber;
    int zipCode;
    boolean single;
    boolean rich;
    boolean female;
    String lastName;

    // Constructor for Friend class, adjust as needed
    public Friend(long ID, short streetNumber, int zipCode, boolean single, boolean rich, boolean female, String lastName) {
        this.ID = ID;
        this.streetNumber = streetNumber;
        this.zipCode = zipCode;
        this.single = single;
        this.rich = rich;
        this.female = female;
        this.lastName = lastName;
    }
}

public class FriendEncoderBin implements FriendEncoder, FriendBinConst {

    private String encoding;  // Character encoding

    public FriendEncoderBin() {
        encoding = DEFAULT_ENCODING;
    }

    public FriendEncoderBin(String encoding) {
        this.encoding = encoding;
    }

    public byte[] encode(Friend friend) throws Exception {

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        
        out.writeLong(friend.ID);
        out.writeShort(friend.streetNumber);
        out.writeInt(friend.zipCode);
        
        // Flags setup
        byte flags = 0;
        if (friend.single)
            flags = SINGLE;
        if (friend.rich)
            flags |= RICH;
        if (friend.female)
            flags |= FEMALE;
        out.writeByte(flags);

        // Encode the last name with length check
        byte[] encodedLastname = friend.lastName.getBytes(encoding);
        if (encodedLastname.length > MAX_LASTNAME_LEN)
            throw new IOException("Friend lastname exceeds encoded length limit");
        
        out.writeByte(encodedLastname.length); // Write length of last name
        out.write(encodedLastname);  // Write last name bytes
        out.flush();

        return buf.toByteArray();
    }
}
