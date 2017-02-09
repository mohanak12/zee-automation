package com.thed;


import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Base64;
import java.util.Properties;

/**
 *
 */
public class ZeeRestClient {


//    private static String csvFile = "/Users/Pravin/Documents/dev/zee/zeerestcsvclient/src/main/resources/users.csv";
//    private static String clientProperties = "/Users/Pravin/Documents/dev/zee/zeerestcsvclient/src/main/resources/client.properties";

 //   private static Properties prop = new Properties();

    public static void main(String as[]) throws Exception {
        if (as.length < 4){
            showUsage();
            return;
        }

        //loadProperties();

        List<User> users = CsvParser.parseCsv(as[1]);
        for (User usr : users) {

            HttpClient httpClient = new DefaultHttpClient();
            String url = as[0]+ "/flex/services/rest/v1/user/";

            System.out.println("posting to URL " + url);

            HttpPost postRequest = new HttpPost(url);
            //HttpPost postRequest = new HttpPost("http://localhost:8080/flex/services/rest/v1/user/");

            Gson gson = new Gson();
            StringEntity input = new StringEntity(gson.toJson(usr));

            input.setContentType("application/json");
            postRequest.setHeader("Authorization", getAuthorization(as[2], as[3]));
            postRequest.setEntity(input);

            HttpResponse response = httpClient.execute(postRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            StringBuffer totalOutput = new StringBuffer();
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                totalOutput.append(output);
            }
            System.out.println(totalOutput.toString());
        }
    }


//    private static void loadProperties() {
//        InputStream input = null;
//        try {
//            input = new FileInputStream(clientProperties);
//            prop.load(input);
//        } catch (Exception e) {
//            System.out.println("could not load properties file " + e.getMessage());
//            System.out.println("plz add full qualified file path of client.properties");
//            e.printStackTrace();
//            throw new IllegalArgumentException("incorrect setup");
//        }
//
//    }

    private static String getAuthorization(String userName, String password) {
        String auth = userName + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
        return "Basic " + new String(encodedAuth);
    }

    private static void showUsage() {
        System.out.println("Incorrect input params");
        System.out.println(" ZEE Rest client for user upload");
        System.out.println("\t 1. Server URL ex. " + "https://demo.yourzephyr.com");
        System.out.println("\t 2. CSV file path (absolute) ex./Users/Pravin/resources/users.csv" +
        " Plz get sample csv format from https://github.com/zeedeveloper/zeenodejsclient/blob/master/users.csv");
        System.out.println("\t 3. User ID ex. test.manager");
        System.out.println("\t 4. User password ex. test.manager");
        System.out.println("AN Example call is as following");
        System.out.println("\t java -cp .:/Users/Pravin/Documents/temp/libs/* com.thed.ZeeRestClient " +
                " https://demo.yourzephyr.com /Users/Pravin/Documents/temp/libs/users.csv test.manager test.manager");
    }


}

