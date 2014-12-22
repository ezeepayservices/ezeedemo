package com.ezeepay.services;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings_Activity extends PreferenceActivity
{
	int style_value = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try
		{
			final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
			boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
			if (theme_style)
				setTheme(android.R.style.Theme_Holo);
			else
				setTheme(android.R.style.Theme_Holo_Light);
			getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();
		}

		catch (Exception e)
		{
			Log.e("fatal", "error on preference activity" + e);
		}
	}

	

	// sample code for back key press listener
	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // do
	 * something on back.
	 * Toast.makeText(this,"backkey",Toast.LENGTH_SHORT).show(); return true; }
	 * 
	 * return super.onKeyDown(keyCode, event); }
	 */

}
