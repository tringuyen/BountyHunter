package com.bountyhunter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class BountyHunter extends Activity /*implements OnClickListener */
{
    //protected Button button1;
    //protected Button button2;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //ImageView logo = (ImageView) findViewById(R.id.logo);
        //button1 = (Button) findViewById(R.id.mainButton);
        //button2 = (Button) findViewById(R.id.menuButton);
    }
    /*public void onClick(View v)
    {
		if (v == button1)
    	{
    		setContentView(R.layout.menu);
    		Toast.makeText(this, "Clicked main button", Toast.LENGTH_SHORT).show();
    	}
		else if (v == button2)
		{
			setContentView(R.layout.main);
			Toast.makeText(this, "Clicked menu button", Toast.LENGTH_SHORT).show();
		}
    }*/
    
    //the following are all buttons
    public void mainClick(View view)
    {
    	setContentView(R.layout.menu);
    	Toast.makeText(this, "Clicked main button", Toast.LENGTH_SHORT).show();
    }
    public void menuClick(View view)
    {
    	setContentView(R.layout.main);
    	Toast.makeText(this, "Button in menu was clicked!", Toast.LENGTH_LONG).show();
    }
    public void mapLocation(View view)
    {
    	Intent myIntent = new Intent(BountyHunter.this, BountyMap.class);
    	BountyHunter.this.startActivity(myIntent);
    }
    public void debugLocation(View view)
    {
    	Intent myIntent = new Intent(BountyHunter.this, BountyLocation.class);
    	BountyHunter.this.startActivity(myIntent);
    }
    public void rooms(View view)
    {
    	setContentView(R.layout.rooms);
    }
}