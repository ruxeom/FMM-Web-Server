package fmmserver;

import java.net.Socket;
import java.util.Hashtable;
import java.io.*;

public class ServiceThread extends Thread
{
	Hashtable<String, String> mimetypes;
	Hashtable<Integer, String> statuscodes;
	Socket socket;
	
	public ServiceThread(Socket socket, Hashtable<String, String> mimetypes, Hashtable<Integer, String> statuscodes)
	{
		this.socket = socket;
		this.mimetypes = mimetypes;
		this.statuscodes = statuscodes;
	}
	
	public void run()
	{
		try 
		{
			InputStream is = socket.getInputStream();
			InputStreamReader isr =  new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);
			String line = reader.readLine();
			while(!line.equals(-1))
			{
				System.out.println(line);
				line = reader.readLine();
			}
		} 
		catch (IOException e) 
		{
			if(socket != null)
				System.out.println("Error while giving service to client: " + socket.getRemoteSocketAddress());
			e.printStackTrace();
		}
	}
	
}
