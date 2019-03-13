/*
 * Donald Trowbridge
 * Module 8 Programming Project
 * 
 * This application allows the client to ask the server for a quote, displays the 
 * quote and saves the quote.
 */


package server;


import java.net.Socket;
import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Formatter;
import java.io.FileNotFoundException;


public class client 
{
    private static Socket client;
    private static ObjectInputStream in;
    private static Scanner input = new Scanner(System.in);
    private static ObjectOutputStream output;
    private static Formatter toFile;
    private static String x = "";
    private static boolean exit = false;
    
    public static void main(String[] args)
    {
        startClient();//Connects client to server
        openFile(); //opens/creates folder to save the quotes in.
        //ideally wanted loop to break when the connection was closed but that failed.
        while(exit==false)// so I bring you good old boolean values.
        {
            receiveInput();//receives line from server asking for either quote or exit
            sendOutput();//sends users response to server
            if(!x.equalsIgnoreCase("exit"))//uses String var determined in sendOutput() 
                downloadQuote();
            else
                break;
        }
        closeFile();
    }
    //connects client program to server
    private static void startClient()
    {
        try
        {
            System.out.println("Starting client connection to 127.0.0.1\n");
            client = new Socket("127.0.0.1", 5964);//There's no place like 127.0.0.1
        }
        catch(IOException ex)
        {
            x = "exit";//sets string var to string value that will break the input/output loop in main method
            System.out.println("There has been an error connecting to the server."
                    + " Check to see that no other programs are using the current port 5964");
        }
        
    }
    //receives quote or exit string from server.
    private static void receiveInput()
    {
        try
        {
            //creates input stream obj
            in = new ObjectInputStream(client.getInputStream());
            //reads and displays the sent information
            String x = in.readObject().toString();
            System.out.println(x);
        }
        catch(IOException ex)
        {
            System.out.println("Error receiving data from the server.");
        }
        catch(ClassNotFoundException ex)
        {
            System.out.println("The object sent by the server is not a String.");
        }
    }
    //sends user response to user
    private static void sendOutput()
    {
        try
        {
            x = input.nextLine();//gets user input
            //creates output stream and sends those little 1's and 0' on their way
            output = new ObjectOutputStream(client.getOutputStream());
            output.writeObject(x);
            output.flush();
        }
        catch(IOException ex)
        {
            System.out.println("Error sending data to the server.");
        }
    }
    //gets quote from server
    private static void downloadQuote()
    {
        try
        {
            //creates input stream obj
            in = new ObjectInputStream(client.getInputStream());
            //assigns input information to String var
            String x = in.readObject().toString();
            //Server sends an EOF message to client when the server is out of quotes.
            if(x.equalsIgnoreCase("EOF"))
                exit = true;
            else//writes quotes to file on client computer 
            {
                System.out.println(x);
                writeToFile(x);
            }
        }
        catch(IOException ex)
        {
            System.out.println("Error receiving quote.");
        }
        catch(ClassNotFoundException ex)
        {
            System.out.println("The object sent is not a string.");
            
        }
    }
    //Opens/creates file for on the client computer to store quotes.
    private static void openFile()
    {
        try
        {
            toFile = new Formatter("quoteFile.txt");
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("Error opening quoteFile.txt");
        }
    }
    
    //writes quotes to file
    private static void writeToFile(String x)
    {
       toFile.format("%s%n", x);
    }
    //closes file
    private static void closeFile()
    {
        toFile.close();
    }
    //closes connection to server... hopefully having issues with closing the connection.
    private static void closeConnection()
    {
        try
        {
            client.close();
        }
        catch(IOException ex)
        {
            System.exit(1);
        }
    }
            
}
