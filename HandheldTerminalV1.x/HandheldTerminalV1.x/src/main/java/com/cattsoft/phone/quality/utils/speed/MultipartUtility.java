package com.cattsoft.phone.quality.utils.speed;

import org.apache.http.protocol.HTTP;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Xiaohong on 2014/5/5.
 */
public class MultipartUtility {
    public static final int DEFAULT_CHUNK_LENGTH = 128;
    private static final String CRLF = "\r\n";
    private final String boundary;
    private HttpURLConnection urlConnection;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    private int responseCode;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public MultipartUtility(String requestURL, String charset)
            throws IOException {
        this.charset = charset;

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestURL);
        urlConnection = (HttpURLConnection) url.openConnection();
        // Workarounds for older Android versions who do not do that automatically (2.3.5 for example)
        urlConnection.setRequestProperty(HTTP.TARGET_HOST, url.getHost());
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true); // indicates POST method
        urlConnection.setDoInput(true);
        // 指定流的大小，当内容达到这个值的时候就把流输出
        urlConnection.setChunkedStreamingMode(DEFAULT_CHUNK_LENGTH);
//        urlConnection.setInstanceFollowRedirects(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Connection", "Keep-Alive");
        urlConnection.setRequestProperty("Cache-Control", "no-cache");
        urlConnection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
        urlConnection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(3));
        urlConnection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
//        urlConnection.setRequestProperty("User-Agent", "Java Agent");
//        urlConnection.connect();
        outputStream = urlConnection.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(CRLF);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                CRLF);
        writer.append(CRLF);
        writer.append(value).append(CRLF);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        addFilePartHeader(fieldName, fileName);

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        addFilePartEnd();
    }

    public void addFilePartHeader(String fieldName, String fileName) {
        if (null == fileName)
            fileName = fieldName;
        writer.append("--" + boundary).append(CRLF);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(CRLF);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(CRLF);
        writer.append("Content-Transfer-Encoding: binary").append(CRLF);
        writer.append(CRLF);
        writer.flush();
    }

    public void addFilePartEnd() {
        writer.append(CRLF);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(CRLF);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();

        writer.append(CRLF).flush();
        writer.append("--" + boundary + "--").append(CRLF);
        writer.close();

        // checks server's status code first
        int status = urlConnection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            urlConnection.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public HttpURLConnection getUrlConnection() {
        return urlConnection;
    }
}