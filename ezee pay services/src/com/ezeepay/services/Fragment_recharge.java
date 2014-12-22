package com.ezeepay.services;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_recharge extends Fragment implements OnClickListener
{
	connection_check check;
	private static Context mcontext;
	private static String favourite_name = "", txn_id = "", outpu = "", line = "", mihpayid = "", token = "", pref_paymentmode = "",
			URL = "", methodname = "", resultstatus = "", errmsg = "", x = "", selected_circle = "", ptype = "Wallet", bankcd = "none",
			regex = "", e1 = "", e2 = "", e3 = "", e4 = "", e5 = "", main_flag = "Recharge", outp, from_fav = "", cust_loginid,
			cust_main_balance, cust_username = "", cust_email = "", cust_phone = "", loyalty_points = "", payuid = "null",
			payu_status = "none", req_id = "";
	private static int pay_flag = 0, flag, retry_count = 0;
	private static String device_version="";
	boolean autofill = false;

	static LayoutInflater inflater;
	static ListView plans_listview;
	static HashMap<String, String> map_mobileplans;
	static ListAdapter plans_adapter;
	static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	WebView sample_webview;

	private static Spinner payment_categories_spinner, payment_vendors_spinner, payment_modes_spinner, circle_spinner;
	private static TextView payment_billno_text, payment_remarks_text, payment_amount_text, rechargehint_text, divider1,
			vendor_warning_text, discount_amount_text;
	private static Button autoselect_button, plan_option_button, proceed_payment_button;
	private ImageButton speech_imagebutton, number_option_imagebutton;

	private CheckBox redeempoints_checkbox;

	// ArrayAdapter<String> adapter, adapter1, adapter2;
	View recharge_view;

	Matcher matcher;

	Map<String, String> pa = new HashMap<String, String>();
	static List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
	static List<NameValuePair> main_parameters = new ArrayList<NameValuePair>(2);

	String recharge_category_names[] =
	{ "Select Category", "Mobile/Datacard Topup", "Recharge DTH" };
	int recharge_category_images[] =
	{ 0, R.drawable.recharge_mobile, R.drawable.recharge_dth };
	// //////circles
	String circles_names[];
	int circles_images[] =
	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	// ///// topup vendors
	String topups_vendor_names[] =
	{ "Select Operator", "Aircel", "Airtel", "BSNL 3G", "BSNL Recharge/Validity (RCV)", "BSNL Special (STV)", "BSNL TopUp", "Idea",
			"Loop Mobile", "MTNL Recharge/Special", "MTNL TopUp", "MTS/MTS Blaze/Browse", "Reliance CDMA/NetConnect 1X/+",
			"Reliance GSM/NetConnect 3G", "S Tel", "Tata Docomo", "Tata Docomo Special", "Tata Indicom/Photon +/Whiz", "Uninor",
			"Uninor Special", "Videocon", "Videocon Special", "Virgin CDMA", "Virgin GSM", "Vodafone"

	};
	int topups_vendor_images[] =
	{ 0, R.drawable.mobile_aircel, R.drawable.mobile_airtel, R.drawable.mobile_bsnl3g, R.drawable.mobile_bsnl, R.drawable.mobile_bsnl,
			R.drawable.mobile_bsnl, R.drawable.mobile_idea, R.drawable.mobile_loopmobile, R.drawable.mobile_mtnl, R.drawable.mobile_mtnl,
			R.drawable.mobile_mts, R.drawable.mobile_reliancecdma, R.drawable.datacard_rconnect3g, 0, R.drawable.mobile_docomo,
			R.drawable.mobile_docomo, R.drawable.datacard_photonplus, R.drawable.mobile_uninor, R.drawable.mobile_uninor,
			R.drawable.mobile_videocon, R.drawable.mobile_videocon, R.drawable.mobile_virgin, R.drawable.mobile_virgin,
			R.drawable.mobile_vodafone };
	static int topups_vendor_ids[] =
	{ 0, 6, 1, 302, 301, 303, 3, 8, 10, 2501, 25, 13, 4, 5, 15, 11, 1101, 9, 16, 1601, 17, 1701, 12, 14, 2

	};

	String dth_vendor_names[] =
	{ "Select DTH Provider", "Airtel DTH", "Dish TV DTH", "Big TV DTH", "Sun DTH", "Tata Sky DTH", "Videocon DTH" };
	int dth_vendor_images[] =
	{ 0, R.drawable.dth_airtel, R.drawable.dth_dishtv, R.drawable.dth_reliancebigtv, R.drawable.dth_sundirect, R.drawable.dth_tatasky,
			R.drawable.dth_videocon };
	static int dth_vendor_ids[] =
	{ 0, 23, 18, 20, 22, 19, 21 };
	// /////////////
	// String payment_modes_names[] =
	// { "Select Payment Mode", "My wallet", "Credit Cards", "Debit Cards",
	// "Net Banking" };
	// int payment_modes_images[] =
	// { 0, R.drawable.mywallet, R.drawable.creditcards,
	// R.drawable.creditcards2, R.drawable.netbanking };

	String payment_modes_names[] =
	{ "Select Payment Mode", "Credit Cards", "Debit Cards", "Net Banking" };
	int payment_modes_images[] =
	{ 0, R.drawable.creditcards, R.drawable.creditcards2, R.drawable.netbanking };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.e("INSIDE", "inside1");
		recharge_view = inflater.inflate(R.layout.fragment_recharge, container, false);
		try
		{
			SharedPreferences default_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

			SharedPreferences prefs = getActivity().getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			cust_loginid = prefs.getString("loginid", "");
			cust_username = prefs.getString("username", "");
			cust_email = prefs.getString("email", "");
			cust_phone = prefs.getString("phone", "");
			cust_main_balance = prefs.getString("balance", "");
			loyalty_points = prefs.getString("loyalty_points", "");

			pref_paymentmode = default_prefs.getString("pref_paymentmode", "");

			check = new connection_check(getActivity());

			// ///INITIALIZE VALUES FROM XML ///
			circles_names = getResources().getStringArray(R.array.circles_names);

			// SPINNERS////
			payment_categories_spinner = (Spinner) recharge_view.findViewById(R.id.payment_categories_spinner);
			payment_vendors_spinner = (Spinner) recharge_view.findViewById(R.id.payment_vendors_spinner);
			payment_modes_spinner = (Spinner) recharge_view.findViewById(R.id.payment_modes_spinner);
			circle_spinner = (Spinner) recharge_view.findViewById(R.id.circle_spinner);
			circle_spinner.setPrompt("Select Circle");

			// / TEXTVIEWS////

			payment_billno_text = (TextView) recharge_view.findViewById(R.id.payment_billno_text);
			rechargehint_text = (TextView) recharge_view.findViewById(R.id.rechargehint_text);
			rechargehint_text.setVisibility(View.GONE);
			divider1 = (TextView) recharge_view.findViewById(R.id.divider1);
			divider1.setVisibility(View.GONE);
			vendor_warning_text = (TextView) recharge_view.findViewById(R.id.vendor_warning_text);
			vendor_warning_text.setVisibility(View.GONE);
			payment_remarks_text = (TextView) recharge_view.findViewById(R.id.payment_remarks_text);
			payment_amount_text = (TextView) recharge_view.findViewById(R.id.payment_amount_text);
			discount_amount_text = (TextView) recharge_view.findViewById(R.id.discount_amount_text);

			// /CHECKBOX////

			redeempoints_checkbox = (CheckBox) recharge_view.findViewById(R.id.redeempoints_checkbox);

			// /BUTTONS////

			speech_imagebutton = (ImageButton) recharge_view.findViewById(R.id.speech_imagebutton);
			speech_imagebutton.setOnClickListener(this);
			number_option_imagebutton = (ImageButton) recharge_view.findViewById(R.id.number_option_imagebutton);
			number_option_imagebutton.setOnClickListener(this);
			autoselect_button = (Button) recharge_view.findViewById(R.id.autoselect_button);
			autoselect_button.setVisibility(View.GONE);
			autoselect_button.setOnClickListener(this);
			plan_option_button = (Button) recharge_view.findViewById(R.id.plan_option_button);
			plan_option_button.setOnClickListener(this);
			proceed_payment_button = (Button) recharge_view.findViewById(R.id.proceed_payment_button);

			proceed_payment_button.setEnabled(false);
			proceed_payment_button.setOnClickListener(this);

			// initiate values

			payment_categories_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
					recharge_category_names, recharge_category_images));

			Intent i = getActivity().getIntent();
			if (i.hasExtra("from_favourites"))
			{
				Bundle bundle = i.getExtras();
				if (bundle.containsKey("from_favourites"))
				{
					from_fav = bundle.getString("from_favourites");

					if (from_fav.equals("true"))
					{
						payment_billno_text.setText(bundle.getString("selected_number"));
						call_db(bundle.getString("selected_number"));
						payment_amount_text.setText(bundle.getString("selected_amount"));
						payment_modes_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
								payment_modes_names, payment_modes_images));
						if (pref_paymentmode.equals("credit_cards"))
							payment_modes_spinner.setSelection(0);
						else if (pref_paymentmode.equals("debit_cards"))
							payment_modes_spinner.setSelection(0);
						else if (pref_paymentmode.equals("net_banking"))
							payment_modes_spinner.setSelection(0);
					}
				}
				else
				{
					Log.e("from fav ", "empty");
				}
			}
			URL = selectwebservice.currentwebservice();
			payment_categories_spinner.setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{

					autofill = false;
					payment_billno_text.setText("");
					return false;
				}
			});

			payment_amount_text.addTextChangedListener(new TextWatcher()
			{
				public void afterTextChanged(Editable s)
				{
					if (redeempoints_checkbox.isChecked())
					{
						discount_amount_text.setText("Total :" + "\u20B9 " + payment_amount_text.getText().toString());
					}
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after)
				{
				}

				public void onTextChanged(CharSequence s, int start, int before, int count)
				{
				}
			});

			redeempoints_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					if (isChecked)
					{
						String discounted_total = String.valueOf(Float.parseFloat(payment_amount_text.getText().toString())
								- (Float.parseFloat(loyalty_points) / 100));
						Log.e("total loyalty rs",String.valueOf(Float.parseFloat(loyalty_points)/100));
						discount_amount_text.setText("Total :" + "\u20B9 " + discounted_total);
					}
					else
					{
						discount_amount_text.setText("Total :" + "\u20B9 " + payment_amount_text.getText().toString());
					}

				}
			});

			payment_categories_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
				{
					payment_billno_text.setInputType(InputType.TYPE_CLASS_NUMBER);

					if (!autofill)
					{
						vendor_warning_text.setVisibility(View.GONE);
						circle_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages, circles_names,
								circles_images));
						payment_modes_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
								payment_modes_names, payment_modes_images));
						if (pref_paymentmode.equals("credit_cards"))
							payment_modes_spinner.setSelection(1);
						else if (pref_paymentmode.equals("debit_cards"))
							payment_modes_spinner.setSelection(2);
						else if (pref_paymentmode.equals("net_banking"))
							payment_modes_spinner.setSelection(3);

						if (payment_categories_spinner.getSelectedItem().equals("Select Category"))
						{
							payment_billno_text.setHint("Select Category");
							// payment_vendors_spinner.setVisibility(View.GONE);

						}

						else if (payment_categories_spinner.getSelectedItem().equals("Mobile/Datacard Topup"))
						{
							payment_billno_text.setHint("Mobile/Datacard Number");
							rechargehint_text.setVisibility(View.VISIBLE);
							divider1.setVisibility(View.VISIBLE);
							autoselect_button.setVisibility(View.VISIBLE);
							circle_spinner.setVisibility(View.VISIBLE);
							plan_option_button.setVisibility(View.VISIBLE);
							payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
									topups_vendor_names, topups_vendor_images));

						}
						else if (payment_categories_spinner.getSelectedItem().equals("Recharge DTH"))
						{
							// payment_billno_text.setText("");
							autoselect_button.setVisibility(View.GONE);
							rechargehint_text.setVisibility(View.GONE);
							divider1.setVisibility(View.GONE);
							circle_spinner.setVisibility(View.GONE);
							plan_option_button.setVisibility(View.GONE);
							circle_spinner.setSelection(1);
							payment_billno_text.setHint("DTH Number");

							payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
									dth_vendor_names, dth_vendor_images));
						}
						else
						{
							rechargehint_text.setVisibility(View.GONE);
							autoselect_button.setVisibility(View.GONE);
							divider1.setVisibility(View.GONE);
							// circle_spinner.setAdapter();
							// payment_vendors_spinner.setAdapter(null);
							// payment_modes_spinner.setAdapter(null);
						}
					}
					else
					{
						autofill = false;
					}
				}

				public void onNothingSelected(AdapterView<?> arg0)
				{
					Toast.makeText(getActivity(), "nothing selected", Toast.LENGTH_SHORT).show();

				}
			});

			payment_modes_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
				{
					set_layout();
				}

				public void onNothingSelected(AdapterView<?> arg0)
				{
					Toast.makeText(getActivity(), "nothing selected", Toast.LENGTH_SHORT).show();

				}
			});
		}
		catch (Exception e)
		{
			Log.e("crash", "fatal error in fragment_recharge on create\n" + e.toString());
		}
		return recharge_view;
	}

	@Override
	public void onClick(View v)
	{
		try
		{
			switch (v.getId())
			{
			case R.id.speech_imagebutton:
			{
				PackageManager pm = getActivity().getPackageManager();
				List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
				if (activities.size() != 0)
				{
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your option");
					startActivityForResult(intent, 123);
					break;
				}
				else
				{
					Toast.makeText(getActivity(), "Recognizer not present", Toast.LENGTH_SHORT);
				}

			}
			case R.id.number_option_imagebutton:

			{
				PopupMenu popup = new PopupMenu(getActivity(), v);
				popup.getMenu().add(0, 0, 0, "Use my mobile");
				popup.getMenu().add(0, 1, 1, "Select from contacts");
				popup.getMenu().add(0, 2, 2, "My DTH");
				popup.getMenu().add(0, 3, 3, "My DataCard");
				popup.show();

				final SharedPreferences prefs = getActivity().getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
				final SharedPreferences default_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

				popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
				{

					@Override
					public boolean onMenuItemClick(MenuItem item)
					{
						if (item.getItemId() == 0)
						{
							String mynum = default_prefs.getString("pref_mymobile", "");
							if (mynum.equals(""))
							{
								payment_billno_text.setText("");
								Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
							}
							else
							{
								payment_billno_text.setText(mynum);
								call_db(payment_billno_text.getText().toString());
							}
						}
						else if (item.getItemId() == 1)
						{
							Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
							intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
							startActivityForResult(intent, 11);
						}
						else if (item.getItemId() == 2)
						{
							String mynum = default_prefs.getString("pref_mydth", "");
							if (mynum.equals(""))
							{
								payment_billno_text.setText("");
								Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
							}
							else
							{

								payment_billno_text.setText(mynum);
								payment_categories_spinner.setSelection(2, true);
							}
						}
						else if (item.getItemId() == 3)
						{
							String mynum = default_prefs.getString("pref_mydatacard", "");
							if (mynum.equals(""))
							{
								payment_billno_text.setText("");
								Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
							}
							else
							{
								payment_billno_text.setText(mynum);
								call_db(payment_billno_text.getText().toString());
							}
						}
						return true;
					}
				});
				break;
			}
			case R.id.autoselect_button:
			{
				if (payment_billno_text.getText().toString().trim().equals("") || payment_billno_text.getText().length() < 9)
				{
					Toast.makeText(getActivity(), "Select/Input a valid number", Toast.LENGTH_SHORT).show();
				}
				else
				{
					call_db(payment_billno_text.getText().toString());
				}
				break;
			}
			case R.id.plan_option_button:
			{
				try
				{
					methodname = "fetchmobile_plans";
					new recharge_asynctask(getActivity()).execute(methodname);
				}
				catch (Exception e)
				{
					Log.e("crash", "error on policy button view");
				}

				break;
			}
			case R.id.proceed_payment_button:
			{

				if (payment_categories_spinner.getSelectedItem().toString().equals("Mobile/Datacard Topup"))
				{
					regex = "^[7-9][0-9]{9}$";
					matcher = Pattern.compile(regex).matcher(payment_billno_text.getText().toString().trim());
					if (matcher.find())
					{
						if ((payment_billno_text.getText().toString().trim().length() == 10))
						{
							e1 = "";
						}
						else
						{
							payment_billno_text.setError("Phone number should be 10 digits long");
							e1 = "Phone number should be 10 digits long";
						}
					}
					else
					{
						e1 = "Phone number not valid";
						payment_billno_text.setError(e1);
					}
				}
				else if (payment_categories_spinner.getSelectedItem().toString().equals("Recharge DTH"))
				{
					if (payment_vendors_spinner.getSelectedItem().toString().equals("Airtel DTH"))
					{
						if (payment_billno_text.getText().toString().trim().length() == 10)
							e1 = "";
						else
						{
							e1 = "Enter your 10 digit Airtel ID properly";
							payment_billno_text.setError(e1);
						}
					}
					else if (payment_vendors_spinner.getSelectedItem().toString().equals("Dish TV DTH"))
					{
						if (payment_billno_text.getText().toString().trim().length() == 11)
							e1 = "";
						else
						{
							e1 = "Enter your 11 digit DishTV ID properly";
							payment_billno_text.setError(e1);
						}
					}
					else if (payment_vendors_spinner.getSelectedItem().toString().equals("Big TV DTH"))
					{
						if (payment_billno_text.getText().toString().trim().length() == 12)
							e1 = "";
						else
						{
							e1 = "Enter your 12 digit BigTV ID properly";
							payment_billno_text.setError(e1);
						}
					}
					else if (payment_vendors_spinner.getSelectedItem().toString().equals("Sun DTH"))
					{
						if (payment_billno_text.getText().toString().trim().length() == 11)
							e1 = "";
						else
						{
							e1 = "Enter your 11 digit SunDirect ID properly";
							payment_billno_text.setError(e1);
						}
					}
					else if (payment_vendors_spinner.getSelectedItem().toString().equals("Tata Sky DTH"))
					{
						if (payment_billno_text.getText().toString().trim().length() == 10)
							e1 = "";
						else
						{
							e1 = "Enter your 10 digit TataSky ID properly";
							payment_billno_text.setError(e1);
						}
					}
					else if (payment_vendors_spinner.getSelectedItem().toString().equals("Videocon DTH"))
					{
						e1 = "";
					}
				}

				regex = "^[0-9]{0,5}$";
				matcher = Pattern.compile(regex).matcher(payment_amount_text.getText().toString().trim());
				if (matcher.find())
				{
					e3 = "";
				}
				else
				{
					e3 = "Invalid rupee value";
					payment_amount_text.setError(e3);
				}

				if ((payment_categories_spinner.getSelectedItemPosition() == 0) || (circle_spinner.getSelectedItemPosition() == 0)
						|| (payment_vendors_spinner.getSelectedItemPosition() == 0)
						|| (payment_modes_spinner.getSelectedItemPosition() == 0)
						|| payment_billno_text.getText().toString().trim().equals("")
						|| payment_amount_text.getText().toString().trim().equals(""))
				{
					Toast.makeText(getActivity(), "Please fill in the required data", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if (e1.equals("") && e2.equals("") && e3.equals(""))
					{
						if (Integer.parseInt(payment_amount_text.getText().toString().trim()) >= 10)
						{

							if (payment_modes_spinner.getSelectedItem().toString().equals("Credit Cards")
									|| payment_modes_spinner.getSelectedItem().toString().equals("Debit Cards")
									|| payment_modes_spinner.getSelectedItem().toString().equals("Net Banking"))
							{
								// call_otherpayments();
								Payments recharge = new Payments(getActivity());
								recharge.show_payments_dialog(main_flag, payment_categories_spinner.getSelectedItem().toString().trim(),
										payment_modes_spinner.getSelectedItem().toString(), payment_amount_text.getText().toString().trim());
							}
							else
							{
								if (check.isnetwork_available())
								{
									ptype = "Wallet";
									Payments.p_state = "SUCCESS";
									methodname = "transaction_main";
									new recharge_asynctask(getActivity()).execute(methodname);
								}
								else
								{
									Toast.makeText(getActivity(), "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
								}

							}
						}
						else
						{
							payment_amount_text.setError("Minimum amount is 10 rupees");
						}

					}
					else
					{
						// Toast.makeText(getActivity(),
						// "Please resolve the following errors\n" + e1 + "\n" +
						// e2 + "\n" + e3,
						// Toast.LENGTH_LONG).show();
					}
				}

				break;
			}
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on on click event\n" + e.toString());
		}
	}

	// // speech result activity && contaacts activity
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data)
	{
		super.onActivityResult(reqCode, resultCode, data);
		final SharedPreferences default_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		try
		{

			switch (reqCode)
			{
			case (11):
			{
				if (resultCode == Activity.RESULT_OK)
				{
					Uri contactData = data.getData();
					Cursor c = getActivity().managedQuery(contactData, null, null, null, null);
					if (c.moveToFirst())
					{
						c = getActivity().getContentResolver().query(contactData, new String[]
						{ ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE }, null, null, null);
						if (c != null && c.moveToFirst())
						{
							String number = c.getString(0);
							number = number.replace(" ", "");
							number = number.replace("+91", "");
							int type = c.getInt(1);
							payment_billno_text.setText(String.valueOf(number));
							call_db(payment_billno_text.getText().toString());
						}
					}
				}
				break;
			}
			case 123:
			{
				if (reqCode == 123 && resultCode == Activity.RESULT_OK)
				{
					int flag = 10;
					ArrayList<String> voice_match = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

					// Toast.makeText(getActivity(), "you said " + voice_match,
					// Toast.LENGTH_LONG).show();
					if ((voice_match.get(0).startsWith("recharge my mobile with"))
							|| (voice_match.get(1).startsWith("recharge my mobile with")))
					{
						String match1 = voice_match.get(0).replace("recharge my mobile with", "").trim();
						String match2 = voice_match.get(1).replace("recharge my mobile with", "").trim();
						Log.e("final string", match1);
						try
						{
							int x = Integer.parseInt(match1);
							payment_amount_text.setText(String.valueOf(x));
							flag = 1;
						}
						catch (Exception e)
						{
							Log.e("error", "number not found in string");
						}
						if (flag != 1)
						{
							try
							{
								int x = Integer.parseInt(match2);
								payment_amount_text.setText(String.valueOf(x));
								flag = 1;
							}
							catch (Exception e)
							{
								Log.e("error", "number not found in string");
							}
						}
						String mynum = default_prefs.getString("pref_mymobile", "");
						if (mynum.equals(""))
						{
							Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
						}
						else
						{
							payment_billno_text.setText(mynum);
							call_db(mynum);
						}
					}
					else if ((voice_match.get(0).startsWith("recharge my data card with"))
							|| (voice_match.get(1).startsWith("recharge my data card with")))
					{
						String match1 = voice_match.get(0).replace("recharge my data card with", "").trim();
						String match2 = voice_match.get(1).replace("recharge my data card with", "").trim();
						Log.e("final string", match1);
						try
						{
							int x = Integer.parseInt(match1);
							payment_amount_text.setText(String.valueOf(x));
							flag = 1;
						}
						catch (Exception e)
						{
							Log.e("error", "number not found in string");
						}
						if (flag != 1)
						{
							try
							{
								int x = Integer.parseInt(match2);
								payment_amount_text.setText(String.valueOf(x));
								flag = 1;
							}
							catch (Exception e)
							{
								Log.e("error", "number not found in string");
							}
						}
						String mynum = default_prefs.getString("pref_mydatacard", "");
						if (mynum.equals(""))
						{
							Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
						}
						else
						{
							payment_billno_text.setText(mynum);
							call_db(mynum);
						}
					}
				}
				break;
			}
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on result activity\n" + e.toString());
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
			JSONArray txns = his_data.getJSONArray("transaction_report");
			Log.e("report length   ", String.valueOf(txns.length()));
			JSONObject e = txns.getJSONObject(0);
			int flag = Integer.parseInt(e.getString("flag"));
			Log.e("flag   ", String.valueOf(flag));

			if (flag == 0)
			{
				String txn_status = e.getString("txn_status");
				String txn_status_reason = e.getString("txn_status_reason");
				txn_id = e.getString("txn_id");
				if (txn_status.equals("SUCCESS"))
				{
					paymentstatus_text.setTextColor(Color.GREEN);
					paymentstatus_text.setText(txn_status);
				}
				else
				{
					TextView hdfc_warning_text = (TextView)dialog.findViewById(R.id.hdfc_warning_text);
					hdfc_warning_text.setVisibility(View.VISIBLE);
					paymentstatus_text.setTextColor(Color.RED);
					paymentstatus_text.setText(txn_status + "," + txn_status_reason);
					retry_button.setEnabled(true);
				}

				paymentsummary1_text.setText("Your Transaction ID :" + txn_id);
				paymentsummary2_text.setText("Bill/Phone Number :" + payment_billno_text.getText().toString().trim());
				paymentsummary3_text.setText("Amount:" + payment_amount_text.getText().toString().trim());

			}
			else if (flag == 10)
			{
				paymentstatus_text.setText("Insufficent balance");
			}
			else if (flag == 20)
			{
				paymentstatus_text.setText("Zero balance");
			}
			else if (flag == 3)
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
				paymentstatus_text.setText("Insufficent balance");
				paymentstatus_text.setTextColor(Color.RED);
				paymentstatus_text.setText("Login failed! Please check your credentials or contact admin for account activation");
			}

			finish_button.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (savetofavorite_checkbox.isChecked())
					{
						if (favorite_alias_text.getText().toString().equals(""))
						{
							Toast.makeText(mcontext, "Please enter a name for this transaction", Toast.LENGTH_SHORT).show();
						}
						else
						{
							favourite_name = favorite_alias_text.getText().toString().trim();
							methodname = "update_favorites";
							new recharge_asynctask(mcontext).execute(methodname);

							// getActivity().finish();
							// dialog.dismiss();
						}
					}
					else
					{
						((Activity) mcontext).finish();
						dialog.dismiss();
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
						Payments recharge = new Payments(mcontext);
						recharge.show_payments_dialog(main_flag, payment_categories_spinner.getSelectedItem().toString().trim(),
								payment_modes_spinner.getSelectedItem().toString(), payment_amount_text.getText().toString().trim());
					}
					else
					{
						if (retry_count < 1)
						{
							methodname = "transaction_main";
							new recharge_asynctask(mcontext).execute(methodname);
							retry_count = retry_count + 1;
						}
						else
						{
							methodname = "call_refund";
							new recharge_asynctask(mcontext).execute(methodname);
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
					}
					else
					{
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

	public void set_layout()
	{

		try
		{
			payment_amount_text.setEnabled(true);
			plan_option_button.setEnabled(true);

			payment_remarks_text.setEnabled(true);
			proceed_payment_button.setEnabled(true);
			// proceed_payment_button.setClickable(true);
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on set_layout\n" + e.toString());
		}

	}

	public void call_db(String number)
	{
		DataBaseHelper myDbHelper = new DataBaseHelper(null);
		myDbHelper = new DataBaseHelper(getActivity());

		try
		{
			myDbHelper.createDataBase();
		}
		catch (IOException ioe)
		{
			throw new Error("Unable to create database");
		}

		try
		{
			myDbHelper.openDataBase();

		}
		catch (SQLException sqle)
		{

			Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
			throw sqle;

		}

		SQLiteDatabase sampleDB = null;

		try
		{
			int x = Integer.parseInt(number.substring(0, 4));
			Log.e("number to be checked on databse", String.valueOf(x));
			sampleDB = getActivity().openOrCreateDatabase("mobilecodes_db", 0, null);
			Cursor c = sampleDB.rawQuery("SELECT * FROM mobile_codes where _id=" + x, null);
			String Operator = "";
			String Circle = "";
			Log.e("cursor output", c.toString());
			if (c.moveToFirst())
			{
				Log.e("inside cursor output", c.getString(0));
				Operator = c.getString(c.getColumnIndex("Operator"));
				Circle = c.getString(c.getColumnIndex("Circle"));
				Log.e("operator code ", Operator);
				payment_categories_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
						recharge_category_names, recharge_category_images));
				payment_categories_spinner.setSelection(1, true);

				payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
						topups_vendor_names, topups_vendor_images));

				if (Operator.equals("Reliance CDMA"))

				{
					payment_vendors_spinner.setSelection(12, true);
				}
				else if (Operator.equals("Reliance GSM"))

				{
					payment_vendors_spinner.setSelection(13, true);
				}
				else if (Operator.equals("TATA Docomo"))

				{
					vendor_warning_text.setVisibility(View.VISIBLE);
					vendor_warning_text
							.setText("Docomo Special recharge selected, Please choose Tata Docomo if you are looking for regular plans");
					payment_vendors_spinner.setSelection(16, true);
				}

				else
				{
					payment_vendors_spinner.setSelection(Arrays.asList(topups_vendor_names).indexOf(Operator), true);

				}
				circle_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages, circles_names,
						circles_images));

				circle_spinner.setSelection(Arrays.asList(circles_names).indexOf(Circle), true);

				sampleDB.close();
				autofill = true;

			}
			else
			{
				Toast.makeText(getActivity(), "Number not found in the database!", Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception se)
		{
			Log.e(getClass().getSimpleName(), "Could not create or Open the database\n" + se.toString());
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK) && sample_webview.canGoBack())
		{
			sample_webview.goBack();
			return true;
		}
		return super.getActivity().onKeyDown(keyCode, event);
	}

	// ////////////// ASYNC TASK FOR SENDING DATA TO WEBSERVICE /////////

	static class recharge_asynctask extends AsyncTask<String, Integer, String>
	{
		ProgressDialog progress;

		public recharge_asynctask(Context context)
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

			if (params[0].toString().equals("transaction_main"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/transaction_method";
					String METHOD_NAME = "transaction_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo loginidprop = new PropertyInfo();
					loginidprop.setName("loginid");
					loginidprop.setValue(cust_loginid);
					loginidprop.setType(String.class);
					request.addProperty(loginidprop);

					PropertyInfo categoryprop = new PropertyInfo();
					categoryprop.setName("category");
					categoryprop.setValue(main_flag);
					categoryprop.setType(String.class);
					request.addProperty(categoryprop);
					// /////////////////////////

					PropertyInfo subcategoryprop = new PropertyInfo();
					subcategoryprop.setName("subcategory");
					String cate = "";
					if (payment_categories_spinner.getSelectedItemPosition() == 1)
					{
						cate = "Mobile/Datacard";
					}
					else
					{
						cate = "DTH";
					}
					subcategoryprop.setValue(cate);
					subcategoryprop.setType(String.class);
					request.addProperty(subcategoryprop);

					PropertyInfo circleprop = new PropertyInfo();
					circleprop.setName("circle");
					circleprop.setValue(circle_spinner.getSelectedItem().toString());
					circleprop.setType(String.class);
					request.addProperty(circleprop);

					PropertyInfo circle_idprop = new PropertyInfo();
					circle_idprop.setName("circle_id");
					circle_idprop.setValue(circle_spinner.getSelectedItemPosition());
					circle_idprop.setType(Integer.class);
					request.addProperty(circle_idprop);

					PropertyInfo operatorprop = new PropertyInfo();
					operatorprop.setName("operator");
					operatorprop.setValue(payment_vendors_spinner.getSelectedItem().toString());
					operatorprop.setType(String.class);
					request.addProperty(operatorprop);

					PropertyInfo operator_idprop = new PropertyInfo();
					operator_idprop.setName("operator_id");
					if (payment_categories_spinner.getSelectedItem().toString().equals("Mobile/Datacard Topup"))
					{
						operator_idprop.setValue(topups_vendor_ids[payment_vendors_spinner.getSelectedItemPosition()]);
					}
					else
					{
						operator_idprop.setValue(dth_vendor_ids[payment_vendors_spinner.getSelectedItemPosition()]);
					}
					operator_idprop.setType(Integer.class);
					request.addProperty(operator_idprop);

					PropertyInfo accountnoprop = new PropertyInfo();
					accountnoprop.setName("billno");
					accountnoprop.setValue(payment_billno_text.getText().toString().trim());
					accountnoprop.setType(String.class);
					request.addProperty(accountnoprop);

					PropertyInfo remarksprop = new PropertyInfo();
					remarksprop.setName("remarks");
					remarksprop.setValue(payment_remarks_text.getText().toString().trim());
					remarksprop.setType(String.class);
					request.addProperty(remarksprop);

					PropertyInfo charges_prop = new PropertyInfo();
					charges_prop.setName("additional_charges");
					charges_prop.setValue("0");
					charges_prop.setType(String.class);
					request.addProperty(charges_prop);

					PropertyInfo amountprop = new PropertyInfo();
					amountprop.setName("amount");
					amountprop.setValue(payment_amount_text.getText().toString().trim());
					amountprop.setType(String.class);
					request.addProperty(amountprop);

					PropertyInfo payment_type = new PropertyInfo();
					payment_type.setName("payment_type");
					payment_type.setValue(ptype);
					payment_type.setType(String.class);
					request.addProperty(payment_type);

					PropertyInfo payment_state = new PropertyInfo();
					payment_state.setName("payment_state");
					payment_state.setValue(Payments.p_state);
					payment_state.setType(String.class);
					request.addProperty(payment_state);

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

					PropertyInfo parameter1_prop = new PropertyInfo();
					parameter1_prop.setName("parameter1");
					parameter1_prop.setValue("null");
					parameter1_prop.setType(String.class);
					request.addProperty(parameter1_prop);

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
						PackageInfo pi = mcontext.getPackageManager().getPackageInfo(mcontext.getPackageName(), PackageManager.GET_ACTIVITIES);
						device_version = pi.versionName;
					}
					catch (PackageManager.NameNotFoundException e)
					{
						Log.e("error","cannot get version name");
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
					resultstatus = "transactioncall_executed";

				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "transactioncall_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "transactioncall_executed";
				}
			}
			else if (params[0].toString().equals("update_favorites"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/updatefavourites_method";
					String METHOD_NAME = "updatefavourites_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo txnidprop = new PropertyInfo();
					txnidprop.setName("txn_id");
					txnidprop.setValue(txn_id);
					txnidprop.setType(String.class);
					request.addProperty(txnidprop);

					PropertyInfo nameprop = new PropertyInfo();
					nameprop.setName("favourite_name");
					nameprop.setValue(favourite_name);
					nameprop.setType(String.class);
					request.addProperty(nameprop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					// outp = str;
					resultstatus = "update_favorites_executed";

				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "update_favorites_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "update_favorites_executed";
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
					String amt = payment_amount_text.getText().toString().trim();
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
			else if (params[0].toString().equals("fetchmobile_plans"))
			{

				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/fetchmobileplans_method";
					String METHOD_NAME = "fetchmobileplans_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo payuid_prop = new PropertyInfo();
					payuid_prop.setName("Operator");
					payuid_prop.setValue(payment_vendors_spinner.getSelectedItem().toString());
					payuid_prop.setType(String.class);
					request.addProperty(payuid_prop);

					PropertyInfo payu_requestid_prop = new PropertyInfo();
					payu_requestid_prop.setName("Circle");
					payu_requestid_prop.setValue(circle_spinner.getSelectedItem().toString());
					payu_requestid_prop.setType(String.class);
					request.addProperty(payu_requestid_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;
					resultstatus = "fetchmobile_plans_executed";
				}
				catch (ClientProtocolException e)
				{
					Log.e("Clientpexcept", e.toString());
				}
				catch (Exception e)
				{
					Log.e("IOexcep", e.toString());
				}

				resultstatus = "fetchmobile_plans_executed";
			}

			//

			return resultstatus;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				super.onPostExecute(result);
				progress.dismiss();
				Log.e("reached", "post execute");
				if (result.equals("transactioncall_executed"))
				{
					payment_status(outp);
				}
				else if (resultstatus.equals("call_refund_executed"))
				{
					Toast.makeText(mcontext, "Transaction amount will be refunded to your account shortly", Toast.LENGTH_SHORT).show();
				}
				else if (resultstatus.equals("update_favorites_executed"))
				{
					Toast.makeText(mcontext, "Favourites added successfully", Toast.LENGTH_SHORT).show();
					((Activity) mcontext).finish();
				}
				else if (resultstatus.equals("fetchmobile_plans_executed"))
				{

					JSONObject his_data = new JSONObject(outp);
					JSONArray txns = his_data.getJSONArray("plan_history");
					mylist.clear();
					if (txns.length() > 0)
					{
						for (int i = 0; i < txns.length(); i++)
						{

							map_mobileplans = new HashMap<String, String>();
							JSONObject e = txns.getJSONObject(i);
							map_mobileplans.put("plantype", "Plan Type : " + e.getString("plantype"));
							map_mobileplans.put("rate", e.getString("rate"));
							map_mobileplans.put("description", e.getString("description"));
							map_mobileplans.put("validity", e.getString("validity"));
							mylist.add(map_mobileplans);

						}

						plans_adapter = new SimpleAdapter(mcontext, mylist, R.layout.listview_mobileplans, new String[]
						{ "plantype", "rate", "description", "validity" }, new int[]
						{ R.id.plantype_label, R.id.amount_label, R.id.description_label, R.id.validity_label });

						inflater = LayoutInflater.from(mcontext);
						View pl = inflater.inflate(R.layout.mobileplans_layout, null);
						final AlertDialog.Builder b = new AlertDialog.Builder(mcontext);
						b.setView(pl);
						final Dialog plan_dialog = b.create();
						plans_listview = (ListView) pl.findViewById(R.id.mobileplans_listview);
						plans_listview.setAdapter(plans_adapter);
						plans_listview.setOnItemClickListener(new OnItemClickListener()
						{
							public void onItemClick(AdapterView<?> parent, View view, int position, long id)
							{
								Log.e("selected favourite", mylist.get(position).toString());
								payment_amount_text.setText(mylist.get(position).get("rate").toString()
										.replace("Amount : " + "\u20B9 ", ""));
								plan_dialog.dismiss();
							}
						});
						plan_dialog.show();
					}
					else
					{
						Toast.makeText(mcontext, "Plans will be added soon for this operator,check back later", Toast.LENGTH_SHORT).show();
					}

				}

			}
			catch (Exception e)
			{
				Log.e("error", "error on transaction post execute" + e);
				Toast.makeText(mcontext, "Plans will be added soon for this operator,check back later", Toast.LENGTH_SHORT).show();
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
