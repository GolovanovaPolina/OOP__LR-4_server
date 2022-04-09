import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;



public class CompressorImpl extends java.rmi.server.UnicastRemoteObject
        implements Compressor {
    static byte[] byteImage;
    static int offset;

    public static int convertByteArrayToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
    public static byte[] convertIntToByteArray(int value) {
        return  ByteBuffer.allocate(4).putInt(value).array();
    }

    public static int convertByteArrayToInt2(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                ((bytes[3] & 0xFF) << 0);
    }

    public static ArrayList<Byte> compressMap(){
        ArrayList<Byte> newMap = null;

        int count = 1;
        int curPixel = convertByteArrayToInt(Arrays.copyOfRange(byteImage, 0, 4));
        for (int i = offset; i<byteImage.length; i+=4) {
            int nextPixel = convertByteArrayToInt(Arrays.copyOfRange(byteImage, i+4, i+8));

            if (curPixel == nextPixel) {
                count++;
            } else {
                // Записываем.
                byte[] c = convertIntToByteArray(count);
                byte[] p = convertIntToByteArray(curPixel);

                for (int j = 0; j < 4; j++){
                    newMap.add(c[j]);
                }

                for (int j = 0; j < 4; j++){
                    newMap.add(p[j]);
                }

                count = 1;
            }
            curPixel = nextPixel;
        }
        return newMap;
    }

    // Реализации должны иметь явный конструктор для того, чтобы объявить
// исключительную ситуацию RemoteException
    public CompressorImpl()
            throws java.rmi.RemoteException {
        super();
    }

    public byte[] GetCompressedPicture() throws java.rmi.RemoteException, IOException {
        /* Начало карты */
        byte[] bof = Arrays.copyOfRange(byteImage, 10, 14);
        offset = convertByteArrayToInt2(bof);


        ArrayList<Byte> compressedByteImage = null;
        byte[] newHeader = Arrays.copyOfRange(byteImage, 0, offset);
        for (int i = 0; i<newHeader.length; i++){
            compressedByteImage.add((Byte)newHeader[i]);
        }
        compressedByteImage.addAll(compressMap());

        byte[] result = new byte[compressedByteImage.size()];
        IntStream.range(0, compressedByteImage.size()).forEach(i -> result[i] = compressedByteImage.get(i));
        return result;
    };

    public void SetPicture(byte[] bytes) throws IOException {
        BufferedImage image = javax.imageio.ImageIO.read(new ByteArrayInputStream(bytes));

        if (bytes[0] == 'B' && bytes[1] == 'M') this.byteImage = bytes;
        else throw new IOException("Image isn't BMP format!");
    };
}

/* Новая карта изображения в Integer. *//*
        ArrayList<Integer> intImageMap = new ArrayList<Integer>();
        Integer count = 1;
        for (int xPixel = 0; xPixel < image.getWidth(); xPixel++)
        {
            for (int yPixel = 0; yPixel < image.getHeight() - 1; yPixel++)
            {
                Integer color = image.getRGB(xPixel, yPixel);
                if (color == image.getRGB(xPixel, yPixel+1)) {
                    count++;
                } else {
                    intImageMap.add(count);
                    intImageMap.add(color);
                    count = 1;
                }
            }
        };*/