///A Simple Web Server (WebServer.java)

package http.server;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * <p>
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 *
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

    private String baseName = "/home/elie/Documents/INSA/4IF/S1/RESEAUX/HTTP/RESEAUX_HTTP/TP-HTTP-Code/TP-HTTP-Code/src/http/resources";

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
                        if (requestElements.length >= 2) {
                            String action = requestElements[0];
                            String resourceName = requestElements[1];
                            if (resourceName.contains("..")){
                                out.println("HTTP/1.0 403 Forbidden");
                                out.println("Content-Type: text/html");
                                out.println("Server: Bot");
                                out.println("");
                                out.println("403 Forbidden");
                            }
                            else if (action.equals("GET")) {
                                // si on passé des params alors que l'on veut juste consulter une ressource
                                if (resourceName.contains("?") && !resourceName.contains(".py")){
                                    out.println("HTTP/1.0 400 Bad Request");
                                    out.println("Content-Type: text/html");
                                    out.println("Server: Bot");
                                    out.println("");
                                    out.println("400 Bad Request");
                                }
                                else if (resourceName.contains(".txt") || resourceName.contains(".html")){
                                    ArrayList<String> response = this.fromFileToString(baseName + resourceName);
                                    out.println("HTTP/1.0 " + response.get(0));
                                    out.println("Content-Type: text/html");
                                    out.println("Server: Bot");
                                    out.println("");
                                    out.println(response.get(1));
                                }
                                else if (resourceName.contains(".jpg")){
                                    out.println("HTTP/1.0 200 OK");
                                    out.println("Content-Type: image/jpeg");
                                    out.println("Server: Bot");
                                    out.println("");
                                    out.flush();
                                    File file = new File(baseName+resourceName);
                                    OutputStream o = remote.getOutputStream();
                                    Files.copy(file.toPath(), o);
                                    o.flush();
                                }
                                else if (resourceName.contains(".mp3")){
                                    out.println("HTTP/1.0 200 OK");
                                    out.println("Content-Type: video/mp3");
                                    out.println("Server: Bot");
                                    out.println("");
                                    out.flush();
                                    File file = new File(baseName+resourceName);
                                    OutputStream o = remote.getOutputStream();
                                    Files.copy(file.toPath(), o);
                                    o.flush();
                                }
                                else if (resourceName.contains(".py")) {
                                    String[] fileNamePlusParams = resourceName.split("[?]");
                                    ArrayList<String> scriptParameters = new ArrayList<String>();

                                    if (fileNamePlusParams.length == 2) {
                                        String[] params = fileNamePlusParams[1].split("&");
                                        for (String p : params) {
                                            String[] keyValuePair = p.split("=");
                                            // si le couple key value est mal formé, il ne sera pas écrit dans le fichier
                                            if (keyValuePair.length == 2) {
                                                String key = keyValuePair[0];
                                                String value = keyValuePair[1];
                                                scriptParameters.add(value);
                                            }
                                        }
                                        resourceName = fileNamePlusParams[0];
                                    }
                                    ArrayList<String> response = this.executeScript(resourceName, scriptParameters, scriptParameters.size());

                                    out.println("HTTP/1.0 "+response.get(0));
                                    out.println("Content-Type: text/html");
                                    out.println("Server: Bot");
                                    out.println("");
                                    out.println(response.get(1));
                                }
                                else {
                                    out.println("HTTP/1.0 404 Not Found");
                                    out.println("Content-Type: text/html");
                                    out.println("Server: Bot");
                                    out.println("");
                                    out.println("404 Not Found");
                                }
                            }
                            else if (action.equals("POST") && resourceName.split("[?]").length == 2) {
                                String[] fileNamePlusParams = resourceName.split("[?]");
                                // on check s'il a bien des params dans l'url
                                String fileContentToBePosted = "";
                                if (fileNamePlusParams.length == 2){
                                    String fileNameToBeCreated = fileNamePlusParams[0];
                                    String[] params = fileNamePlusParams[1].split("&");
                                    for (String p: params){
                                        String[] keyValuePair = p.split("=");
                                        // si le couple key value est mal formé, il ne sera pas écrit dans le fichier
                                        if (keyValuePair.length == 2){
                                            String key = keyValuePair[0];
                                            String value = keyValuePair[1];
                                            fileContentToBePosted += key+" : "+value+"\n";
                                        }
                                    }
                                    File file = new File(baseName+fileNameToBeCreated);
                                    if (fileContentToBePosted.equals("") || fileContentToBePosted == null){
                                        out.println("HTTP/1.0 400 Bad Request");
                                        out.println("Content-Type: text/html");
                                        out.println("Server: Bot");
                                        out.println("");
                                        out.println("400 Bad Request");
                                    }
                                    else if (file.exists() && !file.isDirectory()) {
                                        String content = this.fromFileToString(baseName+fileNameToBeCreated).get(1);
                                        FileWriter myWriter = new FileWriter(baseName+fileNameToBeCreated);
                                        myWriter.write(content+"\n"+fileContentToBePosted);
                                        myWriter.close();
                                        out.println("HTTP/1.0 200 OK");
                                        out.println("Content-Type: text/html");
                                        out.println("Server: Bot");
                                        out.println("");
                                        out.println("File "+fileNameToBeCreated+" append with data");
                                    }
                                    else{
                                        System.out.println("File "+fileNameToBeCreated+" already exists");
                                        FileWriter myWriter = new FileWriter(baseName+fileNameToBeCreated);
                                        myWriter.write(fileContentToBePosted);
                                        myWriter.close();
                                        out.println("HTTP/1.0 201 Already existing resource");
                                        out.println("Content-Type: text/html");
                                        out.println("Server: Bot");
                                        out.println("");
                                        out.println(fileNameToBeCreated+" created and filled with data");
                                    }
                                }
                            } else if (action.equals("PUT") && resourceName.split("[?]").length == 2) {
                                String[] fileNamePlusParams = resourceName.split("[?]");
                                String fileContentToBePosted = "";
                                if (fileNamePlusParams.length == 2){
                                    String fileNameToBeCreated = fileNamePlusParams[0];
                                    String[] params = fileNamePlusParams[1].split("&");
                                    for (String p: params){
                                        String[] keyValuePair = p.split("=");
                                        // si le couple key value est mal formé, il ne sera pas écrit dans le fichier
                                        if (keyValuePair.length == 2){
                                            String key = keyValuePair[0];
                                            String value = keyValuePair[1];
                                            fileContentToBePosted += key+" : "+value+"\n";
                                        }
                                    }
                                    File file = new File(baseName+fileNameToBeCreated);
                                    if (fileContentToBePosted.equals("") || fileContentToBePosted == null){
                                        out.println("HTTP/1.0 400 Bad Request");
                                    }
                                    else {
                                        if (file.exists() && !file.isDirectory()) {
                                            out.println("HTTP/1.0 200 OK");
                                        }
                                        else {
                                            out.println("HTTP/1.0 201 Created");
                                        }
                                        FileWriter myWriter = new FileWriter(baseName+fileNameToBeCreated);
                                        myWriter.write(fileContentToBePosted);
                                        myWriter.close();
                                    }
                                    out.println("Content-Type: text/html");
                                    out.println("Server: Bot");
                                    out.println("");
                                }
                            } else if (action.equals("DELETE")) {
                                String[] fileNamePlusParams = resourceName.split("[?]");
                                // on check s'il a bien des params dans l'url
                                String fileContentToBePosted = "";
                                File file = new File(baseName+resourceName);

                                if (fileNamePlusParams.length >= 2){
                                    out.println("HTTP/1.0 400 Bad Request");
                                    out.println("Content-Type: text/html");
                                    out.println("Server: Bot");
                                    out.println("");
                                    out.println("400 Bad Request");
                                }
                                else if(file.exists() && !file.isDirectory()){
                                    System.out.println("File "+resourceName+" found and about to be deleted");
                                    String body = "";
                                    if (file.delete()){
                                        System.out.println("File "+resourceName+" deleted");
                                        out.println("HTTP/1.0 200 OK");
                                        body = "File "+resourceName+" deleted";
                                    }
                                    else{
                                        out.println("HTTP/1.0 500 Error");
                                        body = "Server Error occured";
                                    }
                                    out.println("Content-Type: text/html");
                                    out.println("Server: Bot");
                                    out.println("");
                                    out.println(body);
                                }
                                else{
                                    System.out.println("File "+resourceName+" not found");
                                    out.println("HTTP/1.0 404 Not Found");
                                    out.println("Content-Type: text/html");
                                    out.println("Server: Bot");
                                    out.println("");
                                    out.println("File not found");
                                }

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
        try {
            reader = new BufferedReader(new FileReader(path));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            String content = "";
            if (stringBuilder.length() > 0){
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                content = stringBuilder.toString();
                reader.close();
            }
            else{
                content = "";
            }
            response.add("200 OK");
            response.add(content);

        } catch (IOException e) {
            response.add("404 Not found / File error");
            response.add("File Not Found / File Error");
        }
        return response;
    }

    private ArrayList<String> executeScript(String fileName, ArrayList<String> parameters, int nbParameters) {
        ArrayList<String> response = new ArrayList<String>();
        try {
            File file = new File(baseName+fileName);
            if (!file.exists() || file.isDirectory()){
                throw new FileNotFoundException();
            }

            String[] cmd = new String[nbParameters+1];
            cmd[0] = baseName+fileName;
            for (int i = 0; i < nbParameters; ++i){
                cmd[i+1] = parameters.get(i);
            }

            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String body = "";
            String header = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                body += line;
            }
            response.add(header);
            response.add(body);
        }
        catch (FileNotFoundException e){
            response.add("404 Not found");
            response.add("File not found");
        }
        catch (IOException | InterruptedException e){
            response.add("500 Server Error");
            response.add(" ");
        }
        return response;
    }
}

