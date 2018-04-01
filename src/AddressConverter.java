import java.util.HashMap;
import java.util.Set;

public class AddressConverter {
	
	HashMap<String, String> arg_map;
	
	public AddressConverter() {
		arg_map = new HashMap<String,String>();
	}

	public void parseCommandLineArgs(String args[])
	{
		//first argument will be -L  -P or -C  denoting x to this conversion
		//System.out.println("I am added = "+ args[0]);
		
	      for(int i=1;i<args.length;i++)
	      {
	    	  /*TO:DO----
	    	   * 1. Need to handle cases where  -- options with = sign are given 
	    	   * 	e.g.  --physical_address=128
	    	   * Current code won't parse that correctly   partition-start
	    	   * 
	    	   * 2. -s option needs to be added (no value will follow)
	    	   * */  
	    	  
	    	 
	    	  if(args[i].equals("-B"))
	    		  arg_map.put(args[i], "");
	    	  else
	    	  {
	    		  //handle --x=y cases
	    		  if(args[i].startsWith("--"))
	    		  {
	    			  int value_after_index = args[i].indexOf("=");
	    			  String option = args[i].substring(2, value_after_index);
	    			  String value = args[i].substring(value_after_index+1);
	    			  
	    			  //Map --x options to its corresponding single letter options
	    			  // So, the core logic of calculations remains same with ease of extracting required info
	    			  switch(option)
	    			  {
	    			  	case "partition-start": arg_map.put("-b", value); break;
	    			  	case "logical-known": 	arg_map.put("-l", value); break;
	    			  	case "physical-known": 	arg_map.put("-p", value);break;
	    			  	case "cluster-known": 	arg_map.put("-c", value);break;
	    			  	case "cluster-size": 	arg_map.put("-k", value);break;
	    			  	case "reserved": 		arg_map.put("-r", value);break;
	    			  	case "fat-tables":		arg_map.put("-t", value);break;
	    			  	case "fat-length": 		arg_map.put("-f", value);break;
	    			  	default: System.out.println("Invalid Arguments...");return;
	    			  }	    			  
	    		  }
	    		  else
	    		  {
		    		  //option followed by its value
			    	  System.out.println("added = " + args[i]);
			    	  arg_map.put(args[i], args[i+1]);
			    	  i++;
	    		  }
		    	  	  
	    	  }
	      }
	      System.out.println(arg_map.toString()+"      ----"+ args[0].compareTo("-L"));
	      
	      /*  Decide which conversion function to call?*/
	      if(args[0].equals("-L"))
	      {
	    	  if(arg_map.containsKey("-c")){
	    		  //cluster to logical conversion
	    		  Cluster_to_Logical();
	    	  }
	    	  else if(arg_map.containsKey("-p")){
	    		  //physical to logical address
	    		  Physical_to_Logical();
	    		  }
	    	  else
	    		  System.out.println("Invalid Arguments. Specify -c or -p with     -L");
	    	  
	      }
	      else if(args[0].equals("-P"))
	      {
	    	  if(arg_map.containsKey("-c")){
	    		  //cluster to physical conversion
	    		  Cluster_to_Physical();
	    	  }
	    	  else if(arg_map.containsKey("-l")){
	    		  //logical to physical address
	    		  //Logical_to_Physical();
	    		  }
	    	  else
	    		  System.out.println("Invalid Arguments. Specify -c or -l with     -P");
	      }
	      else if(args[0].equals("-C"))
	      {
	    	  if(arg_map.containsKey("-p"))
	    	  {
	    		  // physical to cluster conversion
	    		  String offset = arg_map.get("-b");
	    		    
	    		    if(offset==null){
	    				System.out.println("Please speicfy  -b  ");
	    				return;
	    			}
	    		    long physical = Long.parseLong(arg_map.get("-p"));
	    		    physical = physical - Long.parseLong(offset);
	    		    
	    		    System.out.println("Cluster address == "+Physical_to_cluster(physical));
	    	  }
	    	  else if(arg_map.containsKey("-l"))
	    	  {
	    		  //logical to cluster address
	    		  //logic remains the same as physical to cluster  just ignore the offset
	    		  long physical = Long.parseLong(arg_map.get("-l"));
	    		  System.out.println("Cluster address == "+Physical_to_cluster(physical));
	    	  }
	    	  else
	    		  System.out.println("Invalid Arguments. Spcify -p or -l with     -C");
	      }
	      else
	      {
	    	  System.out.println("Invalid Arguments. Must specify -L or -P or -C");
	      }
	}
	
	
	public void Physical_to_Logical()
	{
		//check required parameters are present or not

		String offset = arg_map.get("-b");
		if(offset==null){
			System.out.println("Please speicfy  -b  ");
			return;
		}
		long physical = Long.parseLong(arg_map.get("-p"));
		long logical_offset = Long.parseLong(offset);
		
		System.out.println("Logical address == "+(physical-logical_offset));
	}
	
