import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CompressorServer {
    public CompressorServer() {
        try {
            Compressor c = new CompressorImpl();
            Naming.rebind("rmi://localhost:1099/CalculatorService", c);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }
    public static void main(String args[]) throws RemoteException {
        final Registry registry = LocateRegistry.createRegistry(1099);
        new CompressorServer();
    }

}


