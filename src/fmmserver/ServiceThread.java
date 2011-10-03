package fmmserver;

import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.*;

import javax.swing.ScrollPaneLayout.UIResource;

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
			String[] commandline = line.split(" ");
			if(commandline.length == 3)
				evaluateCommand(commandline, reader);
			
			//else
				//answerBadRequest(reader);
			/*while(!line.equals(""))
			{
				System.out.println(line);
				line = reader.readLine();
			}*/
			//System.out.println("Termine");
			reader.close();
			socket.close();
		} 
		catch (IOException ioe) 
		{
			System.out.println("Error while giving service to client"+
					((socket != null)? ": "+socket.getRemoteSocketAddress():"."));
			ioe.printStackTrace();
		}
		catch (NullPointerException npe)
		{
			System.out.println("The client disconnected unexpectedly.");
		}
	}
	
	public void evaluateCommand(String[] commandline, BufferedReader reader)
	{
		String command = commandline[0].toLowerCase();
		if(command.equals("get"))
		{
			serviceGet(commandline[1], reader);
		}
		else if(command.equals("head"))
		{
			//serviceHead();
		}
		else
		{
			//commandNotSupported();
		}
	}
	
	public void serviceGet(String path, BufferedReader reader)
	{
		//evaluatePath(path);
		URL u;
		BufferedWriter writer;
		try 
		{
			u = new URL("http", "localhost", 80, path);
			File file = new File(".", u.getPath());
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			if(file.exists())
			{
				serviceHead(file, reader, writer);
			}
			else
			{
				//answerNotFound();
			}
		} 
		catch (MalformedURLException urle) 
		{
			//answerBadRequest();
			urle.printStackTrace();
		}
		catch (IOException ioe)
		{
			System.out.println("Error while creating the BufferedWriter.");
			ioe.printStackTrace();
		}
	}
	
	public void serviceHead(File file, BufferedReader reader, BufferedWriter writer)
	{
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("Server: FMM/0.1");
		if(file.isFile())
		{
			String name = file.getName();
			String[] extension = name.split("\\.");
			headers.add("Content-Type: "+ mimetypes.get(extension[extension.length-1]));
			long filesize = getFileSize(file);
			headers.add("Content-Length: "+filesize);
		}
	}
	
	public long getFileSize(File file)
	{
		long filesize = 0;
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while(reader.read() != -1)
			{
				filesize++;
			}
		} 
		catch (FileNotFoundException e) 
		{
			// We already know it exists but FINE, let's do it YOUR way
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return filesize;
	}
}
