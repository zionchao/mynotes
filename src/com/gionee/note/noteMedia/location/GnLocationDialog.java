package com.gionee.note.noteMedia.location;

import com.gionee.note.NoteActivity;
import com.gionee.note.R;

import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import amigo.preference.AmigoPreferenceManager;
import amigo.widget.AmigoButton;
import android.view.View;
import android.widget.CheckBox;

public class GnLocationDialog extends AmigoActivity {
    
    private AmigoButton mBtnDone;
    private CheckBox mCheckBox;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gn_location_dialog);
        mBtnDone = (AmigoButton) findViewById(R.id.location_done);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                // TODO Auto-generated method stub 
                if(mCheckBox.isChecked()) {
                    SharedPreferences pref = AmigoPreferenceManager.getDefaultSharedPreferences(GnLocationDialog.this);
                    Editor edit = pref.edit();
                    edit.putBoolean("location_without_tip", true);
                    edit.commit();
                }
                setResult(NoteActivity.RESULT_CODE_LOCATION_TIP_OK);
                finish();
            }
        });
        mCheckBox = (CheckBox) findViewById(R.id.location_without_tip);
    }
    
}
