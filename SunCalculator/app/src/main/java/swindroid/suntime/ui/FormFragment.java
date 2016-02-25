package swindroid.suntime.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import swindroid.suntime.R;

/**
 * Created by Simon on 9/20/2015.
 */
public class FormFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.tab_form, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button btn = (Button)getActivity().findViewById(R.id.saveLoc);
        btn.setOnClickListener(saveLocationHandler);
    }


    View.OnClickListener saveLocationHandler = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            //confirm
            Context context = getActivity().getApplicationContext();
            CharSequence text = "Saved";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            //get text from input fields
            TextView citytext = (TextView)getActivity().findViewById(R.id.city);
            TextView latitudetext = (TextView)getActivity().findViewById(R.id.latitude);
            TextView longitudetext = (TextView)getActivity().findViewById(R.id.longitude);
            TextView timezonetext = (TextView)getActivity().findViewById(R.id.timezone);

            String city = citytext.getText().toString();
            double latitude = Double.parseDouble(latitudetext.getText().toString());
            double longitude = Double.parseDouble(longitudetext.getText().toString());
            String timezone = timezonetext.getText().toString();

            //save to internal storage file
            try{
                FileOutputStream write = getActivity().openFileOutput("new_city.txt", 0);
                OutputStreamWriter out = new OutputStreamWriter(write);
                out.write(city + "\n" + latitude + "\n" + longitude + "\n" + timezone);
                out.flush();
                out.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    };
}