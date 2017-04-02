package com.thed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * Test Program to get Execution Information for a given test case Id , sample
 * response is following
 *
 * ~~~~~~~~~~~~~~~~~~~~ [
 * { "testScheduleId" : 32, "assignmentDate" :
 * 1490770800000, "estimatedTime" : 600, "testerId" : 1,
 * "remoteRepositoryTestcaseId" : 131, "remoteTestcaseId" : 95, "cyclePhaseId" :
 * 10, "defects" : [ ], "lastTestResult" : { "id" : 62, "executionDate" :
 * 1490812485000, "executionStatus" : "1", "testerId" : 1,
 * "releaseTestScheduleId" : 32 }, "attachmentCount" : 0, "actualTime" : 600 } ]
 *
 * <p> Here in the above response testScheduleId is the ID we need to pass to update
 * execution for the given testcase. Remeber there can be many execution belongs
 * to a testcase based on the cycles a test has been appeared. </p>
 * <p> Make sure to update right server URL with user name and password</p>
 */
public class GetExecutionIdForTestCase {

    private static String line = new String();
    private static BufferedReader rd = null;
    private static String encoding;
    private static URL url;

    // update this URL to your actual server URL, last parameter is to get the execution ID for the given test
    static String urlString = "https://tricentis.yourzephyr.com/flex/services/rest/v1/execution?tcrtreetestcase.testcase.id=3";
    static String userPassword = "test.manager:test.manager";

    public static void main(String[] args) throws IOException {
        try {

            url = new URL(urlString);
            encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.setAllowUserInteraction(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.connect();

            System.out.println("~~~~~~~~~~~~~~~~~~~~Received response from API ~~~~~~~~~~~~~~~~~~");
            rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
