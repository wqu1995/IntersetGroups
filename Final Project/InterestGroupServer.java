import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yuege on 12/6/2016.
 */
public class InterestGroupServer {
    public static void main(String argv[]) throws Exception
    {
        String clientSentence;
        String capitalizedSentence;
        Date lastChecked;
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
                case "LOGIN LGP":
                    String date=inFromClient.readLine();
                    DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                    lastChecked = format.parse(date);
                    clientSentence=inFromClient.readLine();
                    if(clientSentence.isEmpty())
                        outToClient.writeBytes("LGP 214 No Content\r\n\r\n");
                    else
                        outToClient.writeBytes("401 Bad Request\r\n\r\n");
                    break;


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
