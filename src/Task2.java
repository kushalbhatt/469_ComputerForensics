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
			
	             /********************MD5*********************/
//				
//				byte md5[] = computeHash(file,"MD5");
//	            
//	            //Output MD5
//	            StringBuilder sb = new StringBuilder();
//	            for (byte b : md5) {
//	                sb.append(String.format("%02X ", b));
//	            }
//	            System.out.println("MD5==   "+ sb.toString());
//	            
//	            //Write this to file
//	            String MD5Filename = "MD5-"+inputfilename+".txt";
//	            writeDIgestToFile(MD5Filename, sb.toString());
//	            
//	            /***************SHA-1*************/
//	            byte sha_1[] = computeHash(file,"SHA-1");
//	            
//	            //Output MD5
//	            sb = new StringBuilder();
//	            for (byte b : sha_1) {
//	                sb.append(String.format("%02X ", b));
//	            }
//	            System.out.println("SHA-1==   "+ sb.toString());
//	            
//	          //Write this to file
//	            String SHA1Filename = "SHA1-"+inputfilename+".txt";
//	            writeDIgestToFile(SHA1Filename, sb.toString());
//	            
	            
	            /** read mbr into buffer 
	             * Each value in buffer is one byte
	             * When printed it gives decimal number... convert it into Hex if required
	             * using **/
	            InputStream imageStream = new FileInputStream( file );
	            byte[] mbrBytes = new byte[512];
	           
	            imageStream.read(mbrBytes);
	            //for( int i = 0; i < mbrBytes.length; i++ )
	            //	System.out.println(mbrBytes[i]); 
	            imageStream.close();
	            readMBR(mbrBytes);
	            

	        } catch( IOException ioe ) {
	            System.out.println( "Problem: " + ioe );
	        }
		
	}

	//type, start sector address, and size of each partition in decimal should be printed
	private static void readMBR(byte[] mbrBytes) {
		/*
		 * The partition entries start at offset 0x01BE	==  446 decimal. And each entry is 16 bytes long
		 * type = offset 4;    
		 * start sector = offset 8  (4 bytes-- little endian);  
		 * size: offset 12   (4 bytes-- little endian)      
		 */
		System.out.println( "Partition Entries:: ");
		
		byte partition[][] = new byte[4][16];
		
		//each 16bytes from 446 onwards is a partition entry.... each entry folows the same format
		for(int i=446;i<446+16;i++)
		{
			
			//starts at offset 446
			partition[0][i-446]=mbrBytes[i];
			//after 1st partition (+16 bytes)
			partition[1][i-446]=mbrBytes[i+16];
			//after 2nd partition (+16 bytes)
			partition[2][i-446]=mbrBytes[i+32];
			//after 3rd partition (+16 bytes)
			partition[3][i-446]=mbrBytes[i+48];
		}
		
		
		//printing partition entries as Hex 
		for(int i=0;i<4;i++)
		{
			StringBuilder sb = new StringBuilder();
	        for (byte b : partition[i]) {
	            sb.append(String.format("%02X ", b));
	        }
	        System.out.println("\nPartition "+(i+1)+":=  "+ sb.toString());
	        String part[]=sb.toString().split(" ");
	        
	        //type
	        System.out.print("("+part[4]+") "+getpartitionType(part[4])+", ");
	        
	        //start: byte 11 10 9 8
	        int start = Integer.parseInt((part[11]+part[10]+part[9]+part[8]),16);
	        System.out.print(start+", ");
	        
	        //size: byte 15 14 13 12
	        int size = Integer.parseInt((part[15]+part[14]+part[13]+part[12]),16);
	        System.out.println(size);
	        
		}
	}

	private static String getpartitionType(String type) {
		// TODO Auto-generated method stub
		switch(type){
		case "01": return "FAT-12";
		case "04": return "FAT-16";
		case "05": return "Extended";
		case "06": return "FAT-16";
		case "07": return "NTFS";
		case "08": return "AIX bootable";
		case "09": return "AIX data";
		case "0B": return "FAT-32";
		case "0C": return "FAT-32";
		case "17": return "Hidden NTFS";
		case "1B": return "Hidden FAT32";
		case "1E": return "Hidden VFAT";
		case "3C": return "Magic recovery";
		
		case "66":
		case "67":
		case "68":
		case "69": return "Novell";
		
		case "81": return "linux";
		case "82": return "linux swap";
		case "83": return "linux native";
		case "86": return "Windows NT";
		case "87": return "HPFS";
		case "A5": return "FreeBSD";
		case "A9": return "NetBSD";
		case "C7": return "Currupted NTFS";
		case "EB": return "BeOS";
		}
		return null;
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
