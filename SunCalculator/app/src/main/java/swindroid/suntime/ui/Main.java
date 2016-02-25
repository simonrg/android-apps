package swindroid.suntime.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import swindroid.suntime.R;

public class Main extends Activity
{
	private Intent mShareIntent;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);

		//share intent
		mShareIntent = new Intent();
		mShareIntent.setAction(Intent.ACTION_SEND);
		mShareIntent.setType("text/plain");
		mShareIntent.putExtra(Intent.EXTRA_TEXT, "Some stuff to share with other apps");

		//create the actionbar
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

		Tab tab = actionBar.newTab();
		tab.setText("Suntime");
		TabListener<SuntimeFragment> t1 = new TabListener<SuntimeFragment>(this, "Suntime", SuntimeFragment.class);
		tab.setTabListener(t1);
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		tab.setText("Form");
		TabListener<FormFragment> t2 = new TabListener<FormFragment>(this, "Form", FormFragment.class);
		tab.setTabListener(t2);
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		tab.setText("List");
		TabListener<CityListFragment> t3 = new TabListener<CityListFragment>(this, "List", CityListFragment.class);
		tab.setTabListener(t3);
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		tab.setText("Dates");
		TabListener<DatesFragment> t4 = new TabListener<DatesFragment>(this, "Dates", DatesFragment.class);
		tab.setTabListener(t4);
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		tab.setText("Map");
		TabListener<MapFragment> t5 = new TabListener<MapFragment>(this, "Dates", MapFragment.class);
		tab.setTabListener(t5);
		actionBar.addTab(tab);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ShareActionProvider mShareActionProvider;

		//add items to actionbar
		getMenuInflater().inflate(R.menu.share, menu);

		//menu item of the share action provider
		MenuItem item = menu.findItem(R.id.menu_item_share);

		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(mShareIntent);
		}

		return true;
	}

	private class TabListener<T extends Fragment> implements ActionBar.TabListener {

		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if(mFragment == null)
			{
				//mFragment = Fragment.instantiate(mActivity, mClass.getName(), (Bundle) tab.getTag());
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if(mFragment != null)
			{
				ft.detach(mFragment);
			}
		}
	}
}