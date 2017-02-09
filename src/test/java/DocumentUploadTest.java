import com.google.gson.*;
import com.sun.xml.internal.ws.api.message.Attachment;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * upload a document to ZEE
 * supported itemType
 * 1. testcase, releaseTestSchedule, requirement
 * item id is the id of object
 */
public class DocumentUploadTest {

    public static void main(String asd[]) throws IOException {


        String asp[] = new String[4];
        //loadProperties();
        asp[0] = "http://52.74.143.162";
        asp[2] = "test.manager";
        asp[3] = "test.manager";
        String fileToUploadPath = "/Users/Pravin/Downloads/94NJP6TALX.txt";

        String fileServerUrl = asp[0] +"/flex/upload/document/genericattachment";
        String attachmentServerUrl = asp[0] + "/flex/services/rest/latest/attachment";

        HttpClient httpClient = new DefaultHttpClient();

        String serverFilePath = getServerFilePath(fileToUploadPath, fileServerUrl);
        if (serverFilePath == null){
            throw new IllegalStateException("Couldn't save file on server file : " + fileToUploadPath);
        }

        HttpPost postRequest = new HttpPost(attachmentServerUrl);
        Gson gson = new Gson();
        Attachment attachment = new Attachment();
        attachment.setName("test-file");
        attachment.setItemType("testcase");
        attachment.setItemId(50l);
        attachment.setTempPath(serverFilePath);
        attachment.setDescription("Custom Report");
        attachment.setCreatedBy(1l);

        StringEntity input = new StringEntity(gson.toJson(attachment));

        input.setContentType("application/json");
        postRequest.setHeader("Authorization", getAuthorization(asp[2], asp[3]));
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


    private static String getServerFilePath(String filePath, String serverUrl) throws IOException {
        File dir = new File("test");
        dir = new File(dir, "data");
        File file = new File(filePath);

        PostMethod filePost = new PostMethod(serverUrl);

        Part[] parts = {new FilePart(file.getName(), file)};
        filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

        int status = client.executeMethod(filePost);

        String returnedPath = null;
        if (status == HttpStatus.SC_OK) {
            String str = filePost.getResponseBodyAsString();
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = (JsonArray) jsonParser.parse(str);
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement element = jsonArray.get(i);
                JsonObject jsonObject = element.getAsJsonObject();
                returnedPath = jsonObject.getAsJsonPrimitive("file").getAsString();
                System.out.println("file server path received as =" + returnedPath);
            }
        } else {
            System.out.println("Upload failed, response=" + HttpStatus.getStatusText(status));
        }
        return returnedPath;
    }


    private static String getAuthorization(String userName, String password) {
        String auth = userName + ":" + password;
        byte[] encodedAuth = java.util.Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
        return "Basic " + new String(encodedAuth);
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
