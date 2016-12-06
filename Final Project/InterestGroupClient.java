import java.io.*; // Provides for system input and output through data
// streams, serialization and the file system
import java.net.*; // Provides the classes for implementing networking
// applications

/**
 * Created by Yuege on 12/6/2016.
 */
public class InterestGroupClient {
    public static void main(String argv[]) throws Exception
    {
        String sentence;
        String modifiedSentence;

        // get the server port form command line
        int lisPort = Integer.parseInt(argv[1]);

        // create an input stream from the System.in
        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));

        // create a client socket (TCP)
        Socket clientSocket = new Socket(argv[0], lisPort);

        // create an output stream from the socket output steram
        DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream());

        // create an input stream from the socket input steram
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // read a line form the standard input
        sentence = inFromUser.readLine();

        // send the sentence read to the server
        outToServer.writeBytes(sentence + '\n');

        // get the reply from the server
        modifiedSentence = inFromServer.readLine();

        // print the returned sentence
        System.out.println("FROM SERVER: " + modifiedSentence);

        // close the socket
        clientSocket.close();
    }
}
