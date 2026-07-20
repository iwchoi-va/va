package cs.com.util;

public interface CryptoPadding
{

    public abstract byte[] addPadding(byte abyte0[], int i);

    public abstract byte[] removePadding(byte abyte0[], int i);
}

