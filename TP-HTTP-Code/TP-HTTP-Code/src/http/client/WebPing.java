package http.client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
public class WebPing {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage java WebPing <server host name> <server port number>");
            return;
        }
        int httpServerPort = Integer.parseInt(args[1]);
        String httpServerHost = args[0];
        try {
            Socket sock = new Socket(httpServerHost, httpServerPort);
            InetAddress addr = sock.getInetAddress();
            System.out.println("Connected to " + addr);
            try {
                BufferedReader socIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintStream socOut = new PrintStream(sock.getOutputStream());
                while (true) {
                    socOut.println("GET / HTTP/1.1");
                    socOut.println("");
                    String reply = socIn.readLine();
                    socOut.println(reply);
                    System.out.println("server said:" + reply);
                }
            } catch (Exception e) {
                System.err.println("Error in EchoServer:" + e);
            }
        } catch (java.io.IOException e) {
            System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
            System.out.println(e);
        }
    }
}