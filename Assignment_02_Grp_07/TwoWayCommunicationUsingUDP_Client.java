//Group Number: 07
//CSE Sec B Batch B1

//Re-implement program given in assignment 1 using UDP socket.

package com.two.way.communication.using.udp.socket;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class ClientSideCommunicationUsingUDPSocket {
    private static InetAddress host;
    private static final int PORT_NUMBER = 1234;
    public static void main(String[] args)
    {
        try
        {
            host = InetAddress.getLocalHost();
            System.out.println("Connected with the localhost: " + host);
        }
        catch(UnknownHostException e)
        {
            System.out.println("Host Not Found Buddy: " + e);
            System.exit(1);
        }
        try
        {
            DatagramSocket datagramSocket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in);
            String Client_To_Server_Message = " ";
            String Server_To_Client_Message = " ";
            do
            {
                System.out.println("Enter the Message Buddy[CLIENT TO SERVER]: ");
                Client_To_Server_Message = scanner.nextLine();
                DatagramPacket outgoing_Packet = new DatagramPacket(Client_To_Server_Message.getBytes(), Client_To_Server_Message.length(), host, PORT_NUMBER);
                datagramSocket.send(outgoing_Packet);
                if (Client_To_Server_Message.equals("END CONNECTION"))
                {
                    System.out.println("Thank You Buddy");
                    datagramSocket.close();
                    System.exit(0);
                }
                byte[] buffer = new byte[256];
                DatagramPacket incoming_Packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(incoming_Packet);
                Server_To_Client_Message = new String(incoming_Packet.getData(), 0, incoming_Packet.getLength());
                System.out.println("SERVER TO CLIENT MESSAGE: " + Server_To_Client_Message);
                if (Server_To_Client_Message.equals("END CONNECTION"))
                {
                    System.out.println("Thank You Buddy");
                    datagramSocket.close();
                    System.exit(0);
                }
            }while(true);
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
    }
}
