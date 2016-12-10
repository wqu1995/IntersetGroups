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
    static ArrayList<String> groups=new ArrayList<>(),
            contents=new ArrayList<>();
    static ArrayList<String> subsGroupsIDs=new ArrayList<>(),subsGroupsNames=new ArrayList<>();
    static ArrayList<String> readPosts=new ArrayList<>(),newPosts=new ArrayList<>();
    static BufferedReader inFromUser;
    static DataOutputStream outToServer;
    static BufferedReader inFromServer;
    static final int N=5;
    public static void main(String argv[]) throws Exception
    {
        String sentence;
        String modifiedSentence;
        boolean logIn=false;

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
        inFromUser =
                new BufferedReader(new InputStreamReader(System.in));

        // create a client socket (TCP)
        Socket clientSocket = new Socket(argv[0], lisPort);

        // create an output stream from the socket output steram
        outToServer =
                new DataOutputStream(clientSocket.getOutputStream());

        // create an input stream from the socket input steram
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // read a line form the standard input
        while (!logIn){
            sentence = inFromUser.readLine();
            if(sentence.split(" ").length!=2||!sentence.split(" ")[0].equals("login"))
                System.out.println("Invalid command, you have not logged in yet.");
            else if(sentence.split(" ")[0].equals("login")) {
                // read user info and store them into two sets.
                LogIn(sentence.split(" ")[1]);
                // send the AG IGP request and store all the groups' info in groups array
                AG();
                logIn=true;
                System.out.println("Welcome, "+sentence.split(" ")[1]+". Please enter one of the commands:" +
                        " \"ag\",\"sg\",\"rg\",\"help\",or \"logout\"");
            }
        }

        while(logIn){
            //send LOGIN request
            sentence=inFromUser.readLine();
            switch (sentence.split(" ")[0]){
                case "ag":
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
                    for (String s:subsGroupsIDs){
                        if(!subsGroupsNames.contains(groups.get(Integer.parseInt(s)-1)))
                            subsGroupsNames.add(groups.get(Integer.parseInt(s)-1));
                    }
                    // send the SG IGP request
                    SG();
                    if(sentence.split(" ").length==1)
                        sg(N);// take user to ag interface
                    else if(sentence.split(" ").length==2) {
                        int n=Integer.parseInt(sentence.split(" ")[1]);
                        sg(n);
                    }
                    else
                        System.out.println("Invalid command.");
                    break;
                case "rg":
                    RG();// send the RG IGP request
                    break;
                case "logout":
                    logIn=false;
                    break;
                default:
                    System.out.println("Invalid Command.");
            }


            RP();
            NP();
            CK();
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

    private static void sg(int n) throws IOException {
        boolean showedAll=false,sg=true,wrongIndex=false;
        int i,currentN=n;
        System.out.println("Subscribed Groups. Please enter one of the subcommands: u,n,q");
        //Check if there is new post from subscribed groups.
        String sTemp=CK();
        if(sTemp!=null)
            System.out.println(sTemp);
        for( i=1;i<=currentN;i++){
            if(subsGroupsIDs.size()==i-1){
                showedAll=true;
                break;
            }
            if(Integer.parseInt(newPosts.get(i-1))==0)
                System.out.println(i+"         "+subsGroupsNames.get(i-1));
            else
                System.out.println(i+"    "+newPosts.get(i-1)+"   "+subsGroupsNames.get(i-1));
        }
        while (sg){
            String command=inFromUser.readLine();
            switch (command.split(" ")[0].toLowerCase()){
                case "u":
                    try {
                        ArrayList temporaryGroups=new ArrayList();
                        for(String s:command.substring(2,command.length()).split(" ")) {
                            if(Integer.parseInt(s)>i||Integer.parseInt(s)<=i-currentN){
                                System.out.println("group not in the list!");
                                wrongIndex=true;
                                break;
                            }
                            temporaryGroups.add(Integer.parseInt(s));
                        }
                        if(wrongIndex) {
                            temporaryGroups.clear();
                            wrongIndex=false;
                            break;
                        }
                        // If group indices are valid, add it to user file.
                        for(Object s:temporaryGroups) {
                            newPosts.remove((int)(s)-1);
                            String groupToBeRemoved=subsGroupsNames.remove((int)(s)-1);
                            int IDtobeRemoved=groups.indexOf(groupToBeRemoved)+1;
                            subsGroupsIDs.remove(IDtobeRemoved+"");
                        }
                        System.out.println("You have successfully unsubscribed these groups. Please enter next command.");
                        for( int j=i-currentN+1;j<=i;j++){
                            if(subsGroupsIDs.size()==j-1){
                                showedAll=true;
                                break;
                            }
                            if(Integer.parseInt(newPosts.get(j-1))==0)
                                System.out.println(j+"         "+subsGroupsNames.get(j-1));
                            else
                                System.out.println(j+"    "+newPosts.get(j-1)+"   "+subsGroupsNames.get(j-1));
                        }
                    }
                    catch (Exception e){
                        System.out.println("Invalid command.");
                        break;
                    }
                    break;
                case "n":
                    sTemp=CK();
                    if(sTemp!=null)
                        System.out.println(sTemp);
                    if(showedAll) {
                        sg = false;
                        break;
                    }
                    System.out.println("Subscribed Groups. Please enter one of the subcommands: u,n,q");
                    for( int j=i+1;j<=currentN+i;j++){
                        if(subsGroupsIDs.size()==j-1){
                            showedAll=true;
                            break;
                        }
                        if(Integer.parseInt(newPosts.get(j-1))==0)
                            System.out.println(j+"         "+subsGroupsNames.get(j-1));
                        else
                            System.out.println(j+"    "+newPosts.get(j-1)+"   "+subsGroupsNames.get(j-1));
                    }
                    i=i+currentN;
                    break;
                case "q":
                    sg=false;
                    break;
                default:
                    System.out.println("Invalid command.");break;
            }
        }
        System.out.println("Exit Subscribed groups. Please enter one of the commands: \"ag\",\"sg\",\"rg\",\"help\",or \"logout\"");
    }

    private static void ag(int n) throws IOException {
        boolean showedAll=false,ag=true;boolean wrongIndex=false;
        int i,currentN=n;
        ArrayList<String> temporaryGroups=new ArrayList();
        System.out.println("All Groups. Please enter one of the subcommands: s,u,n,q");
        //Check if there is new post from subscribed groups.
        String sTemp=CK();
        if(sTemp!=null)
            System.out.println(sTemp);
        for( i=1;i<=currentN;i++){
            if(groups.size()==i-1){
                showedAll=true;
                break;
            }
            if(subsGroupsIDs.contains(i+""))
                System.out.println(i+". (s) "+groups.get(i-1));
            else
                System.out.println(i+". ( ) "+groups.get(i-1));
        }
        while (ag){
            String command=inFromUser.readLine();
            switch (command.split(" ")[0].toLowerCase()){
                case "s":
                    try {

                        for(String s:command.substring(2,command.length()).split(" ")) {
                            if(Integer.parseInt(s)>i||Integer.parseInt(s)<=i-currentN){
                                System.out.println("group not in the list!");
                                wrongIndex=true;
                                break;
                            }
                            temporaryGroups.add(s);
                        }
                        if(wrongIndex) {
                            temporaryGroups.clear();
                            wrongIndex=false;
                            break;
                        }
                        // If group indices are valid, add it to user file.
                        for(String s:temporaryGroups) {
                            if(subsGroupsIDs.contains(s)){
                                continue;
                            }
                            subsGroupsIDs.add(s);
                        }
                        temporaryGroups.clear();
                        System.out.println("You have successfully subscribed these groups. Please enter next command.");
                    }
                    catch (Exception e){
                        System.out.println("Invalid command.");
                        break;
                    }
                    break;
                case "u":
                    try {
                        for(String s:command.substring(2,command.length()).split(" ")) {
                            if(Integer.parseInt(s)>i||Integer.parseInt(s)<=i-currentN){
                                System.out.println("group not in the list!");
                                wrongIndex=true;
                                break;
                            }
                            temporaryGroups.add(s);
                        }
                        if(wrongIndex) {
                            temporaryGroups.clear();
                            wrongIndex=false;
                            break;
                        }
                        // If group indices are valid, add it to user file.
                        for(String s:temporaryGroups) {
                            if(!subsGroupsIDs.contains(s)){
                                continue;
                            }
                            subsGroupsIDs.remove(s);
                        }
                        temporaryGroups.clear();
                        System.out.println("You have successfully unsubscribed these groups. Please enter next command.");
                    }
                    catch (Exception e){
                        System.out.println("Invalid command.");
                        break;
                    }
                    break;
                case "n":
                    sTemp=CK();
                    if(sTemp!=null)
                        System.out.println(sTemp);
                    if(showedAll) {
                        ag = false;
                        break;
                    }
                    System.out.println("All Groups. Please enter one of the subcommands: s,u,n,q");
                    for( int j=i+1;j<=currentN+i;j++){
                        if(groups.size()==j-1){
                            showedAll=true;
                            break;
                        }
                        if(subsGroupsIDs.contains(j+""))
                            System.out.println(j+". (s) "+groups.get(j-1));
                        else
                            System.out.println(j+". ( ) "+groups.get(j-1));
                    }
                    i=i+currentN;
                    break;
                case "q":
                    ag=false;
                    break;
                default:
                    System.out.println("Invalid command.");break;
            }
        }
        System.out.println("Exit All groups. Please enter one of the commands: \"ag\",\"sg\",\"rg\",\"help\",or \"logout\"");
    }

    private static String CK() throws IOException {
        Date now=new Date();
        outToServer.writeBytes("CK IGP\r\n");
        String subg="";
        for (String s:subsGroupsIDs)
            subg=subg+s+" ";
        outToServer.writeBytes("Subscribed-GroupIDs: "+subg+"\r\n");
        outToServer.writeBytes("Date: "+now+"\r\n\r\n");
        if(inFromServer.readLine().equals("IGP 251 No Update")) {
            inFromServer.readLine();
            return null;
        }
        else if(inFromServer.readLine().equals("IGP 250 New Posts")){
            inFromServer.readLine();
            String subject=inFromServer.readLine();
            String group=inFromServer.readLine();
            return "There is a new post: "+subject+" in group: "+group;
        }
        return null;
    }

    private static void NP() throws IOException {
        outToServer.writeBytes("NP GroupID IGP\r\n\r\n");
        outToServer.writeBytes("post subject\r\n");
        outToServer.writeBytes("post content\r\n\r\n");
        if(inFromServer.readLine().equals("IGP 320 Created"))
            inFromServer.readLine();

    }

    private static void RP() throws IOException {
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

    private static void RG() throws IOException {
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

    private static void SG() throws IOException {
        outToServer.writeBytes("SG IGP\r\n");
        String subg="";
        for(String s:subsGroupsIDs)
            subg=subg+s+" ";
        outToServer.writeBytes("Subscribed-GroupIDs: "+subg+"\r\n");
        String readP="";
        for(String s:readPosts)
            readP=readP+s+" ";
        outToServer.writeBytes("Read-Posts: "+readP+"\r\n\r\n");
        if(inFromServer.readLine().equals("IGP 207 OK")){
            inFromServer.readLine();
            String newP=inFromServer.readLine();
            for (String s:newP.split(" ")){
                newPosts.add(s);
            }
            inFromServer.readLine();
        }
    }

    private static void AG() throws IOException {
        outToServer.writeBytes("AG  IGP\r\n\r\n");
        String s;
        if(inFromServer.readLine().equals("IGP 310 All Groups")){
            inFromServer.readLine();
            s=inFromServer.readLine();
            while (!s.isEmpty()){
                groups.add(s);
                s=inFromServer.readLine();
            }
        }
    }

    private static void LogIn(String userID)
            throws IOException {
        try {
            BufferedReader reader=new BufferedReader(new FileReader(userID+".txt"));
            String s1=reader.readLine();
            for (String s:s1.split(" "))
                subsGroupsIDs.add(s);
            String s2=reader.readLine();
            for (String s:s2.split(" "))
                readPosts.add(s);
        }
        catch (FileNotFoundException e){
            PrintWriter writer = new PrintWriter(userID+".txt", "UTF-8");
            writer.println("");
            writer.println("");
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
