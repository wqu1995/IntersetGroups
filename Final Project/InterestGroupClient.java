import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

// streams, serialization and the file system
// applications

/**
 * Created by Yuege on 12/6/2016.
 */
public class InterestGroupClient {
    public static void main(String argv[]) throws Exception
    {
        String sentence;
        String modifiedSentence;
        boolean logIn=false;
        final int N=5;
        /*URL         workDirURL  = InterestGroupClient.class.getClassLoader().getResource("");
        File userFile=new File(workDirURL.getFile());
        userFile=new File(userFile.toString()+File.separator+"user5"+".json");
        if(!userFile.exists())
            userFile.createNewFile();*/

        /*List<String> lines = Arrays.asList("The first line12", "The second line2");
        Path file = Paths.get("the-file-name1.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));*/

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
        while (!logIn){
            sentence = inFromUser.readLine();
            if(sentence.split(" ").length!=2||!sentence.split(" ")[0].equals("login"))
                System.out.println("Invalid command, you have not logged in yet.");
            if(sentence.split(" ")[0].equals("login")) {
                LogIn(outToServer, inFromServer,sentence.split(" ")[1]);
                logIn=true;
            }
        }

        while(logIn){
            //send LOGIN request
            sentence=inFromUser.readLine();
            switch (sentence.split(" ")[0]){
                case "ag":
                    AG(outToServer,inFromServer);// send the AG IGP request
                    if(sentence.split(" ").length==1)
                        ag(N);// take user to ag interface
                    else if(sentence.split(" ").length==2) {
                        int n=Integer.parseInt(sentence.split(" ")[1]);
                        ag(n);
                    }
                    else
                        System.out.println("Invalid command.");
                    break;
                case "sg":
                    SG(outToServer,inFromServer);// send the SG IGP request
                    break;
                case "rg":
                    RG(outToServer,inFromServer);// send the RG IGP request
                    break;
                case "logout":
                    logIn=false;
                    break;
                default:
                    System.out.println("Invalid Command.");
            }



            RP(outToServer,inFromServer);
            NP(outToServer,inFromServer);
            CK(outToServer,inFromServer);
        }

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

    private static void ag(int n) {
        for(int i=1;i<=n;i++){
            System.out.println(i+". ( ) groupname");
        }
    }

    private static void CK(DataOutputStream outToServer, BufferedReader inFromServer) throws IOException {
        Date now=new Date();
        outToServer.writeBytes("CK IGP\r\n");
        outToServer.writeBytes("Subscribed-GroupIDs: groupIDs\r\n");
        outToServer.writeBytes("Date: "+now+"\r\n");
        outToServer.writeBytes("\r\n");
        if(inFromServer.readLine().equals("IGP 251 No Update"))
            inFromServer.readLine();
        else if(inFromServer.readLine().equals("IGP 250 New Posts")){
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
        if(inFromServer.readLine().equals("IGP 320 Created"))
            inFromServer.readLine();

    }

    private static void RP(DataOutputStream outToServer, BufferedReader inFromServer) throws IOException {
        outToServer.writeBytes("RP PostID IGP\r\n\r\n");
        if(inFromServer.readLine().equals("IGP 207 OK")){
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
        outToServer.writeBytes("RG GroupID IGP\r\n\r\n");
        if(inFromServer.readLine().equals("IGP 207 OK")){
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
        outToServer.writeBytes("SG IGP\r\n");
        outToServer.writeBytes("Subscribed-GroupIDs: groupIDs\r\n");
        outToServer.writeBytes("Read-Posts: postsIDs\r\n\r\n");
    }

    private static void AG(DataOutputStream outToServer,BufferedReader inFromServer) throws IOException {
        outToServer.writeBytes("AG  IGP\r\n\r\n");
    }

    private static void LogIn(DataOutputStream outToServer,BufferedReader inFromServer,String userID) throws IOException {
        try {
            BufferedReader reader=new BufferedReader(new FileReader(userID+".txt"));
            String s1=reader.readLine();
            String s2=reader.readLine();
            System.out.println(s1+"\r\n"+s2);
        }
        catch (FileNotFoundException e){
            PrintWriter writer = new PrintWriter(userID+".txt", "UTF-8");
            writer.println("SubscribedGroupIDs: ");
            writer.println("ReadPostIDs: ");
            writer.close();
        }

        Date now=new Date();
        outToServer.writeBytes("LOGIN IGP\r\n");
        outToServer.writeBytes("Date: "+now+"\r\n");
        outToServer.writeBytes("\r\n");
        if(inFromServer.readLine().equals("IGP 214 No Content"))
            inFromServer.readLine();
    }
}
