package com.ezeepay.services;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdView;

public class Mainpage_Activity extends FragmentActivity
{
	private boolean theme_style;
	private connection_check check = new connection_check(this);
	private String mpin = "", loginid = "", main_balance = "", pinstore = "", username = "", email = "", phone = "", points = "";
	private String resultstatus = "", methodname = "", errmsg = "", URL = "",device_version="";
	private int flag = 99;
	String ver_status = "";

	AdView ad;
	private Bundle bundle;
	private String nvdrawer_menuitems[] =
	{ "View/Cancel Tickets", "Send Feedback", "FAQ", "Favorites", "About" };
	private int nvdrawer_icons[] =
	{ R.drawable.sub_viewtickets, R.drawable.sub_sendfeedback, R.drawable.faq, R.drawable.favourites, R.drawable.aboutus };

	private DrawerLayout drawer;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// private CharSequence mTitle;
	// private CharSequence mDrawerTitle;
	// private String[] mPlanetTitles;
	// private ListView navList;

	private TextView loyalty_points_label;

	@Override
	public void onCreate(Bundle savedInstanceState)

	{
		try
		{
			final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
			theme_style = prefs_theme.getBoolean("pref_darktheme", false);
			if (theme_style)
				setTheme(android.R.style.Theme_Holo);
			else
				setTheme(android.R.style.Theme_Holo_Light);

			super.onCreate(savedInstanceState);
			setContentView(R.layout.mainpage_layout);

			getServerResponse();

			new Change_Log(this).show();

			SharedPreferences prefs = this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			loginid = prefs.getString("loginid", "");
			username = prefs.getString("username", "");
			email = prefs.getString("email", "");
			phone = prefs.getString("phone", "");
			main_balance = prefs.getString("balance", "");
			points = prefs.getString("loyalty_points", "");
			pinstore = prefs.getString("pinstore", "");

			URL = selectwebservice.currentwebservice();

			// //INITIALAIZE VARIABLES
			TextView username_label = (TextView) findViewById(R.id.username_label);
			TextView balancedesc_label = (TextView) findViewById(R.id.balancedesc_label);
			loyalty_points_label = (TextView) findViewById(R.id.loyalty_points_label);

			Spannable spannable = (Spannable) username_label.getText();
			StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
			int len = username_label.length();
			spannable.setSpan(boldSpan, 8, len, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			username_label.setText("Welcome " + username);

			DecimalFormat formatter = new DecimalFormat("#,###.00");
			// balancedesc_label.setText("Wallet Balance" + "\u20B9 " +
			// formatter.format((main_balance)));

			loyalty_points_label.setText("Loyalty Points : " + points);

			// LocationManager mlocManager = (LocationManager)
			// getSystemService(Context.LOCATION_SERVICE);
			// LocationListener mlocListener = new MyLocationListener();
			// mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
			// 0, 0, mlocListener);
		}
		catch (Exception e)
		{
			Log.e("fatal", "Fatal error in mainpage\n" + e.toString());
		}

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
		{

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view)
			{
				getActionBar().setTitle("EzeePay");
				super.onDrawerClosed(view);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView)
			{
				getActionBar().setTitle("Quick Navigation");
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		this.getActionBar().setDisplayHomeAsUpEnabled(false);
		this.getActionBar().setHomeButtonEnabled(true);
		this.getActionBar().setIcon(R.drawable.ic_drawer);
		this.getActionBar().setTitle("EzeePay");
		// drawer.closeDrawer(mDrawerList);
		mDrawerList = (ListView) findViewById(R.id.drawer);
		if (theme_style)
			mDrawerList.setBackgroundResource(R.color.DimGray);
		mDrawerList.setAdapter(new custommenudrawer_adapter(this, R.layout.spinneritems_withimages, nvdrawer_menuitems, nvdrawer_icons));
		mDrawerList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int pos, long id)
			{
				mDrawerLayout.closeDrawer(mDrawerList);
				if (pos == 0)
				{
					Intent intent1 = new Intent(Mainpage_Activity.this, Viewtickets_Activity.class);
					startActivity(intent1);
				}
				else if (pos == 1)
				{
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");
					i.putExtra(Intent.EXTRA_EMAIL, new String[]
					{ "support@ezeepayservices.com" });
					i.putExtra(Intent.EXTRA_SUBJECT, "Application feedback from " + loginid);
					try
					{
						startActivity(Intent.createChooser(i, "Send feedback..."));
					}
					catch (android.content.ActivityNotFoundException ex)
					{
						Toast.makeText(Mainpage_Activity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
					}
				}
				else if (pos == 2)
				{
					Intent intent1 = new Intent(Mainpage_Activity.this, Help_Activity.class);
					startActivity(intent1);
				}
				else if (pos == 3)
				{
					Intent intent1 = new Intent(Mainpage_Activity.this, Favourites_Activity.class);
					intent1.putExtra("username_intent", loginid);
					startActivity(intent1);
				}
				else if (pos == 4)
				{
					aboutus();
				}
			}
		});

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Log.e("item is", String.valueOf(item.getTitle()));
		try
		{
			switch (item.getItemId())
			{
			case R.id.action_history:
			{
				Intent intent1 = new Intent(this, History_Activity.class);
				intent1.putExtra("username_intent", loginid);
				startActivity(intent1);
				break;
			}
			case R.id.action_preferences:
			{
				Intent intent1 = new Intent(this, Settings_Activity.class);
				startActivity(intent1);
				break;
			}
			case R.id.action_help:
			{
				Intent intent1 = new Intent(this, Help_Activity.class);
				startActivity(intent1);
				break;
			}
			case R.id.action_about:
			{
				aboutus();
			}
			// case R.id.action_quit:
			// {
			// // finish();
			// // System.exit(0);
			// // Intent intent = new
			// // Intent("com.google.zxing.client.android.SCAN");
			// // intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			// // startActivityForResult(intent, 0);
			// }
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onOptionsItemSelected" + e);
		}

		if (mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Pass the event to ActionBarDrawerToggle, if it returns
	// // true, then it has handled the app icon touch event
	//
	//
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// public class MyLocationListener implements LocationListener
	//
	// {
	// TextView v1 = (TextView) findViewById(R.id.textView1);
	// ImageView i1 = (ImageView) findViewById(R.id.imageView1);
	//
	// @Override
	// public void onLocationChanged(Location loc)
	//
	// {
	// loc.getLatitude();
	// loc.getLongitude();
	// double lattitude = loc.getLatitude();
	// double longitude = loc.getLongitude();
	// Geocoder gc = new Geocoder(Mainpage_Activity.this, Locale.getDefault());
	// try
	// {
	// List<Address> addresses = gc.getFromLocation(lattitude, longitude, 1);
	// StringBuilder sb = new StringBuilder();
	// if (addresses.size() > 0)
	// {
	// Address address = addresses.get(0);
	// // for(int i =0;i
	// // sb.append(address.getAddressLine(i)).append("\n");
	//
	// sb.append(address.getThoroughfare()).append("\n");
	// sb.append(address.getLocality()).append("\n");
	// // sb.append(address.getCountryName());
	// }
	// String addressString = sb.toString();
	// v1.setText(addressString);
	// i1.setImageResource(R.drawable.gps);
	// }
	// catch (Exception e)
	// {
	// Toast.makeText(Mainpage_Activity.this, "errorrrrr",
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// // String Text = "My current location is: " +
	// // "latitude"+loc.getLatitude() +
	// // "Longitud = " + loc.getLongitude();
	// }
	//
	// @Override
	// public void onProviderDisabled(String provider)
	// {
	// // Toast.makeText(getApplicationContext(), "Gps Disabled",
	// // Toast.LENGTH_SHORT).show();
	// v1.setText("GPS Disabled");
	// i1.setImageResource(R.drawable.gps_off);
	// }
	//
	// @Override
	// public void onProviderEnabled(String provider)
	// {
	// // Toast.makeText(getApplicationContext(), "Gps Enabled",
	// // Toast.LENGTH_SHORT).show();
	// v1.setText("Searching location");
	// }
	//
	// @Override
	// public void onStatusChanged(String provider, int status, Bundle extras)
	// {
	// }
	// }

	public void onfacebook_button_select(View view)
	{

		Uri u = Uri.fromParts("tel", "*123#", "");
		Intent i = new Intent(Intent.ACTION_CALL, u);
		startActivity(i);
		// startActivity(new
		// Intent("android.intent.action.CALL",Uri.parse("tel:*123" +
		// Uri.encode("#"))));
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data)
	{
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode)
		{
		case (21):
		{
			// String xx = data.getData().toString();
			// Toast.makeText(Mainpage_Activity.this, "ggg",
			// Toast.LENGTH_SHORT).show();
		}
		}
	}

	public void onshare_button_select(View view)
	{

		String message = "I tried this app for recharge and its cool\n"
				+ Uri.parse("https://play.google.com/store/apps/details?id=com.ezeepay.services");
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, message);
		startActivity(Intent.createChooser(share, "Share the news in"));

	}

	// /////////////////////////////////////////MAINSCREEN_ICONS///////////////////////////////////////////

	public void oncards_button_select(View view)
	{
		try
		{
			Toast.makeText(Mainpage_Activity.this, "Launching Soon!!", Toast.LENGTH_SHORT).show();
			// AlertDialog.Builder b = new AlertDialog.Builder(this);
			// b.setTitle("Please enter your mpin");
			// final EditText input = new EditText(this);
			// input.setInputType(InputType.TYPE_CLASS_NUMBER |
			// InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			// b.setView(input);
			// b.setPositiveButton("OK", new DialogInterface.OnClickListener()
			// {
			// @Override
			// public void onClick(DialogInterface dialog, int whichButton)
			// {
			// mpin = input.getText().toString();
			// if (mpin.equals("1234"))
			// {
			// Intent intent1 = new Intent(Mainpage_Activity.this,
			// Instapay_Activity.class);
			// startActivity(intent1);
			// }
			// }
			// });
			// b.setCancelable(false);
			// b.setNeutralButton("FORGOT MPIN", new
			// DialogInterface.OnClickListener()
			// {
			// @Override
			// public void onClick(DialogInterface dialog, int whichButton)
			// {
			// mpin = input.getText().toString();
			// Toast.makeText(Mainpage_Activity.this, "WIP",
			// Toast.LENGTH_SHORT).show();
			// }
			// });
			// b.setNegativeButton("CANCEL", null);
			// b.create().show();
		}
		catch (Exception e)
		{
			Log.e("crash", "error on oninstapay_button_select" + e);
		}
	}

	public void onpayments_button_select(final View view)
	{
		// try
		// {
		// AlertDialog.Builder b = new AlertDialog.Builder(this);
		// b.setTitle("Please enter your mpin");
		// final EditText input = new EditText(this);
		// input.setInputType(InputType.TYPE_CLASS_NUMBER |
		// InputType.TYPE_NUMBER_VARIATION_PASSWORD);
		// b.setView(input);
		// b.setPositiveButton("OK", new DialogInterface.OnClickListener()
		// {
		// @Override
		// public void onClick(DialogInterface dialog, int whichButton)
		// {
		// String text = "";
		// mpin = input.getText().toString();
		// if (!pinstore.equals(""))
		// {
		// text = new String(Base64.decode(pinstore, Base64.DEFAULT));
		// }
		//
		// if (mpin.equals(text))
		// {
		// Intent intent1 = new Intent(Mainpage_Activity.this,
		// Makepayments_Activity.class);
		// startActivity(intent1);
		// }
		// else
		// {
		// Toast.makeText(Mainpage_Activity.this, "Invalid mpin!",
		// Toast.LENGTH_SHORT).show();
		// onwallettopup_button_select(view);
		// }
		//
		// }
		// });
		// b.setCancelable(false);
		// b.setNeutralButton("FORGOT MPIN", new
		// DialogInterface.OnClickListener()
		// {
		// @Override
		// public void onClick(DialogInterface dialog, int whichButton)
		// {
		// mpin = input.getText().toString();
		// forgot_mpin(mpin);
		// }
		// });
		// b.setNegativeButton("CANCEL", null);
		// b.create().show();
		// }
		// catch (Exception e)
		// {
		// Log.e("crash", "error on onpayments_button_select\n" + e.toString());
		// }
		Intent intent1 = new Intent(Mainpage_Activity.this, Makepayments_Activity.class);
		startActivity(intent1);
	}

	public void onfundtransfer_button_select(final View view)
	{
		try
		{
			Intent intent1 = new Intent(Mainpage_Activity.this, Invitefriends_Activity.class);
			startActivity(intent1);

			// AlertDialog.Builder b = new AlertDialog.Builder(this);
			// b.setTitle("Please enter your mpin");
			// final EditText input = new EditText(this);
			// input.setInputType(InputType.TYPE_CLASS_NUMBER |
			// InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			// b.setView(input);
			// b.setPositiveButton("OK", new DialogInterface.OnClickListener()
			// {
			// @Override
			// public void onClick(DialogInterface dialog, int whichButton)
			// {
			// String text = "";
			// mpin = input.getText().toString();
			// if (!pinstore.equals(""))
			// {
			// text = new String(Base64.decode(pinstore, Base64.DEFAULT));
			// }
			//
			// if (mpin.equals(text))
			// {
			// Intent intent1 = new Intent(Mainpage_Activity.this,
			// Makepayments_Activity.class);
			// startActivity(intent1);
			// }
			// else
			// {
			// Toast.makeText(Mainpage_Activity.this, "Invalid mpin!",
			// Toast.LENGTH_SHORT).show();
			// onfundtransfer_button_select(view);
			// }
			//
			// }
			// });
			// b.setCancelable(false);
			// b.setNeutralButton("FORGOT MPIN", new
			// DialogInterface.OnClickListener()
			// {
			// @Override
			// public void onClick(DialogInterface dialog, int whichButton)
			// {
			// mpin = input.getText().toString();
			// forgot_mpin(mpin);
			// }
			// });
			// b.setNegativeButton("CANCEL", null);
			// b.create().show();
		}
		catch (Exception e)
		{
			Log.e("crash", "error on fundtransfer_button_select\n" + e.toString());
		}
	}

	public void onwallettopup_button_select(final View view)
	{
		try
		{
			Intent intent1 = new Intent(Mainpage_Activity.this, Quickpaydata_Activity.class);
			startActivity(intent1);
			// AlertDialog.Builder b = new AlertDialog.Builder(this);
			// b.setTitle("Please enter your mpin");
			// final EditText input = new EditText(this);
			// input.setInputType(InputType.TYPE_CLASS_NUMBER |
			// InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			// b.setView(input);
			// b.setPositiveButton("OK", new DialogInterface.OnClickListener()
			// {
			// @Override
			// public void onClick(DialogInterface dialog, int whichButton)
			// {
			// String text = "";
			// mpin = input.getText().toString();
			// if (!pinstore.equals(""))
			// {
			// text = new String(Base64.decode(pinstore, Base64.DEFAULT));
			// }
			//
			// if (mpin.equals(text))
			// {
			// //Intent intent1 = new Intent(Mainpage_Activity.this,
			// Wallettopup_Activity.class);
			// //startActivity(intent1);
			// Intent intent1 = new Intent(Mainpage_Activity.this,
			// Invitefriends_Activity.class);
			// startActivity(intent1);
			// }
			// else
			// {
			// Toast.makeText(Mainpage_Activity.this, "Invalid mpin!",
			// Toast.LENGTH_SHORT).show();
			// onwallettopup_button_select(view);
			// }
			//
			// }
			// });
			// b.setCancelable(false);
			// b.setNeutralButton("FORGOT MPIN", new
			// DialogInterface.OnClickListener()
			// {
			// @Override
			// public void onClick(DialogInterface dialog, int whichButton)
			// {
			// mpin = input.getText().toString();
			// forgot_mpin(mpin);
			// }
			// });
			// b.setNegativeButton("CANCEL", null);
			// b.create().show();
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onwallettopup_button_select" + e);
		}
	}

	// /////////////////////////////////////////MAINSCREEN_BOTTOM_ICONS///////////////////////////////////////////

	public void onmyprofile_button_select(View view)
	{
		try
		{
			Intent intent1 = new Intent(this, ViewprofileActivity.class);
			startActivity(intent1);
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onmyprofile_button_select" + e);
		}
	}

	public void onpreference_button_select(View view)
	{
		try
		{
			Intent intent1 = new Intent(this, Settings_Activity.class);
			startActivity(intent1);
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onpreference_button_select" + e);
		}
	}

	public void onhistory_button_select(View view)
	{
		try
		{
			Intent intent1 = new Intent(this, History_Activity.class);
			startActivity(intent1);
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onhistory_button_select" + e);
		}
	}

	public void oninvitefriends_button_select(View view)
	{
		try
		{
			Intent intent1 = new Intent(this, Invitefriends_Activity.class);
			startActivity(intent1);
		}
		catch (Exception e)
		{
			Log.e("crash", "error on oninvitefriends_button_select" + e);
		}
	}

	public void onlogout_button_select(View view)
	{
		try
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setMessage("Logout of your account?");
			alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					Mainpage_Activity.this.finish();
				}
			});
			alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					dialog.cancel();
				}
			});
			AlertDialog alertDialog = alert.create();
			alertDialog.show();
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onlogout_button_select" + e);
		}
	}

	// /////////////////////////////////////////MAINPAGE_MENU///////////////////////////////////////////

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			onlogout_button_select(null);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void aboutus()
	{
		try
		{
			LayoutInflater inflater = getLayoutInflater();

			final AlertDialog.Builder b = new AlertDialog.Builder(this);

			try
			{
				PackageInfo pi = this.getPackageManager().getPackageInfo(this.getPackageName(),
						PackageManager.GET_ACTIVITIES);
				device_version = pi.versionName;
			}
			catch (PackageManager.NameNotFoundException e)
			{
				Log.e("error", "cannot get version name");
			}

			b.setTitle("EzeePay "+device_version);
			final TextView ab = new TextView(this);
			ab.setGravity(Gravity.CENTER);
			ab.setPadding(10, 0, 10, 0);
			ab.setMovementMethod(new ScrollingMovementMethod());
			ab.setText("		EzeePay is a new player providing Cards, Kiosks and Mobile Payments in Indian Payments Industry.Primary focus is on providing customer convenience combined with transparent, secure and easy access for paying bills, while mobile payments offering unique payment experience."
					+ "\n\n"
					+ "		The objective of the company is to increase customer convenience with innovative technology. Our payments solutions leverage the insight, domain experience, and technology we had developed over many years � in particular around the integrated bill payment using Kiosks with nationwide merchants partnerships"
					+ "\n\n" + "�	Ezee Mobile Payment � Payments using Mobile providing flexibility in a secure way" + "");

			b.setView(ab);
			b.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int whichButton)
				{
					dialog.dismiss();

				}
			});
			b.setCancelable(false);
			b.create().show();
		}
		catch (Exception e)
		{
			Log.e("crash", "error on aboutus");
		}
	}

	public void forgot_mpin(String txn_report)
	{
		try
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Confirm mpin reset");
			builder.setMessage("This will reset your mpin and you have to create a new pin during next login,Are you sure?");
			// Add the buttons
			builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					methodname = "reset_mpin";
					new webservicecall_task().execute(methodname);
				}
			});
			builder.setNegativeButton("Try Again", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					// User cancelled the dialog
				}
			});
			// Set other dialog properties

			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();

		}
		catch (Exception e)
		{
			Log.e("crash", "error on payment_method\n" + e);
		}
	}

	// /////////////////////ASYNC FETCH FOR RESETTING MPIN///////////////////

	class webservicecall_task extends AsyncTask<String, Object, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(Mainpage_Activity.this, "", "Processing...");
		}

		@Override
		protected String doInBackground(String... params)
		{
			allowAllSSL.allowAllSSL();
			if (params[0].toString().equals("reset_mpin"))
			{
				// resultstatus = "logincheck_executed";
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/resetmpin_method";
					String METHOD_NAME = "resetmpin_method";
					String NAMESPACE = "http://services.ezeepay.com";
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo unameProp = new PropertyInfo();
					unameProp.setName("loginid");
					unameProp.setValue(loginid);
					unameProp.setType(String.class);
					request.addProperty(unameProp);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);
					SoapObject response = (SoapObject) envelope.bodyIn;

					String str = response.getProperty(0).toString();
					flag = Integer.parseInt(str);
					resultstatus = "reset_mpin_executed";
				}
				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "reset_mpin_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "reset_mpin_executed";
				}
			}

			return resultstatus;
		}

		@Override
		protected void onProgressUpdate(Object... values)
		{
			super.onProgressUpdate(values);
			// progressDialog.setProgress(10);
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			progress.dismiss();

			if (result.equals("reset_mpin_executed"))
			{
				if (flag == 0)
				{
					Toast.makeText(Mainpage_Activity.this, "MPIN reset successfully!Login to generate mpin", Toast.LENGTH_SHORT).show();
					Intent intent1 = new Intent(Mainpage_Activity.this, Login_Activity.class);
					startActivity(intent1);
				}
				else if (flag == 1)
				{
					Toast.makeText(Mainpage_Activity.this, "MPIN reset failed,please try after sometime!", Toast.LENGTH_SHORT).show();
				}
				else if (flag == 3)
				{
					Toast.makeText(Mainpage_Activity.this, "Conenction Timeout,check your internet connection" + errmsg, Toast.LENGTH_LONG)
							.show();
				}
				else if (flag == 4)
				{
					Toast.makeText(Mainpage_Activity.this, "Unknown error,please try after sometime" + errmsg, Toast.LENGTH_LONG).show();
				}

			}

		}
	}

	// **Asyn server call**
	public void getServerResponse()
	{

		Log.e("bg method", "valled");
		new AsyncTask<Void, Void, Object>()
		{

			@Override
			protected Object doInBackground(Void... params)
			{

				ver_status = "false";
				// Log.e("bg method", "valled 2 ");
				try
				{
					allowAllSSL.allowAllSSL();
					URL = selectwebservice.currentwebservice();
					String SOAP_ACTION = "http://services.ezeepay.com/version_check_method";
					String METHOD_NAME = "version_check_method";
					String NAMESPACE = "http://services.ezeepay.com";
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PackageInfo versionInfo = getPackageInfo();
					String device_version = (versionInfo.versionName);

					//Log.e("bg method version", (device_version));
					PropertyInfo unameProp = new PropertyInfo();
					unameProp.setName("device_version");
					unameProp.setValue(device_version);
					unameProp.setType(String.class);
					request.addProperty(unameProp);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);
					SoapObject response = (SoapObject) envelope.bodyIn;

					// Log.e("bg method almost done", "done");

					String str = response.getProperty(0).toString();
					ver_status = str;
					// Log.e("Update required ? ", str);
				}
				catch (SocketTimeoutException e)
				{
					Log.e("exeption damn", e.toString());
				}
				catch (Exception e)
				{
					Log.e("exeption", e.toString());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Object result)
			{
				super.onPostExecute(result);
				if (ver_status.equals("true"))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(Mainpage_Activity.this);
					builder.setTitle("New Version Available");
					builder.setMessage("New version available, Update now ?");
					// Add the buttons
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							String appName = "com.ezeepay.services";
							try
							{
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
							}
							catch (android.content.ActivityNotFoundException anfe)
							{
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
										+ appName)));
							}
						}
					});
					builder.setNegativeButton("Later", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				// Toast.makeText(getApplicationContext(), "executed!!" +
				// errmsg, Toast.LENGTH_SHORT).show();
			}
		}.execute();
	}

	private PackageInfo getPackageInfo()
	{
		PackageInfo pi = null;
		try
		{
			pi = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return pi;
	}
}
