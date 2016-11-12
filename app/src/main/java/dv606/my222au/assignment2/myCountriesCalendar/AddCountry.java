package dv606.my222au.assignment2.myCountriesCalendar;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

import dv606.my222au.assignment2.R;

public class AddCountry extends AppCompatActivity {


    private EditText mYearInput;
    private EditText mCountryInput;
    private Button mButton;
    private AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_country);

        ActionBar actionBar = getActionBar();


        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alert = setup_alert();



        mYearInput = (EditText) findViewById(R.id.yearInput);
        mCountryInput = (EditText) findViewById(R.id.countryInput);
        mButton = (Button) findViewById(R.id.addCountryButton);
        Intent intent = getIntent();
        String country = intent.getStringExtra("country");
        int year = intent.getIntExtra("year",0);
        mCountryInput.setText(country);
        mYearInput.setText(year+"");



        mCountryInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);




        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ( isVaildYear() && isVaildCountry(getCountryInput())) {

                    Intent intent = new Intent();
                    intent.putExtra("result", getCountryInput());
                    intent.putExtra("result2", parseYeart());

                    setResult(RESULT_OK, intent);

                    finish();
                } else
                    alert.show();
            }


        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // Predefined icon ID
                // app icon in action bar clicked ==>  go home
                Intent intent = new Intent(this, MyCountriesCalendar.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int parseYeart(){
        int year = 0;
        try {

            String yearString = mYearInput.getText().toString();
            if (yearString.length() > 0) {
                year = Integer.parseInt(yearString);
            }


            return year;
        } catch (NumberFormatException i) {
            alert.show();
        }



        return year;
    }

    private String getCountryInput(){
        String countryString = mCountryInput.getText().toString();
        return countryString;
    }

    /** validation and error handling**/

    private boolean isVaildYear(){

        return parseYeart() < 2017 && parseYeart() > 1910;
    }




    // checks if the country is a vaild existing country uses locale
    private boolean isVaildCountry(String country){
        String[] locales = Locale.getISOCountries();

        for(String countries :locales ){
            Locale loc = new Locale("",countries);
            loc.setDefault(loc.ENGLISH);  // returns list of country names in english
            if(country.equalsIgnoreCase(loc.getDisplayCountry())){
                return true;
            }

        }

        return false;
    }



    // configurs the alert to the user
    private AlertDialog setup_alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_msg);
        builder.setTitle(R.string.alert_title);
        builder.setPositiveButton("ok", new DialogDone());  // Add button action
        return builder.create();
    }


    private class DialogDone implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();             // Close down dialog
        } }



}


