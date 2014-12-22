package com.ezeepay.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.TextView;

public class Change_Log
{

	private String EULA_PREFIX = "changelog_";
	private Activity mActivity;

	public Change_Log(Activity context)
	{
		mActivity = context;
	}

	private PackageInfo getPackageInfo()
	{
		PackageInfo pi = null;
		try
		{
			pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return pi;
	}

	public void show()
	{
		PackageInfo versionInfo = getPackageInfo();

		// the eulaKey changes every time you increment the version number in
		// the AndroidManifest.xml
		final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
		if (hasBeenShown == false)
		{

			// Show the Eula
			String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;

			// Includes the updates as well so users know what changed.
			String message = mActivity.getString(R.string.updates) + "\n\n" + mActivity.getString(R.string.eula);
			TextView title_text = new TextView(mActivity);
			title_text.setWidth(40);
			title_text.setGravity(Gravity.CENTER_VERTICAL);
			title_text.setGravity(Gravity.RIGHT); // attempt at justifying text
			title_text.setMaxLines(1);
			title_text.setText(mActivity.getString(R.string.changelog));

			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setTitle(title).setMessage(title_text.getText())
					.setPositiveButton(android.R.string.ok, new Dialog.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{
							// Mark this version as read.
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean(eulaKey, true);
							editor.commit();
							dialogInterface.dismiss();
						}
					});
			builder.create().show();
		}
	}

}
