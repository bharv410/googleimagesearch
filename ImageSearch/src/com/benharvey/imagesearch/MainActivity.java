package com.benharvey.imagesearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;

import com.benharvey.imagesearch.helpers.GoogleImage;

public class MainActivity extends Activity {

	EditText imageSearchEditText;
	GridView imageGridView;
	private String searchText;
	private ArrayList<GoogleImage> listImages;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageSearchEditText= (EditText)findViewById(R.id.imageSearchBox);
		imageGridView =(GridView)findViewById(R.id.imageGridView);
	}
	
	public void searchButtonClick(View v){
		//encode search text and retrieve images from google image search api
		searchText = Uri.encode(imageSearchEditText.getText().toString());
		   System.out.println("Search string => "+searchText);
		   new GetMoreImagesTask().execute();
	}
	
	public class GetMoreImagesTask extends AsyncTask<Void, Void, Void> {
		JSONObject json;
		   ProgressDialog dialog;
		   
		   @Override
		   protected void onPreExecute() {
			   super.onPreExecute();
			   dialog = ProgressDialog.show(MainActivity.this, "", "Please wait...");
		   }
		protected Void doInBackground(Void... params) {
	    	
			URL url;
			try {
				url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
					   	"v=1.0&q="+searchText+"&rsz=8");
				System.out.println("Got here1");
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("Referer", "http://kidgeniusdesigns.com");
			System.out.println("Got here2");
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				builder.append(line);
				System.out.println("Got here3");
	}

	 json = new JSONObject(builder.toString());
	//now have some fun with the results...
	 System.out.println("Got here4");
			} catch (MalformedURLException e) {
				e.printStackTrace();
				System.out.println("Url exception");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("IO exception");
			} catch (JSONException e) {
				System.out.println("JSON exception");
				e.printStackTrace();
			}
			return null;
	    }

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if(dialog.isShowing())
			   {
				   dialog.dismiss();
			   }
			   
			   try {
				   JSONObject responseObject = json.getJSONObject("responseData");
				   JSONArray resultArray = responseObject.getJSONArray("results");
				   
				   listImages = getImageList(resultArray);
				   //SetListViewAdapter(listImages);
				   System.out.println("Result array length => "+resultArray.length());
			   } catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<GoogleImage> getImageList(JSONArray resultArray)
	   {
		   ArrayList<GoogleImage> listImages = new ArrayList<GoogleImage>();
		   GoogleImage img;

			try 
			{
				for(int i=0; i<resultArray.length(); i++)
				{
					JSONObject obj;
					obj = resultArray.getJSONObject(i);
					img = new GoogleImage();
				   
					img.setTitle(obj.getString("title"));
					img.setThumbUrl(obj.getString("tbUrl"));
					
					System.out.println("Thumb URL => "+obj.getString("tbUrl"));
					
					listImages.add(img);
				   
				} 
				return listImages;
			 }
			 catch (JSONException e) 
			 {
					e.printStackTrace();
			 }
			 
			 return null;
		}
	   
}