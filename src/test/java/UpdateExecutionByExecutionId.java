import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Update execution for given testcase ID
 * 2 = fail, 1= pass and 3= WIP
 *
 */

public class UpdateExecutionByExecutionId {

    private static String line = new String();
    private  static BufferedReader rd = null;
    private static String encoding;
    private  static URL url;

    // update this URL to your actual server URL, last parameter is to get the execution ID for the given test
    static String urlString = "http://localhost:8081/flex/services/rest/v1/execution/";
    private static String cyclePhaseId = "4";
    static String userPassword = "test.manager:test.manager";


    public static void main(String[] args) throws IOException {
        try {
            // retrieving data from server
            url = new URL(urlString + cyclePhaseId);
            // 2 = fail, 1= pass and 3= WIP
            String payload = "{\"lastTestResult\":{ \"executionStatus\": \"1\"}}";

            encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setDefaultUseCaches(false);
            urlConnection.setAllowUserInteraction(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Authorization", "Basic " + encoding);
            urlConnection.connect();
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(payload);
            writer.close();

            System.out.println("~~~~~~~~~Requesting Status Update ~~~~~~~~~~~");
            rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            while ((line = rd.readLine()) != null){
                System.out.println(line);
            }
            System.out.println("~~~~~~~~~~Status Successfully Updated ~~~~~~~for execution id : "  + cyclePhaseId);

            urlConnection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
