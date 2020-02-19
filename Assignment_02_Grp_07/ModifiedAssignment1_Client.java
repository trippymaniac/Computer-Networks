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
import java.net.Socket;

public class ClientSideCoding {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 3000);
        System.out.println("Established Connection with the socket");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        InputStream inputStream = socket.getInputStream();
        BufferedReader Receive_Read_Message_BReader = new BufferedReader(new InputStreamReader(inputStream));

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("Enter the message buddy[CLIENT TO SERVER]--->");
                        String Send_Message = bufferedReader.readLine();
                        if (Send_Message.equals("END CONNECTION"))
                        {
                            printWriter.println(Send_Message);
                            printWriter.flush();
                            bufferedReader.close();
                            System.out.println("Thank You:)");
                            System.exit(0);
                        }
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
        sendMessage.start();
        readMessage.start();
        System.out.println("**** Client is up and ready to Send and Receive Messages ****");
    }
}
