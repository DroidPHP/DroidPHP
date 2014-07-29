package org.opendroidphp.app.common.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    static InputStream is = null;
    static JSONArray jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    public JSONArray getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

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
            jObj = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

    public List<Extension> populate(String repoUrl) {

        JSONArray json = getJSONFromUrl(repoUrl);
        /*String js = "[{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"},{\"name\":\"extension 1\",\"summery\":\"summery\",\"shell\":\"\",\"downloadUrl\":\"\"}]";
        JSONArray json = null;
        try {
            json = new JSONArray(js);
        } catch (Exception e) {

        }
        */

        List<Extension> extensionList = new ArrayList<Extension>();

        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject extension = json.getJSONObject(i);// Used JSON Object from Android

                extensionList.add(new Extension(
                        (String) extension.get("repoName"),
                        (String) extension.get("repoDescription"),
                        (String) extension.get("shellScript"),
                        (String) extension.get("downloadUrl"),
                        (String) extension.get("fileName"),
                        (String) extension.get("installPath")
                ));
            }
        } catch (Exception e) {

        }
        return extensionList;
    }
}