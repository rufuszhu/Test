package digital.dispatch.TaxiLimoNewUI.Book;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class DriverNoteActivity extends BaseActivity {
	private EditText driverMessage;
	private TextView textRemaining, ok ,clear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_note);
		driverMessage = (EditText) findViewById(R.id.message);
		textRemaining = (TextView) findViewById(R.id.text_remaining);
		driverMessage.setHint("Buzzer #");
		driverMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable note) {
				textRemaining.setText(MBDefinition.DRIVER_NOTE_MAX_LENGTH - note.length() + " Charaters Left");
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
		ok = (TextView) findViewById(R.id.ok);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Utils.driverNoteString = driverMessage.getText().toString();
				
				finish();
			}
		});
		clear = (TextView) findViewById(R.id.clear);
		clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				driverMessage.setText("");
			}
		});

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}