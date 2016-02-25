package swindroid.suntime.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import swindroid.suntime.R;
import swindroid.suntime.calc.AstronomicalCalendar;
import swindroid.suntime.calc.GeoLocation;

/**
 * Created by Simon on 10/27/2015.
 */
public class DatesFragment extends Fragment {
    //for geolocation calculation for dates table
    String city;
    double latitude;
    double longitude;
    String timezone;
    int days = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.tab_dates, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //register click handler
        Button btn = (Button)getActivity().findViewById(R.id.generateTable);
        btn.setOnClickListener(dateRangeHandler);

        //populate location data from file
        readFileData();

        //initialise default start date
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");
        Calendar calendar = Calendar.getInstance();

        String formatted = format.format(calendar.getTime());
        TextView d1 = (TextView)getActivity().findViewById(R.id.startDate);
        d1.setText(formatted);
    }

    View.OnClickListener dateRangeHandler = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            //clear result from previous search
            if(days != 0){
                TableLayout t1 = (TableLayout)getActivity().findViewById(R.id.datesTable);
                while(t1.getChildCount() > 1){
                    t1.removeView(t1.getChildAt(t1.getChildCount() - 1));
                }
                //t1.removeAllViews();
            }

            days = duration();

            //start date
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");

            //city geolocation data
            TimeZone tz = TimeZone.getTimeZone(timezone);
            GeoLocation geolocation = new GeoLocation(city, latitude, longitude, tz);
            AstronomicalCalendar ac = new AstronomicalCalendar(geolocation);

            //objects for table layout, table row, table values
            TableLayout t1;
            TableRow tr;
            TextView val1, val2, val3;

            t1 = (TableLayout)getActivity().findViewById(R.id.datesTable);
            t1.setColumnStretchable(0, true);
            t1.setColumnStretchable(1, true);
            t1.setColumnStretchable(2, true);

            //generate a row for each day
            for(int i = 0; i < days; i++){

                if(i == 0){
                    EditText date = (EditText)getActivity().findViewById(R.id.startDate);
                    String date_string = date.getText().toString();
                    String[] dates = date_string.split("-");

                    int day = Integer.parseInt(dates[0]);
                    int month = Integer.parseInt(dates[1]);
                    int year = Integer.parseInt("20" + dates[2]);

                    ac.getCalendar().set(year, month-1, day);    //set the start date of the range
                }
                else
                    ac.getCalendar().add(Calendar.DATE, 1); //each day from the date by 1 day

                Date srise = ac.getSunrise();
                Date sset = ac.getSunset();

                //time format
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                //text view data in each cell
                val1 = new TextView(getActivity().getApplicationContext());
                val1.setText(format.format(ac.getCalendar().getTime()));
                val1.setTextSize(25);
                val1.setGravity(Gravity.CENTER);

                val2 = new TextView(getActivity().getApplicationContext());
                val2.setText(sdf.format(srise));
                val2.setTextSize(25);
                val2.setGravity(Gravity.CENTER);

                val3 = new TextView(getActivity().getApplicationContext());
                val3.setText(sdf.format(sset));
                val3.setTextSize(25);
                val3.setGravity(Gravity.CENTER);

                //set the rows
                tr = new TableRow(getActivity().getApplicationContext());
                tr.addView(val1);
                tr.addView(val2);
                tr.addView(val3);

                //add to the table
                t1.addView(tr);
            }
        }
    };


    private int duration()
    {
        long days = 0;

        //dates entered
        TextView d1 = (TextView)getActivity().findViewById(R.id.startDate);
        String firstDate = d1.getText().toString();
        TextView d2 = (TextView)getActivity().findViewById(R.id.endDate);
        String secondDate = d2.getText().toString();

        //default date format
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yy");
        try {
            Date start = myFormat.parse(firstDate);
            Date end = myFormat.parse(secondDate);

            long duration  = end.getTime() - start.getTime();
            days = TimeUnit.DAYS.convert(duration, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //extra day to account for start/end date
        return (int)days + 1;
    }

    private void readFileData(){
        //current city and coordinates
        try {
            File file = new File(this.getActivity().getFilesDir(), "new_city.txt");
            FileInputStream in = new FileInputStream(file);

            byte[] bytes = new byte[(int)file.length()];
            in.read(bytes);
            in.close();

            //write to activity default variables
            String[] contents = new String(bytes).split("\n");
            this.city = contents[0];
            this.latitude = Double.parseDouble(contents[1]);
            this.longitude = Double.parseDouble(contents[2]);
            this.timezone = contents[3];
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
