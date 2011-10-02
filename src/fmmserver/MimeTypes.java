package fmmserver;

import java.util.Hashtable;
import java.util.Scanner;
import java.io.*;

public class MimeTypes 
{
	
	public Hashtable<String,String> getMimeHash()
	{
		Hashtable<String,String> table = new Hashtable<String,String>();
		try
		{
			Scanner scanner = new Scanner(new FileInputStream("mimetable.txt"));
			while(scanner.hasNextLine())
			{
				String[] line = scanner.nextLine().split("\t");
				table.put(line[0], line[1]);
			}
			scanner.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return table;
	}
}
