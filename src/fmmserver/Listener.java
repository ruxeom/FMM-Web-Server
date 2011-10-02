package fmmserver;

import java.io.*;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.Scanner;

public class Listener 
{
	//we need hashtables to handle MIME types and Status Codes
	//they can be parameters for the handler as this Listener's variables or a class
	
	public Listener(int port)
	{
		ServerSocket socket = null;
		Hashtable <String,String> mimetypes = new MimeTypes().getMimeHash();
		while(true)
		{
			try 
			{
				socket = new ServerSocket(port);
				socket.accept();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			//not sure of the parameters for the service thread
			//new ServiceThread().start();
		}
	}
	
	public static void main(String arg[]) throws IOException
	{
		new MimeTypes().getMimeHash();
	}
}
