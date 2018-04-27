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
		String inputfilepath = args[0];//"K:/ASU/469_ Forensics/usb_test_raw.001";
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
//	            writeDigestToFile(MD5Filename, sb.toString());
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
//	            writeDigestToFile(SHA1Filename, sb.toString());
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
	            readMBR(mbrBytes,file);
            
	        } catch( IOException ioe ) {
	            System.out.println( "Problem: " + ioe );
	        }
		
	}

	//type, start sector address, and size of each partition in decimal should be printed
	private static void readMBR(byte[] mbrBytes,File file) {
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
	        System.out.println("\n===============================================\nPartition "+(i+1)+":=  "+ sb.toString());
	        String part[]=sb.toString().split(" ");
	        
	        //type
	        String type = getpartitionType(part[4]);
	        System.out.print("("+part[4]+") "+type+", ");
	        
	        //start: byte 11 10 9 8
	        int start = Integer.parseInt((part[11]+part[10]+part[9]+part[8]),16);
	        System.out.print(start+", ");
	        
	        //size: byte 15 14 13 12
	        long size = Long.parseLong((part[15]+part[14]+part[13]+part[12]),16);
	        System.out.println(size);
	        
	        /****  TODO:// If partition is FAT-16 or FAT-32 then read VBR
	         * 		(partition start sector * 512) =  byte offset where VBR is located
	         * 			Read the necessary entries from there  ***/
	        if(type.equals("FAT-16") || type.equals("FAT-32"))
	        {
	        	System.out.println("\n\t----------VBR------------");
	        	InputStream imageStream;
				try {
					imageStream = new FileInputStream( file );
					//skip all bytes till VBR
		        	long skipped = imageStream.skip(start*512);// sector size = 512 bytes
		        	//System.out.println("Skipped == "+skipped+"bytes");
		        	byte[] vbrBytes = new byte[40];
		            imageStream.read(vbrBytes);
		            imageStream.close();
		            sb= new StringBuilder();
		            for (byte b : vbrBytes) {
			            sb.append(String.format("%02X ", b));
			        }
			        //System.out.println("\nVBR:: "+ sb.toString());
		            
			        String vbr[] = sb.toString().split(" ");
		            
		            int reserved_area = Integer.parseInt((vbr[15]+vbr[14]),16); //size, you know start sector already
		            int sectors_per_cluster = Integer.parseInt((vbr[13]),16);
		            int no_fats = Integer.parseInt((vbr[16]),16);
		            int fat_size = Integer.parseInt((vbr[23]+vbr[22]),16);
		            if(type.equals("FAT-32"))
		            	fat_size = Integer.parseInt((vbr[39]+vbr[38]+vbr[37]+vbr[36]),16);
		            long no_of_sectors_inFS = Long.parseLong((vbr[35]+vbr[34]+vbr[33]+vbr[32]),16);//same as size of parition in MBR
			       
		            System.out.println("\tReserved Area:: start sector: "+start+"  end sector: "+(start+reserved_area)+"  size: "+reserved_area);		            
		            //FAT starts after Reserved area... and goes for fat_size in sectors
		            long fat_start = start+reserved_area+1;
		            System.out.println("\tFAT Area::  start= "+(fat_start)+" End: "+(fat_start+(no_fats*fat_size))+"\n\t# of FATs = "+no_fats+"\n\tsize of each fat = "+fat_size);
		            
		            /* TODO:// For FAT-32  
		             * 			     Start of Cluster 2 is just after FAT
		             		   For FAT-16
		             		    	FAT end is followed by Root Directory entries  and then Cluster2
		             		    Calculate accordingly
		             */
		            
		            if(type.equals("FAT-32"))
		            	System.out.println("\tCLuster 2 start:"+(fat_start+(no_fats*fat_size) ) );
		            else
		            {
		            	/*
		            	 * calculate root directory entries size in # of sector
		            	 * (Maximum Root Directory Entries * 32) / Bytes per Sector-- Assume 512 in our case
		            	 */
		            	int max_root_dir_entries = Integer.parseInt((vbr[18]+vbr[17]),16);
		            	System.out.println("Root directory entries == "+max_root_dir_entries);
		            	System.out.println("\tCLuster 2 start:"+((fat_start+(no_fats*fat_size)+(max_root_dir_entries*32/512) ) )  );
		            }
		            	
			            
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	            
	        }
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
		default: return "unallocated";
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
	
	private static void writeDigestToFile(String filename,String digest)
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
