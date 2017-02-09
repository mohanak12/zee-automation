import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Upload document to execution , system will internally create testcase / test steps / executions.
 */
public class DocumentUploadWithNoIds {

    static String userName ;
    static String userPassword ;
    static String serverUrl ;

    public static void main(String asd[]) throws IOException {


        String asp[] = new String[5];
        asp[0] = serverUrl = "http://localhost:8081";
        asp[2] = userName = "test.manager";
        asp[3] = userPassword = "test.manager";
        asp[4] = "/Users/Pravin/Desktop/abc.pdf";  //file to upload path

        HttpClient httpClient = new DefaultHttpClient();
        Long projectId = createProject(httpClient);
        Long releaseId = createRelease(projectId, httpClient);

        Long testcaseTreeId = createTestcaseTree(releaseId, httpClient);
        Long testcase = createTestcase(testcaseTreeId, releaseId, httpClient);
        Long cycleId = createCycle(releaseId, httpClient);

        //uploadAttachment(url, asp[2], asp[3], asp[4], httpClient);

    }

    //create testcase tree
    private static long createProject(HttpClient httpClient) throws IOException {
        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/project");
        Project project = new Project().setName("1-Automation-Project").setStartDate(System.currentTimeMillis());
        return getObjectId(project, postRequest, httpClient);
    }

    //create release
    private static long createRelease(Long projectId,
                                      HttpClient httpClient) throws IOException {
        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/release");
        Release release = new Release().setProjectId(projectId)
                .setName("Release-1.0").setStartDate(System.currentTimeMillis()).setStatus(0);
        return getObjectId(release, postRequest, httpClient);
    }

    //create testcase tree
    private static long createTestcaseTree(Long releaseId,
                                           HttpClient httpClient) throws IOException {
        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/testcasetree");
        TestcaseTree testcaseTree = new TestcaseTree().setName("Automation-Tree")
                .setReleaseId(releaseId).setType("Phase");
        return getObjectId(testcaseTree, postRequest, httpClient);
    }

    //create testcase
    private static long createTestcase(Long testcaseTreeId, Long releaseId, HttpClient httpClient) throws IOException {
        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/testcase");

        TCRCatalogTreeTestcase testcaseTree = new TCRCatalogTreeTestcase().setTcrCatalogTreeId(testcaseTreeId)
                .setTestcase(new Testcase()
                        .setName("Automation-Testcase").setAutomated(false).setReleaseId(releaseId));

        return getObjectId(testcaseTree, postRequest, httpClient);

    }

    private static long createCycle(Long releaseId, HttpClient httpClient) throws IOException {
        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/cycle");
        Cycle cycle = new Cycle().setName("Automation-Cycle").setReleaseId(releaseId)
                .setStartDate(System.currentTimeMillis()).setEndDate(System.currentTimeMillis());
        return getObjectId(cycle, postRequest, httpClient);

    }


    /**
    private static long updateCyclePhase(Long cycleId, Long testcaseTreeId, Long releaseId, HttpClient httpClient) throws IOException {

        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/cycle/" + cycleId + "/phase");
        Cycle cycle = new Cycle().setName("Automation-Cycle").setReleaseId(releaseId)
                .setStartDate(System.currentTimeMillis()).setEndDate(System.currentTimeMillis());
        return getObjectId(cycle, postRequest, httpClient);

    }

    private static long createExecution() throws IOException {

        HttpPost postRequest = new HttpPost(serverUrl + "/flex/services/rest/latest/cycle");
        Cycle cycle = new Cycle().setName("Automation-Cycle").setReleaseId(releaseId)
                .setStartDate(System.currentTimeMillis()).setEndDate(System.currentTimeMillis());
        return getObjectId(cycle, postRequest, httpClient);


    }
     **/



