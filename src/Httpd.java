import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Httpd {
    public Httpd (int port) {
        ServerSocket s = null;
        try {
            s = new ServerSocket(port);
        } catch(IOException ioe) {
            System.err.println("ServerSocket()");
            System.exit(1);
        }
        while (true) {
            Socket c = null;
            try {
                c = s.accept();
            } catch(IOException ioe) {
                System.err.println("accept()");
                System.exit(1);
            }
            new Handler(c).start();
        }
    }

    private class Handler extends Thread {
        private Map<String, String> _map;
        private Socket _c;

        public Handler (Socket c) {
            _c = c;
            _map = new HashMap<String, String>() {{
                put("html", "text/html");
                put("txt", "text/plain");
            }};
        }

        public void run () {
            try {
                InputStream is = _c.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String command[] = br.readLine().split("\\s+");
                OutputStream os = _c.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                if (3 == command.length) {
                    if ("GET".equals(command[0])) {
                        URL u = new URL("http", "localhost", 80, command[1]);
                        File f = new File(".", u.getPath());
                        String name = f.getName();
                        String ext = "";
                        String parts[] = name.split("\\.");
                        if (1 < parts.length) {
                            ext = parts[parts.length - 1].toLowerCase();
                        }
                        if (f.canRead()) {
                            long bytes = 0;
                            FileReader fr;
                            BufferedReader bfr;
                            fr = new FileReader(f);
                            bfr = new BufferedReader(fr);
                            int c;
                            while (-1 != (c=bfr.read())) {
                                bytes += 1;
                            }
                            
                            fr = new FileReader(f);
                            bfr = new BufferedReader(fr);
                            String type = "text/plain"; // Default
                            if (_map.containsKey(ext)) {
                                type = _map.get(ext);
                            }
                            bw.write("HTTP/1.0 200 Ok");
                            bw.newLine();
                            bw.write("Server: Dualbus/0.9");
                            bw.newLine();
                            bw.write(String.format("Content-Type: %s", type));
                            bw.newLine();
                            bw.write(String.format("Content-Length: %d", bytes));
                            bw.newLine();
                            bw.newLine();
                            while (-1 != (c=bfr.read())) {
                                bw.write(c);
                            }
                            bw.flush();
                            bw.close();
                        }
                        //codigo de fofo
                        else{
                        	
                        }
                    }
                }
            } catch(IOException ioe) {
            }
            try {
                _c.close();
            } catch(IOException ioe) {
            }
        }
    }

    public static void main (String args[]) {
        new Httpd(80);
    }
}
