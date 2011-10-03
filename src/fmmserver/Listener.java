package fmmserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;

public class Listener 
{
	
	public Listener(int port)
	{
		ServerSocket serversocket = null;
		try
		{
			serversocket = new ServerSocket(port);
		}
		catch(IOException e)
		{
			System.out.println("Error while creating ServerSocket with parameter port: "+ port);
			e.printStackTrace();
			System.exit(1);
		}
		
		Hashtable <String,String> mimetypes = new MimeTypes().getMimeHash();
		Hashtable<Integer, String> statuscodes = new StatusCodes().getCodeHash();
		while(true)
		{
			Socket clientsocket = null;
			try 
			{
				clientsocket = serversocket.accept();
			} 
			catch (IOException e) 
			{
				System.out.println("Error while attempting to accept a connection.");
				e.printStackTrace();
			}
			
			new ServiceThread(clientsocket, mimetypes, statuscodes).start();
		}
	}
	
	public static void main(String arg[]) throws IOException
	{
		new Listener(80);
	}
}
