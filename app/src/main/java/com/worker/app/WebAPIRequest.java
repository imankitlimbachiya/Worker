package com.worker.app;

import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WebAPIRequest {

    public static boolean login = false;

    public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public static Document performPost(String url, String body) {
        Document doc = null;

        try {
            HttpParams basicparams = new BasicHttpParams();
            URI uri = new URI(url);
            HttpPost method = new HttpPost(uri);
            int timeoutConnection = 60000;
            HttpConnectionParams.setConnectionTimeout(basicparams,
                    timeoutConnection);
            DefaultHttpClient client = new DefaultHttpClient(basicparams);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            body = body.replaceAll("%20", " ");
            String[] parameters = body.split("&");
            for (int i = 0; i < parameters.length; i++) {
                String[] parameter = parameters[i].split("=");
                if (parameter.length >= 2) {
                    nameValuePairs.add(new BasicNameValuePair(parameter[0],
                            parameter[1]));
                }
            }
            method.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse res = client.execute(method);

            InputStream data = res.getEntity().getContent();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(data);
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }

    public static String performRequestAsString(String url, String method) {
        String value = null;

        try {

            if (method == "POST") {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                String sampleurl = url;
                Log.e("Request_Url", "" + sampleurl);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();
                value = convertStreamToString(is);

            } else if (method == "GET") {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                Log.e("Request_Url", "" + url);
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();
                value = convertStreamToString(is);
            }

        } catch (ClientProtocolException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public static JSONObject makeJsonObjHttpRequest(String url, String method, List<NameValuePair> params) {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        // Making HTTP request
        try {

            // check for request method
            if (method == "POST") {
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                String paramString = URLEncodedUtils.format(params, "utf-8");
                String sampleurl = url + "" + paramString;
                Log.e("Request_Url", "" + sampleurl);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } else if (method == "GET") {
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "" + paramString;
                Log.e("Request_Url", "" + url);
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

    public static JSONArray makeJsonArryHttpRequest(String url, String method, List<NameValuePair> params) {

        InputStream is = null;
        JSONArray jArray = null;
        String json = "";
        // Making HTTP request
        try {


            if (method == "POST") {


                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                String paramString = URLEncodedUtils.format(params, "utf-8");
                String sampleurl = url + "" + paramString;
                Log.e("Request_Url", "" + sampleurl);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } else if (method == "GET") {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "" + paramString;
                Log.e("Request_Url", "" + url);
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }


        try {
            jArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return jArray;
    }

    public static String postJsonData(String url, List<NameValuePair> params) {
        String response_string = new String();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));


            String paramString = URLEncodedUtils.format(params, HTTP.UTF_8);
            String sampleurl = url + "" + paramString;
            Log.e("Request_Url", "" + sampleurl);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            if (response != null) {
                InputStream in = response.getEntity().getContent();
                response_string = WebAPIRequest.convertStreamToString(in);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response_string;
    }

    public static String performPost(String url, Bundle b) {
        // TODO Auto-generated method stub
        return null;
    }

    public String performJSon_ImageUpload1(byte[] image_byt, String url1, String name, String filename) {
        System.out.println(name + "    and    " + filename);
        String doc = null;
        String boundary = "---------------------------14737809831466499882746641449";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        try {
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            // connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);


            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\";filename=\"" + filename + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            outputStream.write(image_byt);
            outputStream.writeBytes(lineEnd + twoHyphens + boundary
                    + twoHyphens + lineEnd);
            InputStream data = connection.getInputStream();
            if (data != null) {
                StringBuilder sb = new StringBuilder();
                int b;
                while ((b = data.read()) != -1) {
                    sb.append((char) b);
                }
                data.close();
                Log.i("Image_Upload : ", sb.toString());
                return sb.toString();
            } else {
                Log.i("Image_Upload : ", "data is null");
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static String performJSon_ImageUpload1(byte[] image_byt, String url1, String profileimage, String projectId, String description, String title, String tag) {

        String doc = null;
        String boundary = "---------------------------14737809831466499882746641449";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        try {
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            /*outputStream.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
   outputStream.writeBytes("Content-Disposition: form-data; name=\""+"userImage"+"\";filename=\"" + userId+".png"  + lineEnd);
   outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
   outputStream.write(image_byt);
   outputStream.writeBytes(lineEnd+twoHyphens + boundary +twoHyphens+lineEnd);*/

            if (image_byt != null) {
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"projectImage\";filename=\"" + profileimage + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                Log.e("image_byt", "" + image_byt);
                outputStream.write(image_byt);
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);
            }

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "projectId" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            outputStream.write(projectId.getBytes("UTF-8"));

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "description" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            outputStream.write(description.getBytes("UTF-8"));

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "title" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            outputStream.write(title.getBytes("UTF-8"));


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "tag" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            outputStream.write(tag.getBytes("UTF-8"));

            InputStream data = connection.getInputStream();
            if (data != null) {
                StringBuilder sb = new StringBuilder();
                int b;
                while ((b = data.read()) != -1) {
                    sb.append((char) b);
                }
                data.close();
                Log.i("Image_Upload : ", sb.toString());
                return sb.toString();
            } else {
                Log.i("Image_Upload : ", "data is null");
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static String update_country(byte[] image_byt, String url1, String Image, String OrderID, String CountryID, String language) {

        String doc = null;
        String boundary = "---------------------------14737809831466499882746641449";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        try {
            Log.e("URL", "" + url1);
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            if (image_byt != null) {
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"Upload\";filename=\"" + Image + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                Log.e("Upload", "" + Image);
                outputStream.write(image_byt);
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);
            }


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "OrderID" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("OrderID", "" + OrderID);
            outputStream.write(OrderID.getBytes("UTF-8"));

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "CountryID" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("CountryID", "" + CountryID);
            outputStream.write(CountryID.getBytes("UTF-8"));


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "language" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("language", "" + language);
            outputStream.write(language.getBytes("UTF-8"));


            InputStream data = connection.getInputStream();
            if (data != null) {
                StringBuilder sb = new StringBuilder();
                int b;
                while ((b = data.read()) != -1) {
                    sb.append((char) b);
                }
                data.close();
                Log.i("Image_Upload : ", sb.toString());
                return sb.toString();
            } else {
                Log.i("Image_Upload : ", "data is null");
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static String update_profile(byte[] image_byt, String url1, String Image, String UserID, String OrderId, String language) {

        String doc = null;
        String boundary = "---------------------------14737809831466499882746641449";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        try {
            Log.e("URL", "" + url1);
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            if (image_byt != null) {
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"UserIdentificationAttachment\";filename=\"" + Image + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                Log.e("Image", "" + Image);
                outputStream.write(image_byt);
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);
            }


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "UserID" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("UserID", "" + UserID);
            outputStream.write(UserID.getBytes("UTF-8"));

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "OrderID" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("OrderID", "" + OrderId);
            outputStream.write(OrderId.getBytes("UTF-8"));


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "language" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("language", "" + language);
            outputStream.write(language.getBytes("UTF-8"));


            InputStream data = connection.getInputStream();
            if (data != null) {
                StringBuilder sb = new StringBuilder();
                int b;
                while ((b = data.read()) != -1) {
                    sb.append((char) b);
                }
                data.close();
                Log.i("Image_Upload : ", sb.toString());
                return sb.toString();
            } else {
                Log.i("Image_Upload : ", "data is null");
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static String editproject_video(byte[] image_byt,byte[] viddeo_byt, String videoName,String url1, String postImage, String userId, String description, String title, String tag,byte[] video) {

        String doc = null;
        String boundary = "---------------------------14737809831466499882746641449";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

//        videoPost, description, userId, [ title, tag ] - new parameter, imagePost

        try {
            Log.e("URL", "" + url1);
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            /*outputStream.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
   outputStream.writeBytes("Content-Disposition: form-data; name=\""+"userImage"+"\";filename=\"" + userId+".png"  + lineEnd);
   outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
   outputStream.write(image_byt);
   outputStream.writeBytes(lineEnd+twoHyphens + boundary +twoHyphens+lineEnd);*/

            if (image_byt != null)
            {
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"imagePost\";filename=\"" + postImage + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                Log.e("image_byt", "" + image_byt);
                Log.e("imagePost", "" + postImage);
                outputStream.write(image_byt);
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);
            }

            if(viddeo_byt != null)
            {
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"videoPost\";filename=\"" + videoName + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                Log.e("video_byt", "" + viddeo_byt);
                Log.e("videoname = ", "" + videoName);
                outputStream.write(viddeo_byt);
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);

            }


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "userId" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("userId", "" + userId);
            outputStream.write(userId.getBytes("UTF-8"));

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "description" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("description", "" + description);
            outputStream.write(description.getBytes("UTF-8"));


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "title" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("title", "" + title);
            outputStream.write(title.getBytes("UTF-8"));


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "tag" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("tag", "" + tag);
            outputStream.write(tag.getBytes("UTF-8"));


            InputStream data = connection.getInputStream();
            if (data != null) {
                StringBuilder sb = new StringBuilder();
                int b;
                while ((b = data.read()) != -1) {
                    sb.append((char) b);
                }
                data.close();
                Log.i("Image_Upload : ", sb.toString());
                return sb.toString();
            } else {
                Log.i("Image_Upload : ", "data is null");
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static String socialSignup(byte[] image_byt, String url1l, String proflieImage, String userName, String email, String typeLogin, String socialId, String deviceType, String tokenId, String firstName, String lastName) {

        String doc = null;
        String boundary = "---------------------------14737809831466499882746641449";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        try {
            Log.e("URL", "" + url1l);
            URL url = new URL(url1l);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            /*outputStream.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
   			outputStream.writeBytes("Content-Disposition: form-data; name=\""+"userImage"+"\";filename=\"" + userId+".png"  + lineEnd);
   			outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
   			outputStream.write(image_byt);
   			outputStream.writeBytes(lineEnd+twoHyphens + boundary +twoHyphens+lineEnd);*/

            if (image_byt != null) {
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"proflieImage\";filename=\"" + proflieImage + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                Log.e("image_byt", "" + image_byt);
                Log.e("proflieImage", "" + proflieImage);
                outputStream.write(image_byt);
                outputStream.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);
            }
            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "userName" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("userName", "" + userName);
            outputStream.write(userName.getBytes("UTF-8"));

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "email" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("email", "" + email);
            outputStream.write(email.getBytes("UTF-8"));

        //    userName, email, typeLogin, socialId    [deviceType, tokenId] - new parameter, firstName, lastName


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "typeLogin" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("typeLogin", "" + typeLogin);
            outputStream.write(typeLogin.getBytes("UTF-8"));

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "socialId" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("socialId", "" + socialId);
            outputStream.write(socialId.getBytes("UTF-8"));

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "deviceType" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("deviceType", "" + deviceType);
            outputStream.write(deviceType.getBytes("UTF-8"));


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "tokenId" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("tokenId", "" + tokenId);
            outputStream.write(tokenId.getBytes("UTF-8"));


            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "firstName" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("firstName", "" + firstName);
            outputStream.write(firstName.getBytes("UTF-8"));

            outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "lastName" + "\";" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            Log.e("lastName", "" + lastName);
            outputStream.write(lastName.getBytes("UTF-8"));



            InputStream data = connection.getInputStream();
            if (data != null) {
                StringBuilder sb = new StringBuilder();
                int b;
                while ((b = data.read()) != -1) {
                    sb.append((char) b);
                }
                data.close();
                Log.i("Image_Upload : ", sb.toString());
                return sb.toString();
            } else {
                Log.i("Image_Upload : ", "data is null");
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
