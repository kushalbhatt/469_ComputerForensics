import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Task2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputfilepath = "K:/ASU/469_ Forensics/usb_test_raw.001";
		String inputfilename = inputfilepath;
		
		//extract just the file name
		if(inputfilepath.contains("/"))
			inputfilename = inputfilepath.substring(inputfilepath.lastIndexOf('/')+1);
		System.out.println("InputfileNAme = "+inputfilename);
		
		File file = new File(inputfilepath);
		 
		try {
	            InputStream imageStream = new FileInputStream( file );
	            byte[] mbrBytes = new byte[512];
	           
	            
	            /** read mbr into buffer 
	             * Each value in buffer is one byte
	             * When printed it gives decimal number... convert it into Hex if required
	             * using **/
	            
	            imageStream.read(mbrBytes);
	            //for( int i = 0; i < mbrBytes.length; i++ )
	            //	System.out.println(mbrBytes[i]); 
	            imageStream.close();
	            
	            /********************MD5*********************/
	            byte md5[] = computeHash(file,"MD5");
	            
	            //Output MD5
	            StringBuilder sb = new StringBuilder();
	            for (byte b : md5) {
	                sb.append(String.format("%02X ", b));
	            }
	            System.out.println("MD5==   "+ sb.toString());
	            
	            //Write this to file
	            String MD5Filename = "MD5-"+inputfilename+".txt";
	            writeDIgestToFile(MD5Filename, sb.toString());
	            
	            /***************SHA-1*************/
	            byte sha_1[] = computeHash(file,"SHA-1");
	            
	            //Output MD5
	            sb = new StringBuilder();
	            for (byte b : sha_1) {
	                sb.append(String.format("%02X ", b));
	            }
	            System.out.println("SHA-1==   "+ sb.toString());
	            
	          //Write this to file
	            String SHA1Filename = "SHA1-"+inputfilename+".txt";
	            writeDIgestToFile(SHA1Filename, sb.toString());

	        } catch( IOException ioe ) {
	            System.out.println( "Problem: " + ioe );
	        }
		
	}


	//hash_algo = MD5 or SHA-1
	private static byte[] computeHash(File file,String hash_algo) {
		// TODO Auto-generated method stub
		InputStream imageStream;
		try {
			imageStream = new FileInputStream( file );
			 MessageDigest digest = MessageDigest.getInstance(hash_algo);
		        DigestInputStream dis = new DigestInputStream(imageStream, digest);
		        
		        //Bigger buffer so that reading happens fast
		        byte[] buffer = new byte[65536];
		        int i=0;
		        while (dis.read(buffer) > -1) {
		        	i++;
		        	//just for debugging.... not needed otherwise
		        	if(i%1000==0)
		        		System.out.println(i+" th iteration!");
		        }
		        MessageDigest hash = dis.getMessageDigest();
		        dis.close();
		        imageStream.close();
		        return hash.digest();
		        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static void writeDIgestToFile(String filename,String digest)
	{
		Writer writer;
		BufferedWriter bufferedWriter;
		try {
			writer = new FileWriter(new File(filename));
			 	bufferedWriter = new BufferedWriter(writer);
		        bufferedWriter.write(digest.toString());
		        bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
