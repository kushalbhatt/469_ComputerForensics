import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class mac_conversion {

	public static void main(String[] args) {
		
		/*
		 * Assuming the input will be already parsed as little Endian
		 */

		if(args[0].equals("-T"))
		{
			//time conversion
			if(args[1].equals("-f"))	
				convertTime(getInputFromFile(args[2]));			 
			else if(args[1].equals("-h"))
				convertTime(args[2]);
			else
				System.out.println("Invalid commands.\n mac_conversion -T|-D [–f filename | -h hex value ]");
		}
		else if(args[0].equals("-D"))
		{
			//date conversion
			//time conversion
			if(args[1].equals("-f"))	
				convertDate(getInputFromFile(args[2]));			 
			else if(args[1].equals("-h"))
				convertDate(args[2]);
			else
				System.out.println("Invalid commands.\n mac_conversion -T|-D [–f filename | -h hex value ]");
		}
		else
		{
			System.out.println("-T or -D not defined. Invalid commands.\n mac_conversion -T|-D [–f filename | -h hex value ]");
		}
	}
	
	
	
	static void convertDate(String input)
	{
		//convert into bit stream to extract required bit values
		StringBuilder str = new StringBuilder("");
		
		//ignore 0x
		for(int i=2;i<input.length();i++)
		{
			str.append(getHex(input.charAt(i)));
		}
		
		int year = Integer.parseInt(str.substring(0, 7),2);
		int mon = Integer.parseInt(str.substring(7, 11),2);
		int day = Integer.parseInt(str.substring(11),2);
		System.out.println("Input Time ==  year: "+year+"  Mon: "+mon+" Day: "+day);
		
		String Month = "";
		switch(mon)
		{
			case 1:Month = "Jan";break;
			case 2:Month = "Feb";break;
			case 3:Month = "Mar";break;
			case 4:Month = "Apr";break;
			case 5:Month = "May";break;
			case 6:Month = "Jun";break;
			case 7:Month = "Jul";break;
			case 8:Month = "Aug";break;
			case 9:Month = "Sep";break;
			case 10:Month = "Oct";break;
			case 11:Month = "Nov";break;
			case 12:Month = "Dec";break;
		}
		System.out.println("Date::  "+Month+" "+day+", "+(year+1980));
	}

	
	
	
	static void convertTime(String input)
	{
		//convert into bit stream to extract required bit values
				StringBuilder str = new StringBuilder("");
				
				//ignore 0x
				for(int i=2;i<input.length();i++)
				{
					str.append(getHex(input.charAt(i)));
				}
				
				int hour = Integer.parseInt(str.substring(0, 5),2);
				int min = Integer.parseInt(str.substring(5, 11),2);
				int sec = Integer.parseInt(str.substring(11),2);
				System.out.println("Time = "+hour+":"+min+":"+(sec*2));
				
				//TODO::  Is it required to beb resented in 12 hour format?
	}
	
	
	
	
	
	static String getInputFromFile(String filepath)
	{
		try {
			Scanner sc = new Scanner(new File(filepath));
			//read just one input even if it could have more
			String input = sc.nextLine().split(" ")[0];
			System.out.println("Read from file: "+input);
			return input;
		} catch (FileNotFoundException e) {
			System.out.println("Error opening the File!"+filepath);
			e.printStackTrace();
		}
		return "";
	}
	
	static String getHex(char c)
	{
		switch(c)
		{
			case '0': return "0000";  
			case '1': return "0001"; 
			case '2': return "0010"; 
			case '3': return "0011"; 
			case '4': return "0100"; 
			case '5': return "0101"; 
			case '6': return "0110"; 
			case '7': return "0111"; 
			case '8': return "1000"; 
			case '9': return "1001"; 
			case 'a': return "1010"; 
			case 'b': return "1011"; 
			case 'c': return "1100"; 
			case 'd': return "1101"; 
			case 'e': return "1110"; 
			case 'f': return "1111";
			default: System.out.println("Invlaid time string!");
		}
			
		return null;
	}
}
