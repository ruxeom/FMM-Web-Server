package fmmserver;

import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.*;

import javax.swing.ScrollPaneLayout.UIResource;

public class ServiceThread extends Thread
{
	Hashtable<String, String> mimetypes;
	Hashtable<Integer, String> statuscodes;
	Socket socket;
	ArrayList<String> headers = new ArrayList<String>();
	
	
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
			System.out.println("NEW THREAD");
			InputStream is = socket.getInputStream();
			InputStreamReader isr =  new InputStreamReader(is);
			BufferedReader socketreader = new BufferedReader(isr);
			String line = socketreader.readLine();
			System.out.println(line);
			String[] commandline = line.split(" ");
			if(commandline.length == 3)
				evaluateCommand(commandline, socketreader);
			else
				headers.add(getStatusCode(400));
			
			socketreader.close();
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
	
	public void evaluateCommand(String[] commandline, BufferedReader socketreader)
	{
		String command = commandline[0].toLowerCase();
		
		try 
		{
			URL url = new URL("http", "localhost", this.socket.getLocalPort(), commandline[1]);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			File file = new File(".", url.getPath());
			
			if(command.equals("get"))
			{
				serviceGet(file, socketreader, writer);
			}
			else if(command.equals("head"))
			{
				serviceHead(file, socketreader, writer);
			}
			else
			{
				headers.add(getStatusCode(405));
				writeHeaders(writer);
			}
			writer.flush();
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public void serviceGet(File file, BufferedReader socketreader, BufferedWriter writer)
	{
		//evaluatePath(path);
		int status = serviceHead(file, socketreader, writer);
		if(status > 0)
		{
			writeHeaders(writer);
			if(status == 1)
				writeFile(file, writer);
		}
		else
		{
			headers.add(getStatusCode(404));
			writeHeaders(writer);
		}
	}
	
	public int serviceHead(File file, BufferedReader reader, BufferedWriter writer)
	{
		
		if(file.exists())
		{
			headers.add(getStatusCode(200));
			headers.add("Server: FMM/0.1");
		}
		else
			return 0;		//It does not exist
		
		if(file.isFile())
		{
			String name = file.getName();
			String[] extension = name.split("\\.");
			headers.add("Content-Type: "+ mimetypes.get(extension[extension.length-1]));
			long filesize = getFileSize(file);
			headers.add("Content-Length: "+filesize);
			
			return 1;		//It is a file
		}
		else
			return 2;		//It is a directory
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
	
	public String getStatusCode(int code)
	{
		return new String("HTTP/1.0 " + code + " " + statuscodes.get(code));
	}
	
	public void writeHeaders(BufferedWriter writer)
	{
		try
		{
			for(int i = 0; i < headers.size(); i++)
			{
				writer.write(headers.get(i));
				writer.newLine();
				System.out.println(headers.get(i));
			}
			writer.newLine();
		}
		catch(IOException ioe)
		{
			System.out.println("Error while trying to write the headers.");
			ioe.printStackTrace();
		}
	}
	
	public void writeFile(File file, BufferedWriter writer)
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			int data;
			while((data = reader.read()) != -1)
			{
				writer.write(data);
			}
		} 
		catch (FileNotFoundException fnfe) 
		{
			// Again, just to please the compiler
			fnfe.printStackTrace();
		}
		catch (IOException ioe)
		{
			System.out.println("Error while reading/writing response content file.");
		}
	}
}
