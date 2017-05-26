package com.filiereticsa.arc.augmentepf.managers;

import android.os.AsyncTask;

import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by anthony on 07/05/2017.
 */
// This manager will help you handle http requests
public class HTTPRequestManager {

    private static final String TAG = "Ici";
    private static OkHttpClient client = new OkHttpClient();
    private static final String serverUrl = "http://192.168.206.106/AugmentEPF/php/";

    public static final int AVAILABLE_CLASSROOMS = 0;
    public static final int CLASSROOMS = 1;
    public static final int CONNECTION = 2;
    public static final int ACCOUNT_CREATION = 3;
    public static final int POI = 4;
    public static final int BEACONS = 5;
    public static final int MAPS = 6;
    public static final int PATH_HISTORY = 6;

    /*
                HOW TO DO AN HTTP REQUEST
    First of all, there is an example in the HomePageActivity.java file
    See the method postRequestExample(). It's all commented and working.

    What you need to do :
    - Implement HTTPRequestInterface in the class you wanna do the request
    - Implement the method from this interface
    - Call HTTPRequestManager.doPostRequest or HTTPRequestManager.doGetRequest to do your request
        ° For HTTPRequestManager.doPostRequest there is 4 parameters:
            - Give the name of the method in the server (given by Guilhem)
            - Give the parameters of the POST request as a JSONObject (see the example)
            - Add a reference to the interface (this)
            - Give a requestId (see the constants) to retrieve your request
        ° For HTTPRequestManager.doGetRequest:
            - Give the name of the method in the server (given by Guilhem)
            - Add a reference to the interface (this)
            - Give a requestId (see the constants) to retrieve your request
    - These methods don't return anything but you are probably willing to get the answer for the server
    - The result of the request will be given in the method "onRequestDone" implemented by the interface
    - The result is a String you must put in a JSONObject in order to use it

    Note that you won't be able to do HTTPRequest if your phone is not connected to EPF's network
    For any problem contact Anthony
     */

    // Call this method to do a post request
    public static void doPostRequest(String url, String parameter,
                                     HTTPRequestInterface observer, int requestId) {
        RequestParameter requestParameter = new RequestParameter(serverUrl + url, RequestType.POST,
                parameter, observer, requestId);
        HttpAsyncTask asyncTask = new HttpAsyncTask();
        asyncTask.execute(requestParameter);
    }

    // Call this method to do a get request
    public static void doGetRequest(String url,
                                    HTTPRequestInterface observer, int requestId) {
        RequestParameter requestParameter = new RequestParameter(serverUrl + url, RequestType.GET,
                observer, requestId);
        HttpAsyncTask asyncTask = new HttpAsyncTask();
        asyncTask.execute(requestParameter);
    }

    // This method will automatically be called
    private static String executePostRequest(String url, String parameter) throws IOException {
        Response response = null;
        RequestBody formBody = new FormBody.Builder()
                .add("Send", parameter)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null) {
            return response.body().string();
        } else {
            return "Error";
        }
    }

    // This method will automatically be called
    private static String executeGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    // This class will send the request as an asynchronous task in order to avoid crash :
    // You can't do HTTP Request on the UIThread, that's why there is this class
    private static class HttpAsyncTask extends AsyncTask<RequestParameter, Void, String> {
        RequestParameter requestParameter;

        @Override
        protected String doInBackground(RequestParameter... params) {
            String url;
            String parameter;
            String valueReturned = "Error";
            requestParameter = params[0];
            switch (params[0].requestType) {
                case GET:
                    url = params[0].url;
                    try {
                        valueReturned = executeGetRequest(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return valueReturned;

                case POST:
                    url = params[0].url;
                    parameter = params[0].parameter;
                    valueReturned = "";
                    try {
                        valueReturned = executePostRequest(url, parameter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return valueReturned;

                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(String returnedValue) {
            requestParameter.observer.onRequestDone(returnedValue, requestParameter.requestId);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    // Request parameters put in a class in order to give it all in one parameter in the AsyncTask
    public static class RequestParameter {

        String url;
        RequestType requestType;
        String parameter;
        HTTPRequestInterface observer;
        int requestId;

        // There is 2 constructor : One for Post requests and one for Get requests
        public RequestParameter(String url, RequestType requestType, String parameter,
                                HTTPRequestInterface observer, int requestId) {
            this.url = url;
            this.requestType = requestType;
            this.parameter = parameter;
            this.observer = observer;
            this.requestId = requestId;
        }

        public RequestParameter(String url, RequestType requestType,
                                HTTPRequestInterface observer, int requestId) {
            this.url = url;
            this.requestType = requestType;
            this.observer = observer;
            this.requestId = requestId;
        }
    }

    public enum RequestType {
        POST,
        GET
    }
}