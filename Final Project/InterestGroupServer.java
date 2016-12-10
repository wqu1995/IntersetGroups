import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            switch (clientSentence.split(" ")[0]){
                case "LOGIN":
                    String date=inFromClient.readLine();
                    DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                    lastChecked = format.parse(date);
                    clientSentence=inFromClient.readLine();
                    if(clientSentence.isEmpty())
                        outToClient.writeBytes("IGP 214 No Content\r\n\r\n");
                    break;
                case "AG":
                    outToClient.writeBytes("IGP 310 All Groups\r\n\r\n");
                    outToClient.writeBytes("Groups row by row\r\n\r\n");
                    break;
                case "SG":
                    clientSentence=inFromClient.readLine();
                    int[] subGroupIDs=new int[clientSentence.split(" ").length-1];
                    for (int i=1,j=0;i<clientSentence.split(" ").length;i++)
                        subGroupIDs[j++]=Integer.parseInt(clientSentence.split(" ")[i]);
                    clientSentence=inFromClient.readLine();
                    int[] readPostIDs=new int[clientSentence.split(" ").length-1];
                    for (int i=1,j=0;i<clientSentence.split(" ").length;i++)
                        readPostIDs[j++]=Integer.parseInt(clientSentence.split(" ")[i]);
                    if(inFromClient.readLine().isEmpty())
                        outToClient.writeBytes("IGP 207 OK\r\n\r\n");
                    //get the numbers of new posts for each group, then send IGP 207 OK with the numbers on one line seperated by space.

                case "RG":
                    int groupID=Integer.parseInt(clientSentence.split(" ")[1]);
                    if(inFromClient.readLine().isEmpty()) {
                        outToClient.writeBytes("IGP 207 OK\r\n\r\n");
                        outToClient.writeBytes("posts subjects\r\n\r\n");
                    }
                    break;
                case"RP":
                    int postID=Integer.parseInt(clientSentence.split(" ")[1]);
                    if(inFromClient.readLine().isEmpty()){
                        outToClient.writeBytes("IGP 207 OK\r\n\r\n");
                        outToClient.writeBytes("post subject\r\n");
                        outToClient.writeBytes("post content\r\n\r\n");
                    }
                    break;
                case"NP":
                    if(inFromClient.readLine().isEmpty()){
                        String subject=inFromClient.readLine();
                        ArrayList<String> postContent=new ArrayList<>();
                        String s=inFromClient.readLine();
                        while (!s.isEmpty()){
                            postContent.add(s);
                            s=inFromClient.readLine();
                        }
                        outToClient.writeBytes("IGP 320 Created\r\n\r\n");
                    }
                    break;
                case "CK":
                    clientSentence=inFromClient.readLine();
                    int[] subscribedGroupIDs=new int[clientSentence.split(" ").length-1];
                    for (int i=1,j=0;i<clientSentence.split(" ").length;i++)
                        subscribedGroupIDs[j++]=Integer.parseInt(clientSentence.split(" ")[i]);
                    String date2=inFromClient.readLine();
                    DateFormat format2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                    lastChecked = format2.parse(date2);
                    boolean newPost=false;
                    if(newPost){
                        outToClient.writeBytes("IGP 250 New Posts\r\n\r\n");
                        outToClient.writeBytes("post subject\r\n");
                        outToClient.writeBytes("post content\r\n\r\n");
                    }
                    else {
                        outToClient.writeBytes("IGP 251 No Update\r\n\r\n");
                    }
                    break;
                default:
                    outToClient.writeBytes("IGP 401 Bad Request\r\n\r\n");
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
