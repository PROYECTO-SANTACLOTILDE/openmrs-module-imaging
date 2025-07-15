package org.openmrs.module.imaging.api.client;

import org.junit.Test;
import org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito.*;
import org.openmrs.module.imaging.OrthancConfiguration;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrthancHttpClientTest {

    private OrthancHttpClient httpClient;

    @Before
    public void setUp() {
        httpClient = new OrthancHttpClient();
    }

    @Test
    public void testCreateConnection() throws IOException {
        String method = "POST";
        String url = "http://localhost:8052";
        String path = "/system";
        String userName = "orthanc";
        String password = "orthanc";

        HttpURLConnection con = httpClient.createConnection(method, url, path, userName, password);

        assertEquals("POST", con.getRequestMethod());
        String expectedAuth = "Basic " + Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());
        assertFalse(con.getUseCaches());
    }

    @Test
    public void testSendOrthancQuery() throws IOException {
        HttpURLConnection con = mock(HttpURLConnection.class);
        OutputStream os = mock(OutputStream.class);
        when(con.getOutputStream()).thenReturn(os);

        String query = "{\"query\":\"test\"}";
        httpClient.sendOrthancQuery(con, query);

        verify(con).setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        verify(con).setRequestProperty("charset", "utf-8");
        verify(con).setRequestProperty("Content-Length", Integer.toString(query.getBytes().length));
        verify(con).setDoOutput(true);
        verify(con).getOutputStream();
    }

    @Test
    public void testThrowConnectionException() throws IOException {
        OrthancConfiguration config = mock(OrthancConfiguration.class);
        when(config.getOrthancBaseUrl()).thenReturn("http://localhost:8052");

        HttpURLConnection con = mock(HttpURLConnection.class);
        when(con.getResponseCode()).thenReturn(500);
        when(con.getResponseMessage()).thenReturn("Internal Server Error");

        IOException exception = assertThrows(IOException.class, () ->
            OrthancHttpClient.throwConnectionException(config, con)
        );
        assertTrue(exception.getMessage().contains("Request to Orthanc server " + config.getOrthancBaseUrl() + " failed with error"));
    }

    @Test
    public void testIsOrthancReachable_success() throws IOException {
        OrthancConfiguration config = mock(OrthancConfiguration.class);
        when(config.getOrthancBaseUrl()).thenReturn("http://localhost:8052");
//        when(config.getOrthancBaseUrl()).thenReturn("http://localhost:8042"); // False
        when(config.getOrthancPassword()).thenReturn("orthanc");
        when(config.getOrthancUsername()).thenReturn("orthanc");

        HttpURLConnection con = mock(HttpURLConnection.class);
        when(con.getResponseCode()).thenReturn(200);

        URL url = new URL("http://localhost:8052/system");
        HttpURLConnection realConnection = (HttpURLConnection) url.openConnection();
        realConnection.disconnect();

        boolean reachable = httpClient.isOrthancReachable(config);
        // Can't inject the mock httpURLConnection directly in this method
        // Use this only as a basic test or wrap URL logic in testable units
        assertTrue(reachable);
        //assertFalse(reachable); // "http://localhost:8042"

    }

    @Test
    public void testGetStatus() throws IOException {
        HttpURLConnection con = mock(HttpURLConnection.class);
        when(con.getResponseCode()).thenReturn(200);
        assertEquals(200, httpClient.getStatus(con));
    }

    @Test
    public void testGetResponseStream() throws IOException {
        HttpURLConnection con = mock(HttpURLConnection.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("response".getBytes());
        when(con.getInputStream()).thenReturn(inputStream);
        assertEquals(inputStream, httpClient.getResponseStream(con));
    }

    @Test
    public void testGetErrorMessage() throws IOException {
        HttpURLConnection con = mock(HttpURLConnection.class);
        when(con.getResponseMessage()).thenReturn("Not Found");
        assertEquals("Not Found", httpClient.getErrorMessage(con));
    }

}
