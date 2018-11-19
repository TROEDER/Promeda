import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Run {
    
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//String destFolder="//SVR-APP-11/EC/Promeda.local-working-dir";
		String destFolder="C:\\Web\\htdocs\\Promeda-bin";
        /*
        *  Location where the Nodejs Project is Present
        */
        System.out.println(destFolder);

        String cmdPrompt="cmd";
        String path="/c";
        String npm = isWindows() ? "npm.cmd" : "npm";
        String npmUpdate= npm + " run jpegtran";

        File jsFile=new File(destFolder);
        List<String> updateCommand=new ArrayList<String>();
        updateCommand.add(cmdPrompt);
        updateCommand.add(path);
        //updateCommand.add("gulp");
        updateCommand.add(npmUpdate);
        //updateCommand.add("jpegtran");
        runExecution(updateCommand,jsFile);

    }
	
    public static void runExecution(List<String> command, File navigatePath) throws IOException, InterruptedException {

        System.out.println(command);

        ProcessBuilder executeProcess=new ProcessBuilder(command);
        executeProcess.directory(navigatePath);
        Process resultExecution=executeProcess.start();
        BufferedReader br=new BufferedReader(new InputStreamReader(resultExecution.getInputStream()));
        StringBuffer sb=new StringBuffer();

        String line;
        while((line=br.readLine())!=null) {
            sb.append(line+System.getProperty("line.separator"));
            System.out.println(sb.toString());
        }
        br.close();
        int resultStatust=resultExecution.waitFor();
        System.out.println("Result of Execution"+(resultStatust==0?"\tSuccess":"\tFailure"));
    }
    
    static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

}