package fmmserver;

import java.util.Hashtable;
import java.util.Scanner;
import java.io.*;

public class StatusCodes 
{
	public Hashtable<Integer, String> getCodeHash()
	{
		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
		try
		{
			Scanner scanner = new Scanner(new FileInputStream("statuscodes.txt"));
			String[] line;
			while(scanner.hasNextLine())
			{
				line = scanner.nextLine().split(" ", 2);
				table.put(Integer.parseInt(line[0]), line[1]);
			}
			scanner.close();
		}
		catch(IOException e)
		{
			System.out.println("Error while trying to load Status Codes.");
			e.printStackTrace();
		}
		return table;
	}
}
