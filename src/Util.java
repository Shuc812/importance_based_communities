import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Util {

	public static void writeIntArrayToBinaryFile(int[] arr, String filename) throws Exception {
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
				
        for (int i=0; i<arr.length; i++)
        	dos.writeInt(arr[i]);
        
        dos.close();
	}
	
	public static void readIntArrayFromBinaryFile(int[] arr, String filename) throws Exception {
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
		
		for (int i=0; i<arr.length; i++)
			arr[i] = dis.readInt();
		
		dis.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
