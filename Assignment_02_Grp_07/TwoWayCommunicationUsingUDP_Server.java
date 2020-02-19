//Group Number: 07
//CSE Sec B Batch B1

//Re-implement program given in assignment 1 using UDP socket.

package com.two.way.communication.using.udp.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class TwoWayCommunicationUsingUDPSocket {
    private static final int PORT_NUMBER = 1234;

    public static void main(String[] args) throws SocketException
    {
        DatagramSocket datagramSocket = new DatagramSocket(PORT_NUMBER);
        System.out.println("Created a new Datagram Socket");
        try
        {
            String Incoming_Message;
            String Outgoing_Message;
            InetAddress inetAddress = null; //Client's Address
            int Client_Port;
            do
            {
                byte[] buffer = new byte[256];
                DatagramPacket Incoming_Packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(Incoming_Packet);
                inetAddress = Incoming_Packet.getAddress();
                Client_Port = Incoming_Packet.getPort();
                Incoming_Message = new String(Incoming_Packet.getData(),0, Incoming_Packet.getLength());
                System.out.println(inetAddress + ": " + Incoming_Message);
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter the Message Buddy[SERVER TO CLIENT]: ");
                Outgoing_Message = scanner.nextLine();
                //Outgoing_Message = "Message to Server: " + Incoming_Message;
                DatagramPacket Outgoing_Packet = new DatagramPacket(Outgoing_Message.getBytes(), Outgoing_Message.length(), inetAddress, Client_Port);
                datagramSocket.send(Outgoing_Packet);
            }while(true);
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
    }
}
