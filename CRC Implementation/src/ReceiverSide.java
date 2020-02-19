import java.io.*;
import java.net.Socket;

public class ReceiverSide {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 5000);
        System.out.println("Established Connection with the socket\n");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        InputStream inputStream = socket.getInputStream();
        BufferedReader Receive_Read_Message_BReader = new BufferedReader(new InputStreamReader(inputStream));

        String Received_Message, Send_Message = "";
        //flag to determine whether remainder is zero or not
        int flag;
        // CRC-8 = x8+x2+x+1 =100000111
        int[] CxArray = {1, 0, 0, 0, 0, 0, 1, 1, 1};
        StringBuilder Decrypted_Final_String = new StringBuilder();
        while(true)
        {
            int[] intArray_of_BitStream = new int[0];
            if((Received_Message = Receive_Read_Message_BReader.readLine()) != null)
            {
                System.out.println("\nIncoming Message from the Server[Sender]: " + Received_Message);
                intArray_of_BitStream = new int[Received_Message.length()];
                for (int i = 0; i < Received_Message.length(); i++)
                {
                    intArray_of_BitStream[i] = Character.digit(Received_Message.charAt(i), 10);
                    //System.out.println(intArray_of_BitStream[i]);
                }
                int[] remainder = divide(intArray_of_BitStream, CxArray);
                flag=0;

                for(int i=0 ; i < remainder.length ; i++) {
                    if(remainder[i] != 0)
                    {
                        flag = 1;
                        // If remainder is not zero then there is an error
                        System.out.println("There is an error in the received data...");
                        //Sending back Negative Acknowledgement to the Server
                        printWriter.println("NAK");
                        printWriter.flush();
                        break;
                    }
                }
                //Otherwise there is no error in the received  data
                if(flag == 0) {
                    System.out.println("Data was received without any error.");
                    //Sending back Acknowledgement to the Server
                    printWriter.println("OK");
                    printWriter.flush();

                    int[] intArray_of_Original_Data_BitStream = new int[Received_Message.length()];
                    for (int i = 0; i < Received_Message.length() - 8; i++) {
                        intArray_of_Original_Data_BitStream[i] = Character.digit(Received_Message.charAt(i), 10);
                    }


                    //Decrypting the Binary Data back to Characters
                    char[] chars = new char[intArray_of_BitStream.length / 7];
                    for (int i = 0; i < chars.length; ++i) {
                        int c = 0;
                        for (int j = i * 7; j < (i + 1) * 7; ++j) {
                            c = c << 1;
                            c += intArray_of_Original_Data_BitStream[j];
                        }
                        chars[i] = (char)c;
                    }

                    String Decrypted_String = new String(chars);
                    System.out.print("Decrypted String: ");
                    System.out.println(Decrypted_String);
                    Decrypted_Final_String.append(Decrypted_String);

                    //To Store the Encrypted String in an File
                    FileWriter fileWriter = new FileWriter("OutputTextFile.txt");
                    fileWriter.write(Decrypted_Final_String.toString());
                    System.out.println("Successfully created an File OutputTextFile.txt containing the Decrypted String");
                    System.out.println("Final Decrypted String: " + Decrypted_Final_String.toString());
                    fileWriter.close();
                }

            }
        }
    }


    private static int[] divide(int[] previous_data, int[] divisor) {
        int[] remainder;
        int i;
        int[] data = new int[previous_data.length + divisor.length];
        System.arraycopy(previous_data, 0, data, 0, previous_data.length);
        remainder = new int[divisor.length];
        // Initially, remainder's bits will be set to the data bits
        System.arraycopy(data, 0, remainder, 0, divisor.length);
        // This loop will continuously xor the bits of the remainder and divisor
        for(i=0 ; i < previous_data.length ; i++)
        {
            if(remainder[0] == 1)
            {
                // We have to xor the remainder bits with divisor bits
                for(int j=1 ; j < divisor.length ; j++)
                {
                    remainder[j-1] = xor(remainder[j], divisor[j]);
                    //System.out.print(remainder[j-1]);
                }
            }
            else
            {
                // We have to xor the remainder bits with 0
                for(int j=1 ; j < divisor.length ; j++)
                {
                    remainder[j-1] = xor(remainder[j], 0);
                    //System.out.print(remainder[j-1]);
                }
            }
            // The last bit of the remainder will be taken from the data
            // This is the 'carry' taken from the dividend after every step
            // of division
            remainder[divisor.length - 1] = data[i + divisor.length];
        }
        return remainder;
    }

    private static int xor(int a, int b) {
        if (a == b) {
            return 0;
        } else {
            return 1;
        }
    }

}