	public void Cluster_to_Physical()
	{
		//check required parameters are present or not
	    String cluster_size = arg_map.get("-k");
	    String reserved = arg_map.get("-r");
	    String no_of_FAT = arg_map.get("-t");
	    String FAT_size = arg_map.get("-f");
	    
	    String offset = arg_map.get("-b");
	    String cluster = arg_map.get("-c");
		if(cluster_size==null || reserved==null || no_of_FAT==null||FAT_size==null)
		{
			System.out.println("Invalid.... -k -t -r -f with -c ");
			return;
		}
		//do the calc
		//find the physical address 
		long address = Integer.parseInt(reserved) + (Integer.parseInt(no_of_FAT)*Integer.parseInt(FAT_size)) + (Integer.parseInt(cluster_size)*(Integer.parseInt(cluster)-2));
		address = Integer.parseInt(offset) + address;
		System.out.println("Physical address == "+address);
	}
	
	public void Cluster_to_Logical()
	{
		 String cluster_size = arg_map.get("-k");
		 String cluster = arg_map.get("-c");
		 String reserved = arg_map.get("-r");
		 String no_of_FAT = arg_map.get("-t");
		 String FAT_size = arg_map.get("-f");
		  
		    if(cluster_size==null || reserved==null || no_of_FAT==null||FAT_size==null)
			{
				System.out.println("Invalid.... -k -t -r -f with -c ");
				return;
			}
		 //same as cluster to physical just don't add the offset   
		 long address = Integer.parseInt(reserved) + (Integer.parseInt(no_of_FAT)*Integer.parseInt(FAT_size)) + (Integer.parseInt(cluster_size)*(Integer.parseInt(cluster)-2));
		 System.out.println("Logical address == "+address);
	}
	
	public void Logical_to_Physical()
	{
		String offset = arg_map.get("-b");
		if(offset==null){
			System.out.println("Please speicfy  -b  ");
			return;
		}
		long logical = Long.parseLong(arg_map.get("-l"));
		long partition_offset = Long.parseLong(offset);
		
		System.out.println("Logical address == "+(logical+partition_offset));
	}
	
	public int Physical_to_cluster(long physical)
	{
		String cluster_size = arg_map.get("-k");
	    String reserved = arg_map.get("-r");
	    String no_of_FAT = arg_map.get("-t");
	    String FAT_size = arg_map.get("-f");	
	    
	    
	    if(cluster_size==null || reserved==null || no_of_FAT==null||FAT_size==null)
		{
			System.out.println("Invalid.... -k -t -r -f with -C ");
			return -1;
		}
	    
	    physical = physical - ( Integer.parseInt(reserved) + (Integer.parseInt(no_of_FAT)*Integer.parseInt(FAT_size)) );
	    int cluster_no = (int)( physical / Integer.parseInt(cluster_size));
	    cluster_no+= 2;
	    return cluster_no;
	    //System.out.println("Cluster address == "+physical);
	}
	
	
}
