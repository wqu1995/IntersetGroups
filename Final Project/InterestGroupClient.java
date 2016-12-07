import java.io.*; // Provides for system input and output through data
// streams, serialization and the file system
import java.net.*; // Provides the classes for implementing networking
import java.util.ArrayList;
import java.util.Date;
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

        //send LOGIN request
        LogIn(outToServer,inFromServer);
        AG(outToServer,inFromServer);
        SG(outToServer,inFromServer);
        RG(outToServer,inFromServer);
        RP(outToServer,inFromServer);
        NP(outToServer,inFromServer);
        CK(outToServer,inFromServer);
        // send the sentence read to the server
        outToServer.writeBytes(sentence + '\n');

        // get the reply from the server
        modifiedSentence = inFromServer.readLine();

        // print the returned sentence
        System.out.println("FROM SERVER: " + modifiedSentence);

        // close the socket
        clientSocket.close();
    }

    private static void CK(DataOutputStream outToServer, BufferedReader inFromServer) throws IOException {
        Date now=new Date();
        outToServer.writeBytes("CK LGP\r\n");
        outToServer.writeBytes("Subscribed-GroupIDs: groupIDs\r\n");
        outToServer.writeBytes("Date: "+now+"\r\n");
        outToServer.writeBytes("\r\n");
        if(inFromServer.readLine().equals("LGP 251 No Update"))
            inFromServer.readLine();
        else if(inFromServer.readLine().equals("LGP 250 New Posts")){
            inFromServer.readLine();
            String subject=inFromServer.readLine();
            ArrayList<String> postContent=new ArrayList<>();
            String s=inFromServer.readLine();
            while (!s.isEmpty()){
                postContent.add(s);
                s=inFromServer.readLine();
            }
        }
    }

    private static void NP(DataOutputStream outToServer, BufferedReader inFromServer) throws IOException {
        outToServer.writeBytes("NP GroupID IGP\r\n\r\n");
        outToServer.writeBytes("post subject\r\n");
        outToServer.writeBytes("post content\r\n\r\n");
        if(inFromServer.readLine().equals("LGP 320 Created"))
            inFromServer.readLine();

    }

    private static void RP(DataOutputStream outToServer, BufferedReader inFromServer) throws IOException {
        outToServer.writeBytes("RP PostID LGP\r\n\r\n");
        if(inFromServer.readLine().equals("LGP 207 OK")){
            inFromServer.readLine();
            String subject=inFromServer.readLine();
            ArrayList<String> postContent=new ArrayList<>();
            String s=inFromServer.readLine();
            while (!s.isEmpty()){
                    postContent.add(s);
                s=inFromServer.readLine();
            }
        }
    }

    private static void RG(DataOutputStream outToServer,BufferedReader inFromServer) throws IOException {
        outToServer.writeBytes("RG GroupID LGP\r\n\r\n");
        if(inFromServer.readLine().equals("LGP 207 OK")){
            inFromServer.readLine();
            ArrayList<String> postSubjects=new ArrayList<>();
            String s=inFromServer.readLine();
            while (!s.isEmpty()){
                for(String s1:s.split(" "))
                    postSubjects.add(s1);
                s=inFromServer.readLine();
            }

        }
    }

    private static void SG(DataOutputStream outToServer,BufferedReader inFromServer) throws IOException {
        outToServer.writeBytes("SG LGP\r\n");
        outToServer.writeBytes("Subscribed-GroupIDs: groupIDs\r\n");
        outToServer.writeBytes("Read-Posts: postsIDs\r\n\r\n");
    }

    private static void AG(DataOutputStream outToServer,BufferedReader inFromServer) throws IOException {
        outToServer.writeBytes("AG  LGP\r\n\r\n");
    }

    private static void LogIn(DataOutputStream outToServer,BufferedReader inFromServer) throws IOException {
        Date now=new Date();
        outToServer.writeBytes("LOGIN LGP\r\n");
        outToServer.writeBytes("Date: "+now+"\r\n");
        outToServer.writeBytes("\r\n");
        if(inFromServer.readLine().equals("LGP 214 No Content"))
            inFromServer.readLine();
    }
}
