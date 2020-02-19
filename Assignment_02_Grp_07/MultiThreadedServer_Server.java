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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class MultiThreadedServerUsingTCP {
    public static void main(String[] args) {
        ServerSocket server = null;
        int port = 3000;
        try
        {
            server = new ServerSocket(port);
            while (true)
            {
                System.out.println("New Server Socket Created");
                server.setReuseAddress(false);
                Socket client = server.accept();
                System.out.println("Connected to a new client: " + client.getInetAddress().getHostAddress());
                System.out.println("**** Server is up and ready to Receive the Numbers from the Client ****");
                ClientHandler clientSocket = new ClientHandler(client);
                new Thread(clientSocket).start();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (server != null)
            {
                try
                {
                    server.close();
                }
                catch (IOException e)
                {
                    System.out.println("Error: " + e);
                }
            }
        }
    }

    private static class ClientHandler implements Runnable{
        private final Socket clientSocket;

        private ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }


        @Override
        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            try
            {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                        System.out.printf("Sent from the client: %s\n", line);
                        int addition_result;
                        int subtraction_result;
                        // Use StringTokenizer to break the input into operand and
                        // separator
                        StringTokenizer stringTokenizer = new StringTokenizer(line);
                        int input_number_1_from_client = Integer.parseInt(stringTokenizer.nextToken());
                        String separator = stringTokenizer.nextToken();
                        int input_number_2_from_client = Integer.parseInt(stringTokenizer.nextToken());
                        addition_result = input_number_1_from_client + input_number_2_from_client;
                        subtraction_result = input_number_1_from_client - input_number_2_from_client;
                        System.out.println("Sending back the result to the Client...");
                        // send the result back to the client.
                        out.println(addition_result);
                        out.println(subtraction_result);
                }
            }
            catch (IOException e)
            {
                System.out.println("Error: " + e);
            }
            finally {
                try
                {
                    if (out != null)
                    {
                        out.close();
                    }
                    if (in != null)
                    {
                        in.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e)
                {
                    System.out.println("Error: " + e);
                }
            }
        }
    }
}