    private static <X extends IJson> Long getObjectId(X obj,
                                                      HttpPost postRequest, HttpClient httpClient) throws IOException {
        Gson gson = new Gson();
        StringEntity input = new StringEntity(gson.toJson(obj));

        input.setContentType("application/json");
        postRequest.setHeader("Authorization", getAuthorization(userName, userPassword));
        postRequest.setEntity(input);

        HttpResponse response = httpClient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            throw new IllegalStateException("Could not create " + obj.getClass() + " exiting");
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));
        String output;
        StringBuffer totalOutput = new StringBuffer();

        while ((output = br.readLine()) != null) {
            System.out.println(output);
            totalOutput.append(output);
        }
        X responseProject = (X) gson.fromJson(totalOutput.toString() , obj.getClass());
        System.out.println( obj.getClass() + " created successfully with id " + responseProject.getId());
        return responseProject.getId();
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



    interface IJson {
        Long getId();
    }

    static class TCRCatalogTreeTestcase implements IJson, Serializable {
        long tcrCatalogTreeId;
        int revision;
        boolean original;
        int stateFlag;
        Testcase testcase;
        Long id;

        public TCRCatalogTreeTestcase setTcrCatalogTreeId(long tcrCatalogTreeId) {
            this.tcrCatalogTreeId = tcrCatalogTreeId;
            return this;
        }

        public void setRevision(int revision) {
            this.revision = revision;
        }

        public void setOriginal(boolean original) {
            this.original = original;
        }

        public void setStateFlag(int stateFlag) {
            this.stateFlag = stateFlag;
        }

        public TCRCatalogTreeTestcase setTestcase(Testcase testcase) {
            this.testcase = testcase;
            return this;
        }

        public Long getId() {
            return id;
        }
    }

    private static class Project implements IJson, Serializable {
        String name;
        long startDate;
        Long id;

        public Long getId() {
            return id;
        }
        Project setStartDate(long startDate) {
            this.startDate = startDate;
            return this;
        }
        Project setName(String name) {
            this.name = name;
            return this;
        }
    }

    private static class Release implements IJson, Serializable {
        String name;
        Long projectId;
        Long id;

        public Release setStatus(Integer status) {
            this.status = status;
            return this;
        }

        Integer status;

        public long getStartDate() {
            return startDate;
        }

        public Release setStartDate(long startDate) {
            this.startDate = startDate;
            return this;
        }

        long startDate;

        public Release setProjectId(long projectId) {
            this.projectId = projectId;
            return this;
        }

        public Long getId() {
            return id;
        }

        Release setName(String name) {
            this.name = name;
            return this;
        }
    }

    private static class TestcaseTree implements IJson,  Serializable {
        String name;
        Long releaseId;
        Long id;
        String type;

        public Long getId() {
            return id;
        }
        TestcaseTree setReleaseId(Long releaseId) {
            this.releaseId = releaseId;
            return this;
        }

        TestcaseTree setName(String name) {
            this.name = name;
            return this;
        }

         TestcaseTree setType(String type) {
            this.type = type;
            return this;
        }
    }

    private static class Cycle implements IJson,  Serializable {
        String name;
        Long releaseId;
        Long id;
        long startDate;
        long endDate;

        public Cycle setStartDate(long startDate) {
            this.startDate = startDate;
            return this;
        }

        public Cycle setEndDate(long endDate) {
            this.endDate = endDate;
            return this;
        }

        public Long getId() {
            return id;
        }
        Cycle setReleaseId(Long releaseId) {
            this.releaseId = releaseId;
            return this;
        }

        Cycle setName(String name) {
            this.name = name;
            return this;
        }

    }

    private static class CyclePhase implements IJson,  Serializable {
        Long releaseId;
        Long tcrCatalogTreeId;
        Long id;
        long startDate;
        long endDate;

        public CyclePhase setStartDate(long startDate) {
            this.startDate = startDate;
            return this;
        }

        public CyclePhase setEndDate(long endDate) {
            this.endDate = endDate;
            return this;
        }

        public Long getId() {
            return id;
        }
        CyclePhase setReleaseId(Long releaseId) {
            this.releaseId = releaseId;
            return this;
        }



    }

    private static class Testcase implements IJson, Serializable {
        String name;
        boolean automated;
        Long releaseId;
        Long id;

        Testcase setName(String name) {
            this.name = name;
            return this;
        }

        Testcase setAutomated(boolean automated) {
            this.automated = automated;
            return this;
        }

        Testcase setReleaseId(Long releaseId) {
            this.releaseId = releaseId;
            return this;
        }

        public Long getId() {
            return id;
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
