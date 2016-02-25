package swindroid.suntime.ui;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TimeZone;

import swindroid.suntime.R;
import swindroid.suntime.calc.GeoLocation;

/**
 * Created by Simon on 9/20/2015.
 */
public class CityListFragment extends ListFragment {

    private LinkedHashMap<String, GeoLocation> locations = new LinkedHashMap<String, GeoLocation>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.tab_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialiseUI();
    }


    private void initialiseUI() {
        //current city and coordinates
        try {
            File file = new File(this.getActivity().getFilesDir(), "new_city.txt");
            FileInputStream in = new FileInputStream(file);

            byte[] bytes = new byte[(int)file.length()];
            in.read(bytes);
            in.close();

            //write to activity default variables
            String[] contents = new String(bytes).split("\n");
            String city = contents[0];
            double latitude = Double.parseDouble(contents[1]);
            double longitude = Double.parseDouble(contents[2]);

            //set the field
            TextView selectedCity = (TextView)getActivity().findViewById(R.id.currentCity);
            selectedCity.setText(city);
            TextView cityCoordinates = (TextView)getActivity().findViewById(R.id.cityCoordinates);
            cityCoordinates.setText(String.format("%.2f", latitude) + ", " + String.format("%.2f", longitude));
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        //test write some new values to internal storage
        try{
            FileOutputStream write = getActivity().openFileOutput("new_locations.txt", 0);
            OutputStreamWriter out = new OutputStreamWriter(write);
            out.write("Wodonga,-36.1214,146.8881,Australia/Melbourne\n");
            out.write("Portsea,-38.3200,144.7130,Australia/Melbourne");
            out.flush();
            out.close();

            File file = new File(this.getActivity().getFilesDir(), "new_locations.txt");
            FileInputStream in = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            in.read(bytes);
            in.close();
            String[] contents = new String(bytes).split("\n");

            formatRawString(contents[0]);
            formatRawString(contents[1]);
        }
        catch(IOException e){
            e.printStackTrace();
        }



        //Data source
        //populate list with remaining values from asset file
        try {
            //open the file and read the data
            InputStream data = getActivity().getAssets().open("au_locations.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(data));

            //populate the data source
            String str = reader.readLine();
            while(str != null){
                formatRawString(str);
                str = reader.readLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        //Adapter
        //format the data for display
        Set lkeys = locations.keySet();
        String[] cities = (String[])lkeys.toArray(new String[lkeys.size()]);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, cities);


        //List view
        //set content in view
        ListView lv = getListView();
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //city name from the list
                        String name = String.valueOf(parent.getItemAtPosition(position));
                        TextView selectedCity = (TextView) getActivity().findViewById(R.id.currentCity);
                        selectedCity.setText(name);


                        //geolocation data about that city
                        GeoLocation location = locations.get(name);
                        String timezone = location.getTimeZone().getID();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        TextView cityCoordinates = (TextView) getActivity().findViewById(R.id.cityCoordinates);
                        cityCoordinates.setText(String.format("%.2f", latitude) + ", "
                                + String.format("%.2f", longitude));

                        //save to internal storage file
                        try {
                            FileOutputStream write = getActivity().openFileOutput("new_city.txt", 0);
                            OutputStreamWriter out = new OutputStreamWriter(write);
                            out.write(name + "\n" + latitude + "\n" + longitude + "\n" + timezone);
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private void formatRawString(String datastr){
        String city = datastr.substring(0, datastr.indexOf(","));

        String[] coordinates = datastr.substring(datastr.indexOf(",")+1, datastr.lastIndexOf(",")).split(",");
        double latitude = Double.parseDouble(coordinates[0]);
        double longitude = Double.parseDouble(coordinates[1]);

        String timezone = datastr.substring(datastr.lastIndexOf(",")+1);

        locations.put(city, new GeoLocation(city, latitude, longitude, TimeZone.getTimeZone(timezone)));
    }
}