///A Simple Web Server (WebServer.java)

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 8080");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(8080);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (; ; ) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String request = ".";
        while (request != null && !request.equals("")) {
          request = in.readLine();
          if (request != null) {
            String[] requestElements = request.split(" ");
            for(String e: requestElements){
              System.out.println(e);
            }
            if (requestElements.length >= 2) {
              String action = requestElements[0];
              String resourceName = requestElements[1];
              if (action.equals("GET")) {
                    String baseName = "/home/elie/Documents/INSA/4IF/S1/RESEAUX/HTTP/RESEAUX_HTTP/TP-HTTP-Code/TP-HTTP-Code/src/http/resources";
                    ArrayList<String> response = this.fromFileToString(baseName+resourceName);
                    out.println("HTTP/1.0 "+response.get(0));
                    out.println("Content-Type: text/html");
                    out.println("Server: Bot");
                    out.println("");
                    out.println(response.get(1));
              }
              else if (action.equals("POST")) {

              }
              else if (action.equals("PUT")) {

              }
              else if (action.equals("DELETE")) {

              }
              else if (action.equals("HEAD")) {

              }
              else {
                  out.println("HTTP/1.0 400 Bad Request");
                  out.println("Content-Type: text/html");
                  out.println("Server: Bot");
                  out.println("");
                  out.println("400 Bad Request");
              }
            }
            out.flush();
            remote.close();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Start the application.
   *
   * @param args Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }


  private ArrayList<String> fromFileToString(String path) {
    BufferedReader reader = null;
    ArrayList<String> response = new ArrayList<String>();
    try{
      reader = new BufferedReader(new FileReader(path));
      StringBuilder stringBuilder = new StringBuilder();
      String line = null;
      String ls = System.getProperty("line.separator");
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
        stringBuilder.append(ls);
      }
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      String content = stringBuilder.toString();
      reader.close();
      response.add("200 OK");
      response.add(content);
    }
    catch(IOException e){
      response.add("404 Not found / File error");
      response.add("File Not Found / File Error");
    }
    return response;
  }
}

