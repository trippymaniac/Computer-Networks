//Group Number: 07
//CSE Sec B Batch B1

//Implement concurrent sever using TCP socket. A concurrent server is one which can
//accept connection request from multiple clients concurrently and serve them concurrently.
//(Hint: Use threading). Your server should return the addition and subtraction of the two
//numbers sent by any client to that client. Assume that there are no errors.

package com.multithreaded.server.architecture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientSideCommunication {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 3000;
        try
        {
            Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            String line = null;
            System.out.println("Enter the numbers buddy as--->");
            System.out.println("Number-1 , Number-2");
            System.out.println("**** Client is up and ready to Send the numbers to the Server ****");
            while (true)
            {
                line = scanner.nextLine();
                if (line.equals("END CONNECTION"))
                {
                    scanner.close();
                    out.close();
                    System.exit(0);
                }
                out.println(line);
                out.flush();
                String addition_result = in.readLine();
                System.out.println("Addition Result: " + addition_result);
                String subtraction_result = in.readLine();
                System.out.println("Subtraction Result: " + subtraction_result);
            }
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e);
        }
    }
}
