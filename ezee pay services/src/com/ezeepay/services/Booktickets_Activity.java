package com.ezeepay.services;

import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.lorecraft.phparser.SerializedPhpParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Booktickets_Activity extends Activity implements OnCheckedChangeListener, OnClickListener
{

	private static Context mcontext;
	static List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
	connection_check check = new connection_check(this);
	private static String main_flag = "TicketBooking", cust_loginid, cust_main_balance, pref_paymentmode = "", cust_username = "",
			cust_email = "", cust_phone = "", URL, seat_details = "", methodname, outp, favourite_name, txn_id = "", res, data_type,
			resultstatus, errmsg, passengerlist = "", agelist = "", genderlist = "", temp_seat_data = "", bustype_text, tripid_text,
			trip_from, trip_to, cancellation_policy, cancellation_allowed, ptype = "Wallet", bankcd = "none", payuid = "null",
			payu_status = "none", req_id = "", device_version = "";
	private LinearLayout passenger1, passenger2, passenger3, passenger4, passenger5;

	private static int max_row = 0, max_column = 0, swap, row = 0, column = 0, mx = 0, total_rows, flag, selected_seats_count = 0,
			bus_fare = 0, result_bus_fare = 0, seat_number = 0, sleeper_flag = 0, retry_count = 0;

	private String payment_modes_names[] =
	{ "Select Payment Mode", "Credit Cards", "Debit Cards", "Net Banking" };
	private int payment_modes_images[] =
	{ 0, R.drawable.creditcards, R.drawable.creditcards2, R.drawable.netbanking };

	private TextView s_name1, s_name2, s_name3, s_name4, s_name5;
	private TextView p_name1, p_name2, p_name3, p_name4, p_name5;
	private TextView p_age1, p_age2, p_age3, p_age4, p_age5;
	private ToggleButton p_gender1, p_gender2, p_gender3, p_gender4, p_gender5;
	private TextView selectedseats_text, busfare_text, traveldate_text;
	private String[] passenger, age, gender, seatnamearray, cancellation_rows;
	private CheckBox agreeterms_checkbox;

	View seat_layout_view;
	private LayoutInflater inflater;
	private Button selectseats_button, confirmtickets_button, filter_button, policy_button;
	private static ToggleButton[] button_set_1, button_set_2;

	private static String[] boarding_list, drop_list, boardingid, new_baordinglist;
	private static Spinner boardingpoint_spinner, payment_modes_spinner;
	private List<String> list = new ArrayList<String>();
	private static TextView tripdata_text, tripdate_text, busname_text, total_selectedseats_text, totalfare_text;

	int total_tickets = 0;
	private GridLayout seat_layout_1, seat_layout_2;
	private LinearLayout deck_layout;
	private TextView div1;
	private Intent i;
	private int pay_flag = 0;
	private static Bundle bundle;
	private WebView web;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try
		{
			SharedPreferences prefs = this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			SharedPreferences default_prefs = PreferenceManager.getDefaultSharedPreferences(this);

			final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
			boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
			if (theme_style)
				setTheme(android.R.style.Theme_Holo);
			else
				setTheme(android.R.style.Theme_Holo_Light);
			URL = selectwebservice.currentwebservice();
			setContentView(R.layout.booktickets_layout);

			cust_loginid = prefs.getString("loginid", "");
			cust_username = prefs.getString("username", "");
			cust_email = prefs.getString("email", "");
			cust_phone = prefs.getString("phone", "");
			cust_main_balance = prefs.getString("balance", "");
			pref_paymentmode = default_prefs.getString("pref_paymentmode", "");

			// intialiaze stuffs

			boardingpoint_spinner = (Spinner) findViewById(R.id.boardingpoint_spinner);
			payment_modes_spinner = (Spinner) findViewById(R.id.payment_modes_spinner);

			passenger1 = (LinearLayout) findViewById(R.id.passenger1);
			s_name1 = (TextView) findViewById(R.id.seatno1_text);
			p_name1 = (TextView) findViewById(R.id.passenger1_text);
			p_age1 = (TextView) findViewById(R.id.age1_text);
			p_gender1 = (ToggleButton) findViewById(R.id.gender1_toggle);

			passenger2 = (LinearLayout) findViewById(R.id.passenger2);
			s_name2 = (TextView) findViewById(R.id.seatno2_text);
			p_name2 = (TextView) findViewById(R.id.passenger2_text);
			p_age2 = (TextView) findViewById(R.id.age2_text);
			p_gender2 = (ToggleButton) findViewById(R.id.gender2_toggle);

			passenger3 = (LinearLayout) findViewById(R.id.passenger3);
			s_name3 = (TextView) findViewById(R.id.seatno3_text);
			p_name3 = (TextView) findViewById(R.id.passenger3_text);
			p_age3 = (TextView) findViewById(R.id.age3_text);
			p_gender3 = (ToggleButton) findViewById(R.id.gender3_toggle);

			passenger4 = (LinearLayout) findViewById(R.id.passenger4);
			s_name4 = (TextView) findViewById(R.id.seatno4_text);
			p_name4 = (TextView) findViewById(R.id.passenger4_text);
			p_age4 = (TextView) findViewById(R.id.age4_text);
			p_gender4 = (ToggleButton) findViewById(R.id.gender4_toggle);

			passenger5 = (LinearLayout) findViewById(R.id.passenger5);
			s_name5 = (TextView) findViewById(R.id.seatno5_text);
			p_name5 = (TextView) findViewById(R.id.passenger5_text);
			p_age5 = (TextView) findViewById(R.id.age5_text);
			p_gender5 = (ToggleButton) findViewById(R.id.gender5_toggle);

			passenger1.setVisibility(View.GONE);
			passenger2.setVisibility(View.GONE);
			passenger3.setVisibility(View.GONE);
			passenger4.setVisibility(View.GONE);
			passenger5.setVisibility(View.GONE);

			selectseats_button = (Button) findViewById(R.id.selectseats_button);
			selectseats_button.setOnClickListener(this);

			confirmtickets_button = (Button) findViewById(R.id.confirmtickets_button);
			confirmtickets_button.setOnClickListener(this);

			policy_button = (Button) findViewById(R.id.policy_button);
			policy_button.setOnClickListener(this);

			agreeterms_checkbox = (CheckBox) findViewById(R.id.agreeterms_checkbox);

			tripdata_text = (TextView) findViewById(R.id.tripdata_text);
			tripdate_text = (TextView) findViewById(R.id.tripdate_text);
			busname_text = (TextView) findViewById(R.id.busname_text);
			total_selectedseats_text = (TextView) findViewById(R.id.total_seatsselected_text);
			totalfare_text = (TextView) findViewById(R.id.totalfare_text);

			// /// receive data from fragment_tickets.class

			i = getIntent();
			bundle = i.getExtras();
			seat_details = bundle.getString("seat_details");

			boarding_list = i.getStringArrayExtra("boarding_list");
			boardingid = new String[boarding_list.length];
			new_baordinglist = new String[boarding_list.length];

			for (int l = 0; l < boarding_list.length; l++)
			{
				String[] str_array = boarding_list[l].split("@@");
				boardingid[l] = str_array[1];
				new_baordinglist[l] = str_array[0];
			}

			tripdata_text.setText(bundle.getString("trip_from") + " to " + bundle.getString("trip_to"));
			tripdate_text.setText(bundle.getString("trip_date"));
			busname_text.setText(bundle.getString("bus_details"));
			cancellation_policy = bundle.getString("cancellation_policy");
			cancellation_allowed = bundle.getString("cancellation_allowed");
			bustype_text = bundle.getString("bus_type");
			tripid_text = bundle.getString("trip_id");

			reset_boardingpoint_spinner();
			payment_modes_spinner.setAdapter(new customspinner_adapter(Booktickets_Activity.this, R.layout.spinneritems_withimages,
					payment_modes_names, payment_modes_images));
			if (pref_paymentmode.equals("credit_cards"))
			{
				payment_modes_spinner.setSelection(1);
			}
			else if (pref_paymentmode.equals("debit_cards"))
			{
				payment_modes_spinner.setSelection(2);
			}
			else if (pref_paymentmode.equals("net_banking"))
			{
				payment_modes_spinner.setSelection(3);
			}
			else
			{

			}
			// selectseats_button.performClick();

			boardingpoint_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
				{
					// Toast.makeText(getApplicationContext(),
					// String.valueOf(boardingid[boardingpoint_spinner.getSelectedItemPosition()]),
					// Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0)
				{
					// TODO Auto-generated method stub

				}
			});
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on booktickets main method\n" + e.toString());
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean arg1)
	{
		try
		{
			String seat_text = selectedseats_text.getText().toString();
			if (button_set_1[v.getId()].isChecked())
			{
				// Toast.makeText(getBaseContext(), "seat checked " +
				// v.getTag(R.id.BUTTON_TAG_NAME).toString(),
				// Toast.LENGTH_SHORT).show();
				if (selected_seats_count < 5)
				{
					if (button_set_1[v.getId()].getTag(R.id.BUTTON_TAG_LENGTH).toString().equals("1")
							&& button_set_1[v.getId()].getTag(R.id.BUTTON_TAG_WIDTH).toString().equals("1"))
					{
						button_set_1[v.getId()].setBackgroundResource(R.drawable.seat_selected);
					}
					else
					{

						button_set_1[v.getId()].setBackgroundResource(R.drawable.sleeper_selected);
					}

					selected_seats_count = selected_seats_count + 1;
					Log.e("busfare on checked", String.valueOf(result_bus_fare));
					bus_fare = result_bus_fare * selected_seats_count;
					seat_number = 1;
					temp_seat_data = v.getTag(R.id.BUTTON_TAG_NAME).toString() + ",";
					selectedseats_text.setText(seat_text + temp_seat_data);
					busfare_text.setText("\u20B9 " + String.valueOf(bus_fare));
					traveldate_text.setText(String.valueOf(selected_seats_count));
				}
				else
				{
					Toast.makeText(getBaseContext(), "Only 5 seats can be booked per transaction", Toast.LENGTH_SHORT).show();
					button_set_1[v.getId()].setChecked(false);
					if (button_set_1[v.getId()].getTag(R.id.BUTTON_TAG_LENGTH).toString().equals("1")
							&& button_set_1[v.getId()].getTag(R.id.BUTTON_TAG_WIDTH).toString().equals("1"))
					{
						button_set_1[v.getId()].setBackgroundResource(R.drawable.seat_all);
					}
					else
					{
						button_set_1[v.getId()].setBackgroundResource(R.drawable.sleeper_available);
					}

				}
			}
			else
			{
				// Toast.makeText(getBaseContext(), "seat unchecked " +
				// v.getTag(R.id.BUTTON_TAG_NAME).toString(),
				// Toast.LENGTH_SHORT).show();

				if (button_set_1[v.getId()].getTag(R.id.BUTTON_TAG_LENGTH).toString().equals("1")
						&& button_set_1[v.getId()].getTag(R.id.BUTTON_TAG_WIDTH).toString().equals("1"))
				{
					button_set_1[v.getId()].setBackgroundResource(R.drawable.seat_all);
				}
				else
				{
					button_set_1[v.getId()].setBackgroundResource(R.drawable.sleeper_available);
				}
				selected_seats_count = selected_seats_count - 1;
				temp_seat_data = seat_text.replace(v.getTag(R.id.BUTTON_TAG_NAME).toString() + ",", "");
				selectedseats_text.setText(temp_seat_data);
				bus_fare = result_bus_fare * selected_seats_count;
				busfare_text.setText("\u20B9 " + String.valueOf(bus_fare));
				traveldate_text.setText(String.valueOf(selected_seats_count));
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on oncheckedchanged\n" + e.toString());
		}

	}

	@Override
	public void onClick(View v)
	{
		try
		{
			switch (v.getId())
			{
			case R.id.selectseats_button:
			{
				try
				{
					new Thread(new Runnable()
					{
						public void run()
						{

						}
					}).start();
					inflater = getLayoutInflater();
					seat_layout_view = inflater.inflate(R.layout.seatselection_layout, null);
					seat_layout_1 = (GridLayout) seat_layout_view.findViewById(R.id.gd1);
					seat_layout_2 = (GridLayout) seat_layout_view.findViewById(R.id.gd2);
					deck_layout = (LinearLayout) seat_layout_view.findViewById(R.id.deckname_layout);
					div1 = (TextView) seat_layout_view.findViewById(R.id.div1);
					Log.e("bustype", bustype_text);

					Matcher matcher = Pattern.compile("(Sleeper|sleeper|Semisleeper)").matcher(bustype_text.toString());
					if (matcher.find())
					{
						Matcher matcher2 = Pattern.compile("(Semi|semi)").matcher(bustype_text.toString());
						if (matcher2.find())
						{
							sleeper_flag = 0;
							seat_layout_2.setVisibility(View.GONE);
							div1.setVisibility(View.GONE);
							deck_layout.setVisibility(View.GONE);
							LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
									LinearLayout.LayoutParams.MATCH_PARENT);

							float DP = getResources().getDisplayMetrics().density;
							layoutParams.setMargins(59 * Math.round(DP), 0, 0, 0);
							Log.e("density", String.valueOf(DP));

							seat_layout_1.setLayoutParams(layoutParams);
						}
						else
						{
							sleeper_flag = 1;
							DisplayMetrics metrics = getResources().getDisplayMetrics();
							Log.e("metrics for sleeper", metrics.toString());
						}
					}
					else
					{
						sleeper_flag = 0;
						seat_layout_2.setVisibility(View.GONE);
						div1.setVisibility(View.GONE);
						deck_layout.setVisibility(View.GONE);
						LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.MATCH_PARENT);
						float DP = getResources().getDisplayMetrics().density;
						layoutParams.setMargins(59 * Math.round(DP), 0, 0, 0);
						Log.e("density", String.valueOf(DP));
						seat_layout_1.setLayoutParams(layoutParams);
					}

					selected_seats_count = 0;
					Log.e("checkpoint", "111");

					JSONObject seat_data = new JSONObject(seat_details);
					JSONArray seats = seat_data.getJSONArray("seats");
					Log.e("length", String.valueOf(seats.length()));

					final ProgressBar progress = new ProgressBar(this);
					Runnable runnable = new Runnable()
					{
						@Override
						public void run()
						{
							for (int i = 0; i <= 10; i++)
							{
								final int value = i;
								progress.post(new Runnable()
								{
									@Override
									public void run()
									{
										progress.setProgress(value);
									}
								});
							}
						}
					};
					new Thread(runnable).start();

					for (int a = 0; a < seats.length(); a++)
					{
						JSONObject e = seats.getJSONObject(a);
						if (max_row <= Integer.parseInt(e.getString("row")))
						{
							max_row = Integer.parseInt(e.getString("row"));
						}

						if (max_column <= Integer.parseInt(e.getString("column")))
						{
							max_column = Integer.parseInt(e.getString("column"));
						}
					}
					seat_layout_1.setColumnCount(max_row + 1);
					seat_layout_1.setRowCount(max_column + 1);

					if (sleeper_flag == 1)
					{
						seat_layout_2.setColumnCount(max_row + 1);
						seat_layout_2.setRowCount(max_column + 1);
					}
					Log.e("Maxrows and column", String.valueOf(max_row) + String.valueOf(max_column));
					flag = 0;
					total_rows = max_row;
					GridLayout.LayoutParams[] param = new GridLayout.LayoutParams[seats.length()];
					button_set_1 = new ToggleButton[seats.length()];
					if (sleeper_flag == 1)
					{
						button_set_2 = new ToggleButton[seats.length()];
					}
					Log.e("total seats", String.valueOf(seats.length()));

					for (int a = 0; a < seats.length(); a++)
					{
						Log.e("count", String.valueOf(a));
						JSONObject e = seats.getJSONObject(a);

						param[a] = new GridLayout.LayoutParams();
						param[a].height = LayoutParams.WRAP_CONTENT;
						param[a].width = LayoutParams.WRAP_CONTENT;
						if (sleeper_flag == 0)
						{
						}
						param[a].setGravity(Gravity.CENTER);
						// param[a].setGravity(Gravity.CENTER);
						// param[a].setGravity(Gravity.CENTER);

						param[a].topMargin = 13;
						int x;
						x = Integer.parseInt(e.getString("row"));
						if (total_rows == 5)
						{
							if (x == 0)
								x = 5;
							else if (x == 1)
								x = 4;
							else if (x == 2)
								x = 3;
							else if (x == 3)
								x = 2;
							else if (x == 4)
								x = 1;
							else if (x == 5)
								x = 0;
						}
						else if (total_rows == 4)
						{
							if (x == 0)
								x = 4;
							else if (x == 1)
								x = 3;
							else if (x == 3)
								x = 1;
							else if (x == 4)
								x = 0;
						}
						else if (total_rows == 3)
						{
							if (x == 0)
								x = 3;
							else if (x == 1)
								x = 2;
							else if (x == 2)
								x = 1;
							else if (x == 3)
								x = 0;
						}
						param[a].columnSpec = GridLayout.spec(x);
						param[a].rowSpec = GridLayout.spec(Integer.parseInt(e.getString("column")));
						if (e.getString("zIndex").equals("0"))
						{
							button_set_1[a] = new ToggleButton(this);
							button_set_1[a].setLayoutParams(param[a]);

							if (e.getString("length").equals("1") && e.getString("width").equals("1"))
							{
								button_set_1[a].setBackgroundResource(R.drawable.seat_all);
								button_set_1[a].setMinWidth(40);
								button_set_1[a].setMinHeight(40);
								button_set_1[a].setMinimumWidth(40);
								button_set_1[a].setMinimumHeight(40);
							}
							else
							{
								button_set_1[a].setBackgroundResource(R.drawable.sleeper_available);
								button_set_1[a].setMinWidth(40);
								button_set_1[a].setMinHeight(80);
								button_set_1[a].setMinimumWidth(40);
								button_set_1[a].setMinimumHeight(80);
								if (e.getString("width").equals("2"))
								{
									button_set_1[a].setRotation(90);
								}
							}

							button_set_1[a].setTextOff("");
							button_set_1[a].setTextOn("");
							button_set_1[a].setText("");
							button_set_1[a].setTag(R.id.BUTTON_TAG_NAME, e.getString("name").toString());
							button_set_1[a].setTag(R.id.BUTTON_TAG_LENGTH, e.getString("length").toString());
							button_set_1[a].setTag(R.id.BUTTON_TAG_WIDTH, e.getString("width").toString());
							button_set_1[a].setId(a);
							button_set_1[a].setOnCheckedChangeListener(this);
							Log.e("button name", e.getString("name").toString());
							seat_layout_1.addView(button_set_1[a]);
						}
						else
						{
							if (sleeper_flag == 1)
							{
								button_set_2[a] = new ToggleButton(this);
								button_set_2[a].setLayoutParams(param[a]);
								button_set_2[a].setBackgroundResource(R.drawable.sleeper_available);
								button_set_2[a].setTextOff("");
								button_set_2[a].setTextOn("");
								button_set_2[a].setMinWidth(40);
								button_set_2[a].setMinHeight(80);
								button_set_2[a].setMinimumWidth(40);
								button_set_2[a].setMinimumHeight(80);
								if (e.getString("width").equals("2"))
								{
									button_set_2[a].setRotation(90);
								}
								button_set_2[a].setText("");

								button_set_2[a].setTag(R.id.BUTTON_TAG_NAME, e.getString("name").toString());
								button_set_2[a].setTag(R.id.BUTTON_TAG_LENGTH, e.getString("length").toString());
								button_set_2[a].setTag(R.id.BUTTON_TAG_WIDTH, e.getString("width").toString());
								button_set_2[a].setId(a);
								button_set_2[a].setOnCheckedChangeListener(button_set_2_checker);
								Log.e("button name", e.getString("name").toString());
								seat_layout_2.addView(button_set_2[a]);
							}
						}

					}

					selectedseats_text = (TextView) seat_layout_view.findViewById(R.id.selectedseats_text);
					busfare_text = (TextView) seat_layout_view.findViewById(R.id.busfare_text);
					traveldate_text = (TextView) seat_layout_view.findViewById(R.id.traveldate_text);

					final AlertDialog.Builder b = new AlertDialog.Builder(this);
					b.setView(seat_layout_view);
					b.setPositiveButton("OK", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int whichButton)
						{
							totalfare_text = (TextView) findViewById(R.id.totalfare_text);
							total_selectedseats_text.setText(selectedseats_text.getText());
							totalfare_text.setText(String.valueOf(bus_fare));
							passenger_data();
						}
					});

					b.setCancelable(false);
					// b.setNegativeButton("CANCEL", null);
					b.create().show();

					if (seat_details.equals(""))
					{
						Toast.makeText(getBaseContext(), "null\n" + seat_details, Toast.LENGTH_SHORT).show();
					}
					else
					{
						methodname = "fill_seats";
						new booktickets_asynctask(this).execute(methodname);
					}
				}
				catch (Exception e)
				{
					Log.e("crash", "error on seatselection inflater\n" + errmsg.toString());
				}
				break;
			}

			case R.id.confirmtickets_button:
			{

				try
				{
					if (selected_seats_count > 0)
					{
						passengerlist = "";
						agelist = "";
						genderlist = "";
						passenger = new String[selected_seats_count + 1];
						age = new String[selected_seats_count + 1];
						gender = new String[selected_seats_count + 1];
						Log.e("seat count", String.valueOf(selected_seats_count));

						for (int i = 1; i <= selected_seats_count; i++)
						{
							Log.e("iterationn ", String.valueOf(i));

							String ba = "passenger" + i + "_text";
							Class c = R.id.class;
							Field idField = c.getDeclaredField(ba);
							TextView t = (TextView) findViewById(idField.getInt(idField));
							passenger[i] = t.getText().toString();
							if (passengerlist.equals(""))
							{
								passengerlist = passenger[i];
							}
							else
							{
								passengerlist = passengerlist + "&&&" + passenger[i];
							}

							Log.e("passenger ", i + passenger[i]);

							ba = "age" + i + "_text";
							c = R.id.class;
							idField = c.getDeclaredField(ba);
							t = (TextView) findViewById(idField.getInt(idField));
							age[i] = t.getText().toString();
							if (agelist.equals(""))
							{
								agelist = age[i];
							}
							else
							{
								agelist = agelist + "&&&" + age[i];
							}

							Log.e("age ", i + age[i]);

							ba = "gender" + i + "_toggle";
							c = R.id.class;
							idField = c.getDeclaredField(ba);
							ToggleButton t1 = (ToggleButton) findViewById(idField.getInt(idField));
							gender[i] = t1.getText().toString();
							if (genderlist.equals(""))
							{
								genderlist = gender[i];
							}
							else
							{
								genderlist = genderlist + "&&&" + gender[i];
							}
							Log.e("gender ", i + gender[i]);

						}
					}
					else
					{
						// Toast.makeText(getApplicationContext(),
						// "No seats have been selected",
						// Toast.LENGTH_SHORT).show();
					}

					Log.e("\ntotal passenger,age,gender", passengerlist + agelist + genderlist);

					if (check.isnetwork_available())
					{
						String isseats_filled = "";
						for (int i = 1; i <= selected_seats_count; i++)
						{

							String ba = "passenger" + i + "_text";
							Class c = R.id.class;
							Field idField = c.getDeclaredField(ba);
							TextView t = (TextView) findViewById(idField.getInt(idField));
							passenger[i] = t.getText().toString();
							if (passenger[i].equals(""))
							{
								isseats_filled = "no data entered";
							}
							else
							{
								isseats_filled = "";
							}
							ba = "age" + i + "_text";
							c = R.id.class;
							idField = c.getDeclaredField(ba);
							t = (TextView) findViewById(idField.getInt(idField));
							age[i] = t.getText().toString();
							if (age[i].equals(""))
							{
								isseats_filled = "no data entered";
							}
							else
							{
								isseats_filled = "";
							}
						}
						if (selected_seats_count > 0)
						{
							if (agreeterms_checkbox.isChecked())
							{
								if (payment_modes_spinner.getSelectedItemPosition() == 0)
								{
									Toast.makeText(Booktickets_Activity.this, "Please Select a payment mode", Toast.LENGTH_SHORT).show();
								}

								else if (payment_modes_spinner.getSelectedItem().toString().equals("Credit Cards")
										|| payment_modes_spinner.getSelectedItem().toString().equals("Debit Cards")
										|| payment_modes_spinner.getSelectedItem().toString().equals("Net Banking"))
								{
									if (isseats_filled.equals(""))
									{
										// call_otherpayments();
										Payments bustickets = new Payments(this);
										bustickets.show_payments_dialog(main_flag, main_flag, payment_modes_spinner.getSelectedItem()
												.toString(), totalfare_text.getText().toString().trim());
									}
									else
										Toast.makeText(Booktickets_Activity.this, "Please fill passenger data", Toast.LENGTH_SHORT).show();
								}
								else
								{
									ptype = "Wallet";
									Payments.p_state = "SUCCESS";
									methodname = "blocktickets_main";
									new booktickets_asynctask(this).execute(methodname);
								}

							}
							else
							{
								Toast.makeText(Booktickets_Activity.this, "You have to accept the terms and conditions", Toast.LENGTH_SHORT)
										.show();
							}
						}
						else
						{
							Toast.makeText(this, "No seats have been selected yet", Toast.LENGTH_SHORT).show();
						}

					}
					else
					{
						Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
					}

				}
				catch (Exception e)
				{
					Log.e("crash", "error on confirm ticket\n" + e.toString());
				}
				break;
			}
			case R.id.policy_button:
			{

				try
				{

					inflater = getLayoutInflater();
					View pl = inflater.inflate(R.layout.cancellationpolicy_layout, null);
					final AlertDialog.Builder b = new AlertDialog.Builder(this);
					TextView view_policy_text = (TextView) pl.findViewById(R.id.view_policy_text);
					TextView cancellation_allowed_text = (TextView) pl.findViewById(R.id.cancellation_allowed_text);

					cancellation_allowed_text.setText(cancellation_allowed);
					b.setView(pl);
					b.setTitle("Cancellation Policy");
					String full = "";
					String[] st;
					// cancellation_policy
					cancellation_rows = cancellation_policy.split(";");
					st = new String[cancellation_rows.length];
					for (int m = 0; m < cancellation_rows.length; m++)
					{
						String[] values = cancellation_rows[m].split(":");

						st[m] = "Within " + values[0] + " hours to " + values[1] + " hours of travel : " + values[2] + "%\n";
						full = full + st[m];
						Log.e("final output", st[m]);
					}
					view_policy_text.setLines(cancellation_rows.length);
					view_policy_text.setText(full);
					b.setNegativeButton("CLOSE", null);
					b.create().show();
				}
				catch (Exception e)
				{
					Log.e("crash", "error on policy button view");
				}
				break;
			}

			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on onClick\n" + e.toString());
		}
	}

	OnCheckedChangeListener button_set_2_checker = new OnCheckedChangeListener()
	{

		@Override
		public void onCheckedChanged(CompoundButton v, boolean isChecked)
		{
			try
			{
				String seat_text = selectedseats_text.getText().toString();
				if (button_set_2[v.getId()].isChecked())
				{
					// Toast.makeText(getBaseContext(), "seat checked " +
					// v.getTag(R.id.BUTTON_TAG_NAME).toString(),
					// Toast.LENGTH_SHORT).show();
					if (selected_seats_count < 5)
					{
						button_set_2[v.getId()].setBackgroundResource(R.drawable.sleeper_selected);
						selected_seats_count = selected_seats_count + 1;
						bus_fare = result_bus_fare * selected_seats_count;
						seat_number = 1;
						temp_seat_data = v.getTag(R.id.BUTTON_TAG_NAME).toString() + ",";
						selectedseats_text.setText(seat_text + temp_seat_data);
						busfare_text.setText("\u20B9 " + String.valueOf(bus_fare));
						traveldate_text.setText(String.valueOf(selected_seats_count));
					}
					else
					{
						Toast.makeText(getBaseContext(), "Only 5 seats can be booked per transaction", Toast.LENGTH_SHORT).show();
						button_set_2[v.getId()].setChecked(false);
						button_set_2[v.getId()].setBackgroundResource(R.drawable.sleeper_available);
					}
				}
				else
				{
					// Toast.makeText(getBaseContext(), "seat unchecked " +
					// v.getTag(R.id.BUTTON_TAG_NAME).toString(),
					// Toast.LENGTH_SHORT).show();
					button_set_2[v.getId()].setBackgroundResource(R.drawable.sleeper_available);
					selected_seats_count = selected_seats_count - 1;
					temp_seat_data = seat_text.replace(v.getTag(R.id.BUTTON_TAG_NAME).toString() + ",", "");
					selectedseats_text.setText(temp_seat_data);
					bus_fare = result_bus_fare * selected_seats_count;
					busfare_text.setText("\u20B9 " + String.valueOf(bus_fare));
					traveldate_text.setText(String.valueOf(selected_seats_count));
				}

			}

			catch (Exception e)
			{
				Log.e("crash", "crash on button_set_2_checkedchanged\n" + e.toString());
			}
		}
	};

	public void passenger_data()
	{
		try
		{
			seatnamearray = total_selectedseats_text.getText().toString().split(",");
			// seatnamearray = seatnamelist.split(",");
			if (selected_seats_count == 0)
			{
				passenger1.setVisibility(View.GONE);
				passenger2.setVisibility(View.GONE);
				passenger3.setVisibility(View.GONE);
				passenger4.setVisibility(View.GONE);
				passenger5.setVisibility(View.GONE);
				Toast.makeText(getBaseContext(), "Please select atleast one seat", Toast.LENGTH_SHORT).show();
			}
			else if (selected_seats_count == 1)
			{
				passenger1.setVisibility(View.VISIBLE);
				passenger2.setVisibility(View.GONE);
				passenger3.setVisibility(View.GONE);
				passenger4.setVisibility(View.GONE);
				passenger5.setVisibility(View.GONE);
				s_name1.setText(seatnamearray[0]);
			}
			else if (selected_seats_count == 2)
			{
				passenger1.setVisibility(View.VISIBLE);
				passenger2.setVisibility(View.VISIBLE);
				passenger3.setVisibility(View.GONE);
				passenger4.setVisibility(View.GONE);
				passenger5.setVisibility(View.GONE);
				s_name1.setText(seatnamearray[0]);
				s_name2.setText(seatnamearray[1]);
			}
			else if (selected_seats_count == 3)
			{
				passenger1.setVisibility(View.VISIBLE);
				passenger2.setVisibility(View.VISIBLE);
				passenger3.setVisibility(View.VISIBLE);
				passenger4.setVisibility(View.GONE);
				passenger5.setVisibility(View.GONE);
				s_name1.setText(seatnamearray[0]);
				s_name2.setText(seatnamearray[1]);
				s_name3.setText(seatnamearray[2]);
			}
			else if (selected_seats_count == 4)
			{
				passenger1.setVisibility(View.VISIBLE);
				passenger2.setVisibility(View.VISIBLE);
				passenger3.setVisibility(View.VISIBLE);
				passenger4.setVisibility(View.VISIBLE);
				passenger5.setVisibility(View.GONE);
				s_name1.setText(seatnamearray[0]);
				s_name2.setText(seatnamearray[1]);
				s_name3.setText(seatnamearray[2]);
				s_name4.setText(seatnamearray[3]);

			}
			else if (selected_seats_count == 5)
			{
				passenger1.setVisibility(View.VISIBLE);
				passenger2.setVisibility(View.VISIBLE);
				passenger3.setVisibility(View.VISIBLE);
				passenger4.setVisibility(View.VISIBLE);
				passenger5.setVisibility(View.VISIBLE);
				s_name1.setText(seatnamearray[0]);
				s_name2.setText(seatnamearray[1]);
				s_name3.setText(seatnamearray[2]);
				s_name4.setText(seatnamearray[3]);
				s_name5.setText(seatnamearray[4]);
			}
			else
			{
				passenger1.setVisibility(View.GONE);
				passenger2.setVisibility(View.GONE);
				passenger3.setVisibility(View.GONE);
				passenger4.setVisibility(View.GONE);
				passenger5.setVisibility(View.GONE);
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on passenger_data\n" + e.toString());
		}
	}

	public void reset_boardingpoint_spinner()
	{
		try
		{
			list = new ArrayList<String>();
			list.add("Select Boardingpoint.....");
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_modified, new_baordinglist);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// dataAdapter.createFromResource(this,i, R.layout.spinneritems);
			boardingpoint_spinner.setAdapter(dataAdapter);
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on reset_boardingpoint_spinner\n" + e.toString());
		}
	}

	public void reset_paymentmode_spinner()
	{
		try
		{
			list = new ArrayList<String>();
			list.add("Select Payment mode.....");
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_modified, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// dataAdapter.createFromResource(this,i, R.layout.spinneritems);
			payment_modes_spinner.setAdapter(dataAdapter);
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on reset_payment_spinner\n" + e.toString());
		}
	}

	public static void payment_status(String txn_report)
	{
		try
		{
			final Dialog dialog = new Dialog(mcontext);
			dialog.setContentView(R.layout.paymentstatus_layout);
			dialog.setTitle("Transaction Summary");

			TextView paymentsummary1_text = (TextView) dialog.findViewById(R.id.paymentsummary1_text);
			TextView paymentsummary2_text = (TextView) dialog.findViewById(R.id.paymentsummary2_text);
			TextView paymentsummary3_text = (TextView) dialog.findViewById(R.id.paymentsummary3_text);
			final TextView favorite_alias_text = (TextView) dialog.findViewById(R.id.favorite_alias_text);

			final Button finish_button = (Button) dialog.findViewById(R.id.finish_button);
			final Button retry_button = (Button) dialog.findViewById(R.id.retry_button);

			final TextView paymentstatus_text = (TextView) dialog.findViewById(R.id.paymentstatus_text);
			final CheckBox savetofavorite_checkbox = (CheckBox) dialog.findViewById(R.id.savetofavorite_checkbox);
			JSONObject his_data = new JSONObject(outp);
			JSONArray txns = his_data.getJSONArray("booking_report");
			Log.e("report length   ", String.valueOf(txns.length()));
			JSONObject e = txns.getJSONObject(0);
			int flag = Integer.parseInt(e.getString("flag"));
			Log.e("flag   ", String.valueOf(flag));

			// if (flag == 0)
			{
				String txn_status = e.getString("booking_status");
				String txn_status_reason = e.getString("booking_status_reason");
				txn_id = e.getString("booking_id");

				if (txn_status.equals("Booked"))
				{
					paymentstatus_text.setTextColor(Color.GREEN);
					paymentstatus_text.setText(txn_status);
				}
				else
				{
					TextView hdfc_warning_text = (TextView) dialog.findViewById(R.id.hdfc_warning_text);
					hdfc_warning_text.setVisibility(View.VISIBLE);
					paymentstatus_text.setTextColor(Color.RED);
					paymentstatus_text.setText(txn_status + "," + txn_status_reason);
					retry_button.setEnabled(true);
				}

				paymentsummary1_text.setText("Your Transaction ID :" + txn_id);
				// summary2.setText("Bill/Phone Number :" +
				// payment_billno_text.getText().toString().trim());
				paymentsummary3_text.setText("Amount:" + totalfare_text.getText().toString().trim());
				paymentstatus_text.setText(txn_status + "," + txn_status_reason);

			}

			if (flag == 27)
			{
				paymentstatus_text.setTextColor(Color.BLUE);
				paymentstatus_text.setText("Conenction Timeout");

			}
			else if (flag == 4)
			{
				paymentstatus_text.setTextColor(Color.RED);
				paymentstatus_text.setText("Insufficent balance");
			}
			else
			{

			}

			finish_button.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (savetofavorite_checkbox.isChecked())
					{
						// Toast.makeText(getActivity(), "favorites checked",
						// Toast.LENGTH_SHORT).show();

						if (favorite_alias_text.getText().toString().equals(""))
						{
							Toast.makeText(mcontext, "Please enter a name for this transaction", Toast.LENGTH_SHORT).show();
						}
						else
						{
							favourite_name = favorite_alias_text.getText().toString().trim();
							// methodname = "update_favorites";
							// new transactioncall_task().execute(methodname);
							dialog.dismiss();
							((Activity) mcontext).finish();
							Toast.makeText(mcontext, "Favourites added successfully", Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						dialog.dismiss();
						((Activity) mcontext).finish();
					}

				}
			});
			retry_button.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialog.dismiss();
					if (payuid.equals("null"))
					{
						Payments bustickets = new Payments(mcontext);
						bustickets.show_payments_dialog(main_flag, main_flag, payment_modes_spinner.getSelectedItem().toString(),
								totalfare_text.getText().toString().trim());
					}
					else
					{
						if (retry_count < 1)
						{
							methodname = "blocktickets_main";
							new booktickets_asynctask(mcontext).execute(methodname);
							retry_count = retry_count + 1;
						}
						else
						{
							methodname = "call_refund";
							new booktickets_asynctask(mcontext).execute(methodname);
						}
					}

				}
			});

			savetofavorite_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{

					if (buttonView.isChecked())
					{
						favorite_alias_text.setVisibility(View.VISIBLE);

						// Toast.makeText(getActivity(),
						// "favorites text name enabled",
						// Toast.LENGTH_SHORT).show();

					}
					else
					{
						// not checked
						Toast.makeText(mcontext, "Removed from favorites", Toast.LENGTH_SHORT).show();
					}
				}
			});

			dialog.show();

		}
		catch (Exception e)
		{
			Log.e("crash", "error on payment_method\n" + e);
		}
	}

	static class booktickets_asynctask extends AsyncTask<String, Object, String>
	{
		ProgressDialog progress;

		public booktickets_asynctask(Context context)
		{
			mcontext = context;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(mcontext, "", "Please wait...");
		}

		@Override
		protected String doInBackground(String... params)
		{
			if (params[0].toString().equals("fill_seats"))
			{
				resultstatus = "fill_seats_executed";
				try
				{
					JSONObject seat_data = new JSONObject(seat_details);
					JSONArray seats = seat_data.getJSONArray("seats");
					Log.e("length", String.valueOf(seats.length()));
					for (int a = 0; a < seats.length(); a++)
					{
						Log.e("count", String.valueOf(a));
						JSONObject e = seats.getJSONObject(a);
						if (e.getString("zIndex").equals("0"))
						{
							if (e.getString("available").toString().equals("false"))
							{
								if (e.getString("length").equals("1") && e.getString("width").equals("1"))
								{
									button_set_1[a].setBackgroundResource(R.drawable.seat_booked);
								}
								else
								{
									button_set_1[a].setBackgroundResource(R.drawable.sleeper_booked);
								}
								button_set_1[a].setEnabled(false);
							}

							else if (e.getString("ladiesSeat").toString().equals("true"))
							{
								if (e.getString("length").equals("1") && e.getString("width").equals("1"))
								{
									button_set_1[a].setBackgroundResource(R.drawable.seat_ladies);
								}
								else
								{
									button_set_1[a].setBackgroundResource(R.drawable.sleeper_ladies);
								}
							}
						}
						else
						{
							if (sleeper_flag == 1)
							{
								if (e.getString("available").toString().equals("false"))
								{
									button_set_2[a].setBackgroundResource(R.drawable.sleeper_booked);
									button_set_2[a].setEnabled(false);
								}

								else if (e.getString("ladiesSeat").toString().equals("true"))
								{
									button_set_2[a].setBackgroundResource(R.drawable.sleeper_ladies);
								}
							}

						}
						if (!((int) Double.parseDouble(e.getString("fare")) == 0))
						{
							result_bus_fare = (int) Double.parseDouble(e.getString("fare"));
							Log.e("result_bus_fare", String.valueOf(result_bus_fare));
						}
					}
					flag = 0;
					resultstatus = "fill_seats_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = errmsg + e.toString();
					resultstatus = "fill_seats_executed";
				}
			}
			else if (params[0].toString().equals("blocktickets_main"))
			{
				resultstatus = "blocktickets_executed";
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/blocktickets_method";
					String METHOD_NAME = "blocktickets_method";
					String NAMESPACE = "http://services.ezeepay.com";
					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo loginidprop = new PropertyInfo();
					loginidprop.setName("loginid");
					loginidprop.setValue(cust_loginid);
					loginidprop.setType(String.class);
					request.addProperty(loginidprop);

					PropertyInfo tripid_prop = new PropertyInfo();
					tripid_prop.setName("tripid");
					Log.e("tripid", bundle.getString("trip_id").toString());
					tripid_prop.setValue(bundle.getString("trip_id").toString());
					tripid_prop.setType(String.class);
					request.addProperty(tripid_prop);

					PropertyInfo source_prop = new PropertyInfo();
					source_prop.setName("source");
					Log.e("trip from", bundle.getString("from_id").toString());
					source_prop.setValue(bundle.getString("from_id").toString());
					source_prop.setType(String.class);
					request.addProperty(source_prop);

					PropertyInfo boarding_prop = new PropertyInfo();
					boarding_prop.setName("boardingpointid");
					Log.e("boardingpoint", boardingid[boardingpoint_spinner.getSelectedItemPosition()].toString());
					boarding_prop.setValue(boardingid[boardingpoint_spinner.getSelectedItemPosition()].toString());
					boarding_prop.setType(String.class);
					request.addProperty(boarding_prop);

					PropertyInfo destination_prop = new PropertyInfo();
					destination_prop.setName("destination");
					Log.e("trip to", bundle.getString("to_id").toString());
					destination_prop.setValue(bundle.getString("to_id").toString());
					destination_prop.setType(String.class);
					request.addProperty(destination_prop);

					PropertyInfo busname_prop = new PropertyInfo();
					busname_prop.setName("busname");
					Log.e("busname", busname_text.getText().toString());
					busname_prop.setValue(busname_text.getText().toString());
					busname_prop.setType(String.class);
					request.addProperty(busname_prop);

					PropertyInfo tripdate_prop = new PropertyInfo();
					tripdate_prop.setName("tripdate");
					Log.e("trip date", tripdate_text.getText().toString());
					tripdate_prop.setValue(tripdate_text.getText().toString());
					tripdate_prop.setType(String.class);
					request.addProperty(tripdate_prop);

					PropertyInfo seat_prop = new PropertyInfo();
					seat_prop.setName("seatnamelist");
					Log.e("seatlist", total_selectedseats_text.getText().toString());
					seat_prop.setValue(total_selectedseats_text.getText().toString());
					seat_prop.setType(String.class);
					request.addProperty(seat_prop);

					PropertyInfo passenger_prop = new PropertyInfo();
					passenger_prop.setName("passengerlist");
					Log.e("passengerlist", passengerlist);
					passenger_prop.setValue(passengerlist);
					passenger_prop.setType(String.class);
					request.addProperty(passenger_prop);

					PropertyInfo age_prop = new PropertyInfo();
					age_prop.setName("agelist");
					Log.e("agelist", agelist);
					age_prop.setValue(agelist);
					age_prop.setType(String.class);
					request.addProperty(age_prop);

					PropertyInfo gender_prop = new PropertyInfo();
					gender_prop.setName("genderlist");
					Log.e("genderlist", genderlist);
					gender_prop.setValue(genderlist);
					gender_prop.setType(String.class);
					request.addProperty(gender_prop);

					PropertyInfo totalamount_prop = new PropertyInfo();
					totalamount_prop.setName("total_amount");
					Log.e("total_amount", totalfare_text.getText().toString().trim());
					totalamount_prop.setValue(totalfare_text.getText().toString().trim());
					totalamount_prop.setType(String.class);
					request.addProperty(totalamount_prop);

					PropertyInfo payment_through = new PropertyInfo();
					payment_through.setName("payment_through");
					String pmode = "";
					// if (payment_modes_spinner.getSelectedItemPosition() == 1)
					// pmode = "W";
					if (payment_modes_spinner.getSelectedItemPosition() == 1)
						pmode = "CC";
					else if (payment_modes_spinner.getSelectedItemPosition() == 2)
						pmode = "DC";
					else if (payment_modes_spinner.getSelectedItemPosition() == 3)
						pmode = "NB";
					payment_through.setValue(pmode);
					payment_through.setType(String.class);
					request.addProperty(payment_through);

					PropertyInfo payment_state = new PropertyInfo();
					payment_state.setName("payment_state");
					payment_state.setValue(Payments.p_state);
					payment_state.setType(String.class);
					request.addProperty(payment_state);

					String payuid = "null", payu_status = "none";

					try
					{
						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(Payments.murl);
						String responseString = "";
						Log.e("inside", "pay reponse parsing");
						ResponseHandler<String> res = new BasicResponseHandler();
						responseString = httpclient.execute(httppost, res);
						Log.e("Entering jsoup", "1");
						Document doc = Jsoup.parse(responseString);
						Elements inputs = doc.select("input[type=hidden]");
						Log.e("Entering jsoup", "2");
						for (Element el : inputs)
						{
							if (el.attr("name").toString().equals("mihpayid"))
								payuid = el.attr("value");
							if (el.attr("name").toString().equals("unmappedstatus"))
								payu_status = el.attr("value");
						}
					}
					catch (Exception ex)
					{
						Log.e("error in fetching status from gateway", ex.toString());
					}

					PropertyInfo payuid_prop = new PropertyInfo();
					payuid_prop.setName("payuid");
					payuid_prop.setValue(payuid);
					payuid_prop.setType(String.class);
					request.addProperty(payuid_prop);

					PropertyInfo payu_status_prop = new PropertyInfo();
					payu_status_prop.setName("payu_status");
					payu_status_prop.setValue(payu_status);
					payu_status_prop.setType(String.class);
					request.addProperty(payu_status_prop);

					try
					{
						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost("https://info.payu.in/merchant/postservice");
						try
						{
							Log.e("inside", "test bg");
							String key = "asjjZn";
							String salt = "dHzwOBnl";
							String command = "check_payment";
							String tempe = "";
							String var1 = payuid;
							tempe = key + "|" + command + "|" + var1 + "|" + salt;
							parameters.add(new BasicNameValuePair("key", key));
							parameters.add(new BasicNameValuePair("command", command));
							parameters.add(new BasicNameValuePair("hash", hashCal("SHA-512", tempe)));
							parameters.add(new BasicNameValuePair("var1", var1));

							httppost.setEntity(new UrlEncodedFormEntity(parameters));
							ResponseHandler<String> res = new BasicResponseHandler();
							String responseString = httpclient.execute(httppost, res);
							SerializedPhpParser serializedPhpParser = new SerializedPhpParser(responseString);
							serializedPhpParser.setAcceptedAttributeNameRegex("transaction_details|request_id");
							Object result = serializedPhpParser.parse();
							String all = result.toString().replace("{", "").replace("}", "");
							String data[] = all.split("=");
							req_id = data[2];
							Log.e("request_id is ", data[2]);
						}
						catch (ClientProtocolException e)
						{
							Log.e("Clientpexcept", e.toString());
						}
						catch (Exception e)
						{
							Log.e("IOexcep", e.toString());
						}
					}
					catch (Exception e)
					{
						Log.e("Error", "Error requesting id");
					}

					PropertyInfo payu_requestid_prop = new PropertyInfo();
					payu_requestid_prop.setName("payu_requestid");
					payu_requestid_prop.setValue(req_id);
					payu_requestid_prop.setType(String.class);
					request.addProperty(payu_requestid_prop);

					try
					{
						PackageInfo pi = mcontext.getPackageManager().getPackageInfo(mcontext.getPackageName(),
								PackageManager.GET_ACTIVITIES);
						device_version = pi.versionName;
					}
					catch (PackageManager.NameNotFoundException e)
					{
						Log.e("error", "cannot get version name");
					}

					PropertyInfo versionProp = new PropertyInfo();
					versionProp.setName("app_version");
					versionProp.setValue(device_version);
					versionProp.setType(String.class);
					request.addProperty(versionProp);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;
					resultstatus = "blocktickets_executed";
					flag = 1;
				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "blocktickets_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "blocktickets_executed";
				}
			}
			else if (params[0].toString().equals("call_refund"))
			{

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("https://info.payu.in/merchant/postservice");
				try
				{
					Log.e("inside", "test bg");
					String key = "asjjZn";
					String salt = "dHzwOBnl";
					String command = "cancel_refund_transaction";
					String tempe = "";
					String var1 = payuid;
					String tok = req_id;
					String amt = totalfare_text.getText().toString().trim();
					tempe = key + "|" + command + "|" + var1 + "|" + salt;
					parameters.add(new BasicNameValuePair("key", key));
					parameters.add(new BasicNameValuePair("command", command));
					parameters.add(new BasicNameValuePair("hash", hashCal("SHA-512", tempe)));
					parameters.add(new BasicNameValuePair("var1", var1));
					parameters.add(new BasicNameValuePair("var2", tok));
					parameters.add(new BasicNameValuePair("var3", amt));

					httppost.setEntity(new UrlEncodedFormEntity(parameters));
					ResponseHandler<String> res = new BasicResponseHandler();
					String responseString = httpclient.execute(httppost, res);
					SerializedPhpParser serializedPhpParser = new SerializedPhpParser(responseString);
					serializedPhpParser.setAcceptedAttributeNameRegex("request_id");
					Object result = serializedPhpParser.parse();
					String all = result.toString().replace("{", "").replace("}", "");
					Log.e("refund output", all);
					String data[] = all.split("=");
					Log.e("request_id is ", data[1]);

					String SOAP_ACTION = "http://services.ezeepay.com/callrefund_method";
					String METHOD_NAME = "callrefund_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo payuid_prop = new PropertyInfo();
					payuid_prop.setName("payuid");
					payuid_prop.setValue(payuid);
					payuid_prop.setType(String.class);
					request.addProperty(payuid_prop);

					PropertyInfo payu_requestid_prop = new PropertyInfo();
					payu_requestid_prop.setName("payu_refund_status_id");
					payu_requestid_prop.setValue(data[1]);
					payu_requestid_prop.setType(String.class);
					request.addProperty(payu_requestid_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;
					resultstatus = "call_refund_executed";
				}
				catch (ClientProtocolException e)
				{
					Log.e("Clientpexcept", e.toString());
				}
				catch (Exception e)
				{
					Log.e("IOexcep", e.toString());
				}

				resultstatus = "call_refund_executed";

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
			if (result.equals("fill_seats_executed"))
			{
				if (flag == 0)
				{
					// Toast.makeText(getBaseContext(), "executed" + row +
					// column, Toast.LENGTH_SHORT).show();
				}
				else if (flag == 4)
				{
					Toast.makeText(mcontext, "error\n" + errmsg, Toast.LENGTH_LONG).show();
				}
			}
			else if (result.equals("blocktickets_executed"))
			{
				{
					// Toast.makeText(getBaseContext(), "Response\n" + outp,
					// Toast.LENGTH_LONG).show();
					payment_status(outp);
				}
			}
			else if (resultstatus.equals("call_refund_executed"))
			{
				Toast.makeText(mcontext, "Transaction amount will be refunded to your account shortly", Toast.LENGTH_SHORT).show();
			}

		}
	}

	public static String hashCal(String type, String str)
	{
		byte[] hashseq = str.getBytes();
		StringBuffer hexString = new StringBuffer();
		try
		{
			MessageDigest algorithm = MessageDigest.getInstance(type);
			algorithm.reset();
			algorithm.update(hashseq);
			byte messageDigest[] = algorithm.digest();

			for (int i = 0; i < messageDigest.length; i++)
			{
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append("0");
				hexString.append(hex);
			}

		}
		catch (NoSuchAlgorithmException nsae)
		{
		}

		return hexString.toString();

	}
}
