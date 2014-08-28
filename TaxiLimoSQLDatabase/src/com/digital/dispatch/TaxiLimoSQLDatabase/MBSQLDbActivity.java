package com.digital.dispatch.TaxiLimoSQLDatabase;

import com.digital.dispatch.TaxiLimoSQLDatabase.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MBSQLDbActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbsqldb);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_mbsqldb, menu);
        return true;
    }

}
