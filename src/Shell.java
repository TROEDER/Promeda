import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Shell {

	public static void main(String[] args) {

		Shell obj = new Shell();

		String domainName = "google.com";
		
		//in mac oxs
		//String command = "ping -c 3 " + domainName;
		
		//in windows
		//String command = "ping -n 3 " + domainName;
		String command = "npm install gulp@latest";
		String output = obj.executeCommand(command);

		System.out.println(output);

	}

	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

}
