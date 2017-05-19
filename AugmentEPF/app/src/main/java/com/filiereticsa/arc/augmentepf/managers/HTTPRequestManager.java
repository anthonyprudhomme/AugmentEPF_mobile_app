package com.filiereticsa.arc.augmentepf.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by anthony on 07/05/2017.
 */
//this manager will help you handle http requests
public class HTTPRequestManager {

    private int lastResponseCode;
    private String url="";
    private String path="";
    private String query="";

    /* the constructor requires the url of the website from where you want to do your requests
       it takes the path to the page you want
       it takes the query you want
       those parameters can be null */
    public HTTPRequestManager(String url, String path, String query) {
        if (url != null) {
            this.url = url;
        }
        if (path != null) {
            this.path = path;
        }
        if (query != null) {
            this.query = query;
        }
    }

    // return the response code of the lase http request made
    // this can be used to see if the request failed (404 etc.)
    public int getLastResponseCode() {
        return this.lastResponseCode;
    }

    // this method will do a GET request and get the datas from that request as a String.
    public String doGetHTTPRequest() {
        String result;
        try {
            URL url = new URL(this.url + this.path + this.query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.connect();

            // Checks is the response code is a good response code. If it is not it will return an
            // empty string
            // Good response codes usually starts with a "2".
            lastResponseCode = connection.getResponseCode();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK
                    && connection.getResponseCode() != HttpURLConnection.HTTP_MOVED_PERM) {
                return "";
            }
            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder("");
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            connection.disconnect();
            result = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return result;
    }

    // this method will do a POST request
    public void doPostHTTPRequest() {
        try {
            URL url = new URL(this.url + this.path + this.query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

            writer.write(this.url + this.path + this.query);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}