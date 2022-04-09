import java.io.IOException;

public interface Compressor extends java.rmi.Remote {
    public byte[] GetCompressedPicture() throws java.rmi.RemoteException, IOException;
    public void SetPicture(byte[] bytes) throws java.rmi.RemoteException, IOException;
}