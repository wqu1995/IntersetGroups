import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Yuege on 12/6/2016.
 */
public class InterestGroupServer {
    public static void main(String argv[]) throws Exception
    {
        String clientSentence;
        String capitalizedSentence;

        // get the port number assigned from the command line
        int lisPort = 7667;

        // create a server socket (TCP)
        ServerSocket welcomeSocket = new ServerSocket(lisPort);

        // loop infinitely (process clients sequentially)
        while(true) {
            // Wait and accept client connection
            Socket connectionSocket = welcomeSocket.accept();

            //create an input stream from the socket input steram
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            // create an output stream from the socket output steram
            DataOutputStream outToClient =
                    new DataOutputStream(connectionSocket.getOutputStream());

            // read a line form the input stream
            clientSentence = inFromClient.readLine();
            switch (clientSentence){


            }
            // capitalize the sentence
            capitalizedSentence = clientSentence.toUpperCase() + '\n';

            // send the capitalized sentence back to the  client
            outToClient.writeBytes(capitalizedSentence);

            // close the connection socket
            connectionSocket.close();
        }
    }
}
