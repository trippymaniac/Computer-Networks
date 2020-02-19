//Group Number: 07
//CSE Sec B Batch B1

//Implement two way communication between a client and a server using TCP socket
//Your program should satisfy following requirements
//1. Server should remain on 24X7
//2. Client should be able to reconnect as many times as it wants
//3. Proper closing of connection
//4. Modify assignment 1 such that client and server need not require strict alternate turn
//for sending data.

package com.two.way.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TwoWayCommunicationUsingTCPSocket {
    public static void main(String[] args) throws IOException {
        {
            ServerSocket socket = new ServerSocket(3000);
            System.out.println("Established Connection with the Server");
            Socket socket1 = socket.accept();
            System.out.println("Server accepted the connection");
            System.out.println("Server waiting to listen for Data");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            OutputStream outputStream = socket1.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            System.out.println("Created Buffered Reader and Print Writer to begin listen for Data");
            InputStream inputStream = socket1.getInputStream();
            BufferedReader Receive_Read_Message_BReader = new BufferedReader(new InputStreamReader(inputStream));

            Thread sendMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String Send_Message = bufferedReader.readLine();
                            // write on the output stream
                            printWriter.println(Send_Message);
                            printWriter.flush();
                        } catch (IOException e) {
                            System.out.println("Error: " + e);
                        }
                    }
                }
            });

            Thread readMessage = new Thread(new Runnable() {

                @Override
                public void run() {

                    while (true) {
                        try {
                            String Received_Message;
                            if ((Received_Message = Receive_Read_Message_BReader.readLine()) != null)
                            {
                                    System.out.println("Incoming Message: " + Received_Message);
                            }
                        } catch (IOException e) {
                            System.out.println("Error: " + e);
                        }
                    }
                }
            });
            readMessage.start();
            sendMessage.start();
            System.out.println("**** Server is up and ready to Send and Receive Messages ****");
        }
    }
}
