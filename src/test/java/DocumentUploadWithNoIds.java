import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.ws.security.util.Base64;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * Upload document to execution , system will internally create testcase / test steps / executions.
 */
public class DocumentUploadWithNoIds {

    public static void main(String asd[]) throws IOException {


        String asp[] = new String[5];
        asp[0] = "http://localhost:8081";
        asp[2] = "test.manager";
        asp[3] = "test.manager";
        asp[4] = "/Users/Pravin/Desktop/abc.pdf";  //file to upload path
        HttpClient httpClient = new DefaultHttpClient();

        createProject(asp[0], asp[2], asp[3], httpClient);
        //createTestcaseTree(asp[0], asp[2], asp[3], httpClient);
        //uploadAttachment(url, asp[2], asp[3], asp[4], httpClient);


    }


    //create testcase tree
    private static void createTestcaseTree(String serverUrl, String userName, String userPassword,
                                           HttpClient httpClient) throws IOException {
        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/testcasetree");
        Gson gson = new Gson();
        StringEntity input = new StringEntity(gson.toJson(new TestcaseTree().setName("Automation-Tree").setReleaseId(1l)));

        input.setContentType("application/json");
        postRequest.setHeader("Authorization", getAuthorization(userName, userPassword));
        postRequest.setEntity(input);

        HttpResponse response = httpClient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));

        String output;
        StringBuffer totalOutput = new StringBuffer();
        System.out.println("Testcase tree creation Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            totalOutput.append(output);
        }
        System.out.println(totalOutput.toString());
        System.out.println("Testcase tree created successfully .... \n");
    }


    //create testcase tree
    private static void createProject(String serverUrl, String userName, String userPassword,
                                           HttpClient httpClient) throws IOException {
        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/project");
        Gson gson = new Gson();
        StringEntity input = new StringEntity(gson.toJson(new Project().
                setName("Automation-Project").setStartDate(new Date())));

        input.setContentType("application/json");
        postRequest.setHeader("Authorization", getAuthorization(userName, userPassword));
        postRequest.setEntity(input);

        HttpResponse response = httpClient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            throw new IllegalStateException("Could not create project exiting");
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));

        String output;
        StringBuffer totalOutput = new StringBuffer();
        System.out.println("Project creation Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            totalOutput.append(output);
        }
        System.out.println(totalOutput.toString());
        System.out.println("Project created successfully .... \n");
    }


    //create testcase
    private static void createTestcase(String url, String userName, String userPassword,
                                       String filePath, HttpClient httpClient) throws IOException {


    }


    private static void createTestCycleAndPhase() throws IOException {

    }

    private static void createExecution() throws IOException {

    }


    private static void uploadAttachment(String serverUrl, String userName, String userPassword,
                                         String filePath, HttpClient httpClient) throws IOException {

        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/attachment");
        //HttpPost postRequest = new HttpPost("http://localhost:8080/flex/services/rest/v1/user/");

        Gson gson = new Gson();
        DocumentUploadTest.Attachment attachment = new DocumentUploadTest.Attachment();

        attachment.setName("test-file");
        attachment.setItemType("testcase");
        attachment.setItemId(50l);
        attachment.setTempPath(filePath);
        attachment.setDescription("Custom Report");
        attachment.setCreatedBy(1l);
        //attachment.setTimeStamp(new Date(System.currentTimeMillis()));

        StringEntity input = new StringEntity(gson.toJson(attachment));

        input.setContentType("application/json");
        postRequest.setHeader("Authorization", getAuthorization(userName, userPassword));
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


    private static File createTempFile() throws IOException {
        File tempFile = File.createTempFile("someFile" + System.nanoTime(), "txt");
        //tempFile.deleteOnExit();
        return tempFile;
    }


    /**
     * public void createAttachment() throws IOException {
     * <p>
     * HttpHeaders headers = createHeaders("test.manager", "test.manager");
     * //setMessageConverter();
     * ResponseEntity<Attachment> attResponce = restTemplate.exchange(getLatestRestUrl() + "attachment/1",
     * HttpMethod.GET, new HttpEntity(headers), Attachment.class);
     * Attachment att = attResponce.getBody();
     * //id needs to be 0 for it to be treated as new entity
     * att.setId(0L);
     * att.setRefId(null);
     * att.setName("new");
     * //att.setTempPath(createTempFile().getAbsolutePath());
     * <p>
     * HttpEntity<Attachment> entity = new HttpEntity<Attachment>(att, headers);
     * //jsonGenerator.generateJsonRequest("attachment", "createAttachment", att);
     * ResponseEntity<Attachment> attResponce1 = restTemplate.exchange(getLatestRestUrl() + "attachment/",
     * HttpMethod.POST, entity, Attachment.class);
     * Attachment att1 = attResponce1.getBody();
     * //jsonGenerator.generateJsonRequest("attachment", "createAttachment", att1);
     * //Assert.assertNotNull(att);
     * }
     **/



    private static String getAuthorization(String userName, String password) {
        String auth = userName + ":" + password;
        byte[] encodedAuth = java.util.Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
        return "Basic " + new String(encodedAuth);
    }

    private TCRCatalogTreeTestcase getTestCase() {
        TCRCatalogTreeTestcase TCRObj = new TCRCatalogTreeTestcase();
        TCRObj.setTcrCatalogTreeId(6L);
        TCRObj.setRevision(1);
        TCRObj.setLastModifiedOn(new Date());
        TCRObj.setOriginal(true);
        TCRObj.setStateFlag(0);
        Testcase TCR = new Testcase();
        TCR.setName("name");
        TCR.setAutomated(false);
        TCR.setReleaseId(1L);
        TCRObj.setTestcase(TCR);
        return TCRObj;

    }

    static class TCRCatalogTreeTestcase implements Serializable {
        long tcrCatalogTreeId;
        int revision;
        Date lastModifiedOn;
        boolean original;
        int stateFlag;
        Testcase testcase;

        public void setTcrCatalogTreeId(long tcrCatalogTreeId) {
            this.tcrCatalogTreeId = tcrCatalogTreeId;
        }

        public void setRevision(int revision) {
            this.revision = revision;
        }

        public void setLastModifiedOn(Date lastModifiedOn) {
            this.lastModifiedOn = lastModifiedOn;
        }

        public void setOriginal(boolean original) {
            this.original = original;
        }

        public void setStateFlag(int stateFlag) {
            this.stateFlag = stateFlag;
        }

        public void setTestcase(Testcase testcase) {
            this.testcase = testcase;
        }
    }

    private static class Project implements Serializable {
        String name;
        Date startDate;

        public Project setStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }
        Project setName(String name) {
            this.name = name;
            return this;
        }
    }

    private static class TestcaseTree implements Serializable {
        String name;
        Long releaseId;

        TestcaseTree setReleaseId(Long releaseId) {
            this.releaseId = releaseId;
            return this;
        }

        TestcaseTree setName(String name) {
            this.name = name;
            return this;
        }
    }

    private static class Testcase implements Serializable {
        String name;
        boolean automated;
        Long releaseId;

        void setName(String name) {
            this.name = name;
        }

        void setAutomated(boolean automated) {
            this.automated = automated;
        }

        void setReleaseId(Long releaseId) {
            this.releaseId = releaseId;
        }
    }


    public static class Attachment implements Serializable {

        private static final long serialVersionUID = 1562771272242144958L;
        private long id;
        private String refId;
        private String name;
        private String description;
        private Date timeStamp;
        private Long itemId;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(Date timeStamp) {
            this.timeStamp = timeStamp;
        }

        public Long getItemId() {
            return itemId;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public Long getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Long createdBy) {
            this.createdBy = createdBy;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getTempPath() {
            return tempPath;
        }

        public void setTempPath(String tempPath) {
            this.tempPath = tempPath;
        }

        private String itemType;
        private Long createdBy;
        private String contentType;
        private String fileSize;
        private String tempPath;


    }

}
