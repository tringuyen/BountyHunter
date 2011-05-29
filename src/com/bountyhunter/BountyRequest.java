package com.bountyhunter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

class BountyRequest extends AsyncTask<String, String, String>{

	private static final String BASE_URL = "http://good-hunting.appspot.com/";
	
	public static String getResponse (String uri) {
		String responseString = "";
		HttpClient httpclient = new DefaultHttpClient();
		try {
			 HttpResponse response = httpclient.execute(new HttpGet(BASE_URL + uri));
			 StatusLine statusLine = response.getStatusLine();
			 if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			     ByteArrayOutputStream out = new ByteArrayOutputStream();
			     response.getEntity().writeTo(out);
			     out.close();
			     responseString = out.toString();
			      
			 } else{
			     //Closes the connection.
			     response.getEntity().getContent().close();
			     throw new IOException(statusLine.getReasonPhrase());
			 }
		 } catch (Exception e) {
			 // Do something
		 }
		 
		 return responseString;
	}
	
    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}