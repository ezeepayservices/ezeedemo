package com.ezeepay.services;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PrefFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
//		if (theme_style)
//			getActivity().setTheme(android.R.style.Theme_Holo);
//		else
//			getActivity().setTheme(android.R.style.Theme_Holo_Light);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(getActivity());

		boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
		if (theme_style)
		{
			setBackgroundColorForViewTree((ViewGroup) getActivity().getWindow().getDecorView(),
					getResources().getColor(R.color.background_dark));
		}
//		else
//			setBackgroundColorForViewTree((ViewGroup) getActivity().getWindow().getDecorView(),
//					getResources().getColor(R.color.background_light));

	}

	private static void setBackgroundColorForViewTree(ViewGroup viewGroup, int color)
	{
		for (int i = 0; i < viewGroup.getChildCount(); i++)
		{
			View child = viewGroup.getChildAt(i);
			if (child instanceof ViewGroup)
				setBackgroundColorForViewTree((ViewGroup) child, color);
			child.setBackgroundColor(color);
		}
		viewGroup.setBackgroundColor(color);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String val)
	{
		if (val.equals("pref_darktheme"))
		{
			Toast.makeText(getActivity(), "Theme Changed ! Restart the aplication to see the changes", Toast.LENGTH_LONG).show();
		}
		else if (val.equals("pref_paymentmode"))
		{
			Toast.makeText(getActivity(), "Preferred payment mode set !", Toast.LENGTH_LONG).show();
		}
	}

}