/*
 * Donald Trowbridge
 * Module 8 Programming Project
 * 
 * This application allows the server to send quotes to the client.
 *
 */

package server;

import java.nio.file.Paths;
import java.util.Scanner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;


public class Server 
{
    private static Scanner input;
    private static Socket client;
    private static ObjectOutputStream output;
    private static ObjectInputStream in;
    private static String response;
    private static ServerSocket server;
    private static boolean breakLoop = false;

    public static void main(String[] args) 
    {
        
        startServer();//starts server and accepts client connections
        while(client.isConnected() && breakLoop == false)
        {
            
            sendMessage();//sends quote or exit message but honeslty anything that isn't exit will send a quote
            getInput();//gets input from the client
            if(!response.equalsIgnoreCase("exit")) //anything that isn't exit will send quotes 
                sendQuote();
            else//breaks 
                break;
        }
        closeServer();//closes server connection.
    }
    
    //starts server and accepts client connection.
    public static void startServer()
    {
        try
        {
        System.out.println("Starting server>>>");
        server = new ServerSocket(5964);
        client = server.accept();
        openFile();//opens quote folder
        }
        catch(IOException ex)
        {
            System.out.println("Server cannot be started. "
                    + "Check if another program has this port occupied.");
        }
                
    }
    //sends quote or exit message.
    private static void sendMessage()
    {
        try
        {
            String x = "Type \"quote\" to receive a quote or type \"exit\" to quit the program.";
            output = new ObjectOutputStream(client.getOutputStream());
            output.writeObject(x);
            output.flush();
        }
        catch(IOException ex)
        {
            System.out.println("There has been an error sending the request message.");
        }
    }
    //gets user input.
    private static void getInput()
    {
        try
        {
            
            response = "";//sets string var to null
            //gets clients response
            in = new ObjectInputStream(client.getInputStream());
            response = in.readObject().toString();
        }
        catch(IOException ex)
        {
            System.out.println("There was an error receiving data from the client");
        }
        catch(ClassNotFoundException ex)
        {
            System.out.println("The object that was sent from the client is not a string");
        }
    }
    //opens file to read quotes from
    private static void openFile()
    {
        try
        {
            input = new Scanner(Paths.get("quotes.txt"));
        }
        catch(IOException ex)
        {
 
           System.out.println("Cannot access file");
        }
    }
    //reads lines from file.
    private static String readFile()
    {
        String x;
        if(input.hasNext())//assigns string var to return as quote from file.
            x = input.nextLine();
        else
        {
            x = "EOF";//sends EOF message to client to have client break it's loop
            breakLoop = true;//sets boolean value to break server input/ouput loop
        } 
        return x;
    }
    
    //sends quote to client
    private static void sendQuote()
    {
        try
        {
            output = new ObjectOutputStream(client.getOutputStream());
            output.writeObject(readFile());
            output.flush();
        }
        catch(IOException x)
        {
            System.out.println("Error sending quote.");
        }
    }
    //closes server and closes output/input objecs.
    private static void closeServer()
    {
        try
        {
        output.close();
        input.close();
        in.close();
        client.close();
        server.close();
        }
        catch(IOException ex)
        {
            System.exit(1);
        }
    }
    
}
