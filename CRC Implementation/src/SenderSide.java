import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class SenderSide {

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(5000);
        System.out.println("Established Connection with the Server");
        Socket socket1 = socket.accept();
        System.out.println("Server accepted the connection");


        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        OutputStream outputStream = socket1.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        System.out.println("Created Buffered Reader and Print Writer to begin listen for Data");
        System.out.println("CRC-8 is used to Send and Receive the Data between the Server and the Client");
        System.out.println("Reading 4 characters at a time");
        InputStream inputStream = socket1.getInputStream();

        BufferedReader Receive_Read_Message_BReader = new BufferedReader(new InputStreamReader(inputStream));
        FileReader textFileReader = new FileReader("test.txt");
        BufferedReader bufReader = new BufferedReader(textFileReader);

        char[] buffer = new char[4];
        final Charset UTF_8 = StandardCharsets.UTF_8;

        int numberOfCharsRead = bufReader.read(buffer); // read will be from
        int frame_Count = 1;
        //initialising Mx
        String MxString;
        // CRC-8 = x8+x2+x+1 =100000111
        int[] CxArray = {1, 0, 0, 0, 0, 0, 1, 1, 1};
        String received_Message;

        // memory
        while (numberOfCharsRead != -1) {
            //Read 4 characters at a time , Since value of buffer declared is 4
            String inputChar = String.valueOf(buffer, 0, numberOfCharsRead);

            MxString = "";

            byte[] bytes = inputChar.getBytes(UTF_8); //
            //System.out.println("Bytes= "+ Arrays.toString(bytes));

            String a;
            for (int i=0; i<numberOfCharsRead; i++)
            {
                a = Integer.toBinaryString(bytes[i]);
                MxString = MxString + a;
            }
            //System.out.println(MxString);

            int[] MxArray = new int[MxString.length()];

            for (int i = 0; i < MxString.length(); i++) {
                MxArray[i] = Character.digit(MxString.charAt(i), 10);
            }
            //System.out.println("Mx= "+ Arrays.toString(MxArray));

            int[] RxArray = divide(MxArray, CxArray);
            //System.out.println("Remainder= "+ Arrays.toString(RxArray));

            int[] PxArray = new int[MxArray.length + RxArray.length - 1];
            System.arraycopy(MxArray, 0, PxArray, 0, MxArray.length);
            System.arraycopy(RxArray, 0, PxArray, MxArray.length, RxArray.length - 1);
            //System.out.println("String to send= "+ Arrays.toString(PxArray));

            StringBuilder stringBuilder_Px = new StringBuilder();
            for (int i = 0; i < PxArray.length; i++)
            {
                stringBuilder_Px.append(PxArray[i]);
            }

            System.out.println("\n\nSending Frame: " + frame_Count);
            int retransmitting_Count = 0;

            System.out.println("P(x)--> " + stringBuilder_Px.toString());


            received_Message="NAK";
            while(received_Message.equals("NAK")) {
                int Random_Number_to_Generate_the_Error = ThreadLocalRandom.current().nextInt(0, 1000 + 1);
                System.out.println("Random Number to generate Error or not: " + Random_Number_to_Generate_the_Error);
                if (Random_Number_to_Generate_the_Error % 2 == 0) {
                    //Do not induce an error
                    printWriter.println(stringBuilder_Px.toString());
                    printWriter.flush();

                }
                else
                    {
                    //Induce an error
                    //upper bound = number + 1
                    int randomInt = ThreadLocalRandom.current().nextInt(0, PxArray.length);

                    int[] Px_With_Error = PxArray;
                    if (Px_With_Error[randomInt] == 0)
                    {
                        Px_With_Error[randomInt] = 1;
                    }
                    else if (Px_With_Error[randomInt] == 1)
                    {
                        Px_With_Error[randomInt] = 0;
                    }

                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < Px_With_Error.length; i++) {
                        builder.append(Px_With_Error[i]);
                    }

                    System.out.println("Random Integer Generated to flip the bit: " + randomInt);
                    System.out.println("Error Containing Data[P(x)+E(x)]--> " + builder.toString());
                    printWriter.println(builder.toString());
                    //System.out.println("Retransmitting the same Frame " + Frame_Count);
                    System.out.println("Transmitting Error containing Data to the Client[Receiver]");
                    printWriter.flush();
                }

                if ((received_Message = Receive_Read_Message_BReader.readLine()) != null) {
                    System.out.println("Incoming Message from the Client: " + received_Message);
                    if (received_Message.equals("NAK")) {
                        retransmitting_Count++;
                        System.out.println("\nNumber of times Retransmitting Frame "+ frame_Count +" = " + retransmitting_Count);
                    }
                }
            }
            frame_Count++;
            numberOfCharsRead = bufReader.read(buffer);
        }
        socket.close();
        bufReader.close();
        printWriter.flush();
        printWriter.close();
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
        if (a == b)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
}
