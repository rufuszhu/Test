package digital.dispatch.TaxiLimoNewUI.Book;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class DriverNoteActivity extends BaseActivity {

	private EditText driverMessage;
	private TextView textRemaining;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_note);
        setToolBar();
		driverMessage = (EditText) findViewById(R.id.message);
		textRemaining = (TextView) findViewById(R.id.text_remaining);
		driverMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable note) {
				textRemaining.setText(MBDefinition.DRIVER_NOTE_MAX_LENGTH - note.length() + " Characters Left");
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
		});

		driverMessage.setText(Utils.driverNoteString);
		driverMessage.setSelection(Utils.driverNoteString.length());


	}

    @Override
    protected void onResume(){
        driverMessage.requestFocus();
        super.onResume();
    }
    private void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Typeface face = FontCache.getFont(this, "fonts/Exo2-Light.ttf");
        TextView yourTextView = Utils.getToolbarTitleView(this,toolbar);
        yourTextView.setTypeface(face);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_note, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==android.R.id.home){
			finish();
			return true;
		}
        if(id==R.id.action_accept){
            Utils.driverNoteString = driverMessage.getText().toString();
            finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}

}
