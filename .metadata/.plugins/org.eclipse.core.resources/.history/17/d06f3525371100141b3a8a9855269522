package com.example.tabbedfragui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;

public class FragmentTabs extends FragmentActivity {

	private FragmentTabHost mTabHost;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        
        mTabHost.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"),
        		SimpleFrag.class, null);
        
        mTabHost.addTab(mTabHost.newTabSpec("detailed").setIndicator("Detailed"),
        		DetailedFrag.class, null);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
