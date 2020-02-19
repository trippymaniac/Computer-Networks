//Group Number: 07
//CSE Sec B Batch B1

//Implement two way communication between a client and a server using TCP socket
//Your program should satisfy following requirements
//1. Server should remain on 24X7
//2. Client should be able to reconnect as many times as it wants
//3. Proper closing of connection

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
        String Received_Message, Send_Message;
        System.out.println("Enter the message buddy--->");
        while(true) //enables the server to run 24X7
        {
            Send_Message = bufferedReader.readLine();
            if (Send_Message.equals("END CONNECTION"))
            {
                //Closing the Connections
                printWriter.println(Send_Message);
                printWriter.flush();
                socket.close();
                bufferedReader.close();
                System.out.println("Thank You:)");
                System.exit(0);
            }
            printWriter.println(Send_Message);
            printWriter.flush();
            if((Received_Message = Receive_Read_Message_BReader.readLine()) != null)
            {
                System.out.println("Incoming Message: " + Received_Message);
                if (Received_Message.equals("END CONNECTION"))
                {
                    //Closing the Connections
                    socket.close();
                    bufferedReader.close();
                    System.out.println("Thank You:)");
                    System.exit(0);
                }
            }
        }
    }
}
