package swindroid.suntime.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import swindroid.suntime.R;
import swindroid.suntime.calc.AstronomicalCalendar;
import swindroid.suntime.calc.GeoLocation;

/**
 * Created by Simon on 10/27/2015.
 */
public class SuntimeFragment extends Fragment {

    //default values for first time run
    //these are overwritten by the data file once its created
    String city = "Melbourne";
    double latitude = -37.81;
    double longitude = 144.96;
    String timezone = "Australia/Melbourne";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main, container, false);
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//checks for some persistent data storage
		//if the file exists that data will populate the fields
		File file = new File(this.getActivity().getFilesDir(), "new_city.txt");
		if(file.exists()){
			openReadFile();
		}

		super.onActivityCreated(savedInstanceState);
		initializeUI();
	}

	private void initializeUI()
	{
		TextView currentCity = (TextView)getActivity().findViewById(R.id.locationTV);
		currentCity.setText(city + ", AU");

		DatePicker dp = (DatePicker)getActivity().findViewById(R.id.datePicker);

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		dp.init(year,month,day,dateChangeHandler); // setup initial values and reg. handler
		updateTime(year, month, day);
	}

	private void updateTime(int year, int monthOfYear, int dayOfMonth)
	{
		TimeZone tz = TimeZone.getTimeZone(timezone);
		GeoLocation geolocation = new GeoLocation(city, latitude, longitude, tz);
		AstronomicalCalendar ac = new AstronomicalCalendar(geolocation);
		ac.getCalendar().set(year, monthOfYear, dayOfMonth);
		Date srise = ac.getSunrise();
		Date sset = ac.getSunset();

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

		TextView sunriseTV = (TextView) getActivity().findViewById(R.id.sunriseTimeTV);
		TextView sunsetTV = (TextView) getActivity().findViewById(R.id.sunsetTimeTV);
		Log.d("SUNRISE Unformatted", srise + "");

		sunriseTV.setText(sdf.format(srise));
		sunsetTV.setText(sdf.format(sset));
	}


	DatePicker.OnDateChangedListener dateChangeHandler = new DatePicker.OnDateChangedListener()
	{
		public void onDateChanged(DatePicker dp, int year, int monthOfYear, int dayOfMonth)
		{
			updateTime(year, monthOfYear, dayOfMonth);
		}
	};

	private void openReadFile(){
		//open the file
		try {
			File file = new File(this.getActivity().getFilesDir(), "new_city.txt");
			FileInputStream in = new FileInputStream(file);

			byte[] bytes = new byte[(int)file.length()];
			in.read(bytes);
			in.close();

			//write to activity default variables
			String[] contents = new String(bytes).split("\n");
			city = contents[0];
			latitude = Double.parseDouble(contents[1]);
			longitude = Double.parseDouble(contents[2]);
			timezone = contents[3];
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}