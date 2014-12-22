package com.ezeepay.services;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_paybills extends Fragment implements OnClickListener
{
	View paybills_view;
	private static Context mcontext;

	private static String payuid = "null", payu_status = "none", req_id = "", ptype = "Wallet", bankcd = "none", fill_method = "",
			prefvalue_1 = "", prefvalue_2 = "", URL, methodname = "", resultstatus = "", errmsg = "", x = "", selected_circle = "",
			flagtype = "", subcat_flag = "", main_flag = "Bill Payments", vendor_flag = "", pref_paymentmode = "", cust_loginid,
			cust_main_balance, cust_username = "", cust_email = "", cust_phone = "", favourite_name, txn_id, outp, device_version = "";
	private static int pay_flag = 0, retry_count = 0;
	boolean autofill = false;
	static List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);

	WebView web;
	static DecimalFormat formatter = new DecimalFormat("###.##");
	String dummy_cat[] =
	{ "Select Main Category" };
	int dummy_images[] =
	{ 0 };
	String bills_cat[] =
	{ "Select Category", "Electricity Bills", "Insurance Premiums", "Mobile Postpaid", "Landline Bills", "Gas Bills" };
	int bills_images[] =
	{ 0, R.drawable.payment_electricity, R.drawable.payment_insurance, R.drawable.payment_postpaid, R.drawable.payment_landline,
			R.drawable.payment_gas };
	connection_check check;
	customspinner_adapter ca = null;
	String circles_names[];
	int circles_images[] =
	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	// /////////////
	String postpaid_vendor_names[] =
	{ "Select Postpaid Operator", "Airtel", "Vodafone", "BSNL Mobile", "Reliance CDMA", "Reliance GSM", "Idea", "Tata Indicom",
			"LOOP Mobile", "Tata Docomo GSM" };
	int postpaid_vendor_images[] =
	{ 0, R.drawable.mobile_airtel, R.drawable.mobile_vodafone, R.drawable.mobile_bsnl, R.drawable.mobile_reliancecdma,
			R.drawable.mobile_reliancecdma, R.drawable.mobile_idea, R.drawable.mobile_docomo, R.drawable.mobile_loopmobile,
			R.drawable.mobile_docomo };
	int postpaid_vendor_ids[] =
	{ 0, 31, 33, 36, 35, 34, 32, 39, 40, 38, };
	// ////////////
	String landline_vendor_names[] =
	{ "Select Landline Operator", "Airtel Landline", "BSNL Landline", "MTNL Landline" };
	int landline_vendor_images[] =
	{ 0, R.drawable.mobile_airtel, R.drawable.mobile_bsnl, R.drawable.mobile_mtnl };
	int landline_vendor_ids[] =
	{ 0, 42, 37, 41 };
	// ////////////////////
	String electricity_vendor_names[] =
	{ "Select Electricity Provider", "BSES Rajdhani Power Limited - Delhi", "BSES Yamuna Power Limited - Delhi",
			"Tata Power Delhi Limited - Delhi", "Reliance Energy Limited" };
	int electricity_vendor_images[] =
	{ 0, R.drawable.power_bses, R.drawable.power_bses, R.drawable.power_tata, R.drawable.power_reliance };
	int electricity_vendor_ids[] =
	{ 0, 43, 44, 45, 46 };
	// /////////////
	String gas_vendor_names[] =
	{ "Select Gas Vendor", "Mahanagar Gas" };
	int gas_vendor_images[] =
	{ 0, R.drawable.gas_mahanagar };
	int gas_vendor_ids[] =
	{ 0, 47 };
	// //////////////
	String insurance_vendor_names[] =
	{ "Select Insurance Provider", "ICICI Prudential Life Insurance", "Tata AIA Life Insurance" };
	int insurance_vendor_images[] =
	{ 0, R.drawable.insurance_icici, R.drawable.insurance_tataaia };
	int insurance_vendor_ids[] =
	{ 0, 48, 49 };
	// ///////////////////
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

	ArrayAdapter<String> adapter, adapter1, adapter2;
	LayoutInflater inflator;

	private static int flag;
	String[] nwinfo;
	String[] transaction_info;
	static double creditcard_charges = 2.60;
	static double debitcard_charges = 1.35;
	static double debitcard_charges2 = 1.65;
	static double netbanking_charges = 2.45;
	static double charges_multiplier = 0.0;
	static double additional_charges = 0.0;

	static TextView payment_billno_text, payment_remarks_text, payment_amount_text, additional_details_text, additionalcharges_text;
	static Spinner payment_categories_spinner, payment_vendors_spinner, payment_modes_spinner, circle_spinner;
	ImageButton number_option_imagebutton;
	Button autoselect_button, plan_option_button, proceed_bills_button;

	List<String> list = new ArrayList<String>();

	String regex;
	Matcher matcher;
	String e1 = "", e2 = "", e3 = "", e4 = "", e5 = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		paybills_view = inflater.inflate(R.layout.fragment_paybills, container, false);
		try
		{
			check = new connection_check(getActivity());

			SharedPreferences default_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

			SharedPreferences prefs = getActivity().getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			cust_loginid = prefs.getString("loginid", "");
			cust_username = prefs.getString("username", "");
			cust_email = prefs.getString("email", "");
			cust_phone = prefs.getString("phone", "");
			cust_main_balance = prefs.getString("balance", "");
			pref_paymentmode = default_prefs.getString("pref_paymentmode", "");

			// ///INITIALIZE VALUES FROM XML ///
			circles_names = getResources().getStringArray(R.array.circles_names);

			payment_categories_spinner = (Spinner) paybills_view.findViewById(R.id.payment_categories_spinner);
			payment_vendors_spinner = (Spinner) paybills_view.findViewById(R.id.payment_vendors_spinner);
			payment_modes_spinner = (Spinner) paybills_view.findViewById(R.id.payment_modes_spinner);
			circle_spinner = (Spinner) paybills_view.findViewById(R.id.circle_spinner);
			circle_spinner.setVisibility(View.GONE);

			// /BUTTONS////
			number_option_imagebutton = (ImageButton) paybills_view.findViewById(R.id.number_option_imagebutton);
			number_option_imagebutton.setOnClickListener(this);
			autoselect_button = (Button) paybills_view.findViewById(R.id.autoselect_button);
			autoselect_button.setVisibility(View.GONE);
			autoselect_button.setOnClickListener(this);
			plan_option_button = (Button) paybills_view.findViewById(R.id.plan_option_button);
			plan_option_button.setOnClickListener(this);
			proceed_bills_button = (Button) paybills_view.findViewById(R.id.proceed_bills_button);
			proceed_bills_button.setOnClickListener(this);

			// /TEXTVIEWS
			additionalcharges_text = (TextView) paybills_view.findViewById(R.id.additionalcharges_text);
			payment_billno_text = (TextView) paybills_view.findViewById(R.id.payment_billno_text);
			additional_details_text = (TextView) paybills_view.findViewById(R.id.additional_details_text);
			additional_details_text.setVisibility(View.GONE);
			payment_remarks_text = (TextView) paybills_view.findViewById(R.id.payment_remarks_text);
			payment_amount_text = (TextView) paybills_view.findViewById(R.id.payment_amount_text);
			payment_amount_text.addTextChangedListener(new TextWatcher()
			{
				public void afterTextChanged(Editable s)
				{
					if (payment_amount_text.getText().toString().trim().length() < 8)
					{
						if (payment_modes_spinner.getSelectedItemPosition() > 0)
						{
							if (payment_modes_spinner.getSelectedItem().equals("Credit Cards"))
							{
								charges_multiplier = creditcard_charges;
							}
							else if (payment_modes_spinner.getSelectedItem().equals("Debit Cards"))
							{
								if (!payment_amount_text.getText().toString().equals(""))
								{
									if (Float.parseFloat(payment_amount_text.getText().toString()) <= 1000)
									{
										charges_multiplier = debitcard_charges;
									}
									else
									{
										charges_multiplier = debitcard_charges2;
									}
								}
							}
							else if (payment_modes_spinner.getSelectedItem().equals("Net Banking"))
							{
								charges_multiplier = netbanking_charges;
							}
							else
							{
								charges_multiplier = 0.0;
							}

							if (!payment_amount_text.getText().toString().equals(""))
							{

								additional_charges = (Double.parseDouble(payment_amount_text.getText().toString()) * charges_multiplier) / 100;
								additionalcharges_text.setText("Provisional chrages \u20B9 "
										+ String.valueOf(formatter.format(additional_charges))
										+ "   ,Total Amount \u20B9"
										+ formatter.format(additional_charges
												+ Float.parseFloat(payment_amount_text.getText().toString().trim())));
								// Log.e("total amount",
								// String.valueOf(formatter.format(additional_charges+Integer.parseInt(payment_amount_text.getText().toString().trim()))));
							}
							else
							{
								additionalcharges_text.setText("");
							}
						}
					}
					else
					{
						payment_amount_text.setError("Amount is out of range");
						// Toast.makeText(getActivity(),
						// "Entered amount is out of range",
						// Toast.LENGTH_SHORT).show();
					}
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after)
				{
				}

				public void onTextChanged(CharSequence s, int start, int before, int count)
				{
				}
			});

			// intiate values

			payment_categories_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages, bills_cat,
					bills_images));

			URL = selectwebservice.currentwebservice();

			payment_categories_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
				{

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
					else
					{

					}
					if (payment_categories_spinner.getSelectedItem().equals("Select Category"))
					{
						call_clear_fields();
					}

					else if (payment_categories_spinner.getSelectedItem().equals("Electricity Bills"))
					{
						// call method
						call_clear_fields();

						// set adapter
						payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
								electricity_vendor_names, electricity_vendor_images));

						// set text
						additional_details_text.setText("");
						payment_amount_text.setText("");

						// set hint
						payment_billno_text.setHint("Electricity Bill No");
						additional_details_text.setHint("Enter Cycle Number if available");
						// payment_misc_text.setInputType(type);
						circle_spinner.setSelection(1);

						// set visivibility
						additional_details_text.setVisibility(View.VISIBLE);
						payment_billno_text.setVisibility(View.VISIBLE);
						payment_vendors_spinner.setVisibility(View.VISIBLE);
						payment_modes_spinner.setVisibility(View.VISIBLE);
						circle_spinner.setVisibility(View.GONE);
						payment_amount_text.setVisibility(View.VISIBLE);
						payment_remarks_text.setVisibility(View.VISIBLE);

					}

					else if (payment_categories_spinner.getSelectedItem().equals("Insurance Premiums"))
					{
						// call method
						call_clear_fields();

						// set adapter
						payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
								insurance_vendor_names, insurance_vendor_images));

						// set text
						additional_details_text.setText("");
						payment_amount_text.setText("");
						if (fill_method.equals("insurance"))
						{
							payment_billno_text.setText(prefvalue_1);
							additional_details_text.setText(prefvalue_2);
							fill_method = "";
						}

						// set hint
						payment_billno_text.setHint("Policy No");
						additional_details_text.setHint("Enter date of birth(DD-MM-YYYY)");
						circle_spinner.setSelection(1);

						// set visivibility
						payment_billno_text.setVisibility(View.VISIBLE);
						additional_details_text.setVisibility(View.VISIBLE);
						payment_vendors_spinner.setVisibility(View.VISIBLE);
						payment_modes_spinner.setVisibility(View.VISIBLE);
						circle_spinner.setVisibility(View.GONE);
						payment_amount_text.setVisibility(View.VISIBLE);
						payment_remarks_text.setVisibility(View.VISIBLE);
					}
					else if (payment_categories_spinner.getSelectedItem().equals("Mobile Postpaid"))
					{
						new Thread(new Runnable()
						{
							public void run()
							{

							}
						}).start();
						// call method
						call_clear_fields();

						// set adapter
						payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
								postpaid_vendor_names, postpaid_vendor_images));

						// set text
						additional_details_text.setText("");
						payment_amount_text.setText("");
						if (fill_method.equals("postpaid"))
						{
							payment_billno_text.setText(prefvalue_1);
							call_db(payment_billno_text.getText().toString());
							fill_method = "";
						}

						// set hint
						payment_billno_text.setHint("Mobile No");

						// set visivibility
						autoselect_button.setVisibility(View.VISIBLE);
						payment_billno_text.setVisibility(View.VISIBLE);
						additional_details_text.setVisibility(View.GONE);
						payment_vendors_spinner.setVisibility(View.VISIBLE);
						payment_modes_spinner.setVisibility(View.VISIBLE);
						circle_spinner.setVisibility(View.VISIBLE);
						payment_amount_text.setVisibility(View.VISIBLE);
						payment_remarks_text.setVisibility(View.VISIBLE);
					}
					else if (payment_categories_spinner.getSelectedItem().equals("Landline Bills"))
					{
						// call method
						call_clear_fields();

						// set adapter
						payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
								landline_vendor_names, landline_vendor_images));

						// set text
						additional_details_text.setText("");
						payment_amount_text.setText("");
						if (fill_method.equals("landline"))
						{
							payment_billno_text.setText(prefvalue_1);
							additional_details_text.setText(prefvalue_2);
							fill_method = "";
						}

						// set hint
						payment_billno_text.setHint("LandLine No with std code");
						additional_details_text.setHint("Account number as in your bill");

						// set visivibility
						autoselect_button.setVisibility(View.GONE);
						payment_billno_text.setVisibility(View.VISIBLE);
						additional_details_text.setVisibility(View.VISIBLE);
						payment_vendors_spinner.setVisibility(View.VISIBLE);
						payment_modes_spinner.setVisibility(View.VISIBLE);
						circle_spinner.setVisibility(View.VISIBLE);
						payment_amount_text.setVisibility(View.VISIBLE);
						payment_remarks_text.setVisibility(View.VISIBLE);
					}
					else if (payment_categories_spinner.getSelectedItem().equals("Gas Bills"))
					{
						// call method
						call_clear_fields();

						// set adapter
						payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
								gas_vendor_names, gas_vendor_images));

						// set text
						additional_details_text.setText("");
						payment_amount_text.setText("");

						// set hint
						payment_billno_text.setHint("Gas Bill No");
						circle_spinner.setSelection(1);

						// set visivibility
						autoselect_button.setVisibility(View.GONE);
						payment_billno_text.setVisibility(View.VISIBLE);
						additional_details_text.setVisibility(View.GONE);
						payment_vendors_spinner.setVisibility(View.VISIBLE);
						payment_modes_spinner.setVisibility(View.VISIBLE);
						circle_spinner.setVisibility(View.GONE);
						payment_amount_text.setVisibility(View.VISIBLE);
						payment_remarks_text.setVisibility(View.VISIBLE);
					}

					else
					{
						// circle_spinner.setAdapter(null);
						// payment_modes_spinner.setAdapter(null);
						// payment_vendors_spinner.setAdapter(null);
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
					// set_layout();
					if (payment_amount_text.getText().toString().trim().length() < 8)
					{
						if (payment_modes_spinner.getSelectedItem().equals("Credit Cards"))
						{
							charges_multiplier = creditcard_charges;
						}
						else if (payment_modes_spinner.getSelectedItem().equals("Debit Cards"))
						{
							if (!payment_amount_text.getText().toString().equals(""))
							{
								if (Float.parseFloat(payment_amount_text.getText().toString()) <= 1000)
								{
									charges_multiplier = debitcard_charges;
								}
								else
								{
									charges_multiplier = debitcard_charges2;
								}
							}
						}
						else if (payment_modes_spinner.getSelectedItem().equals("Net Banking"))
						{
							charges_multiplier = netbanking_charges;
						}
						else
						{
							charges_multiplier = 0.0;
						}

						if (!payment_amount_text.getText().toString().equals(""))
						{

							additional_charges = (Double.parseDouble(payment_amount_text.getText().toString()) * charges_multiplier) / 100;
							additionalcharges_text.setText("Provisional chrages \u20B9 "
									+ String.valueOf(formatter.format(additional_charges))
									+ "   ,Total Amount \u20B9"
									+ formatter.format(additional_charges
											+ Float.parseFloat(payment_amount_text.getText().toString().trim())));
						}
						else
						{
							additionalcharges_text.setText("");
						}
					}
					else
					{
						payment_amount_text.setError("Amount is out of range");
						// Toast.makeText(getActivity(),
						// "Entered amount is out of range",
						// Toast.LENGTH_SHORT).show();
					}

				}

				public void onNothingSelected(AdapterView<?> arg0)
				{
					Toast.makeText(getActivity(), "nothing selected", Toast.LENGTH_SHORT).show();

				}
			});
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on fragment_paybills oncreate\n" + e.toString());
		}
		return paybills_view;
	}

	@Override
	public void onClick(View v)
	{
		try
		{
			switch (v.getId())
			{
			case R.id.number_option_imagebutton:

			{
				PopupMenu popup = new PopupMenu(getActivity(), v);
				popup.getMenu().add(0, 0, 0, "My postpaid");
				popup.getMenu().add(0, 1, 1, "Select from contacts");
				popup.getMenu().add(0, 2, 2, "My Landline");
				popup.getMenu().add(0, 3, 3, "My Insurance");
				// popup.getMenu().add(0, 4, 4, "My Electicity Bill");
				// popup.getMenu().add(0, 5, 5, "My Gas Bill");
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
							prefvalue_1 = default_prefs.getString("pref_mymobile2", "");
							if (prefvalue_1.equals(""))
							{
								payment_billno_text.setText("");
								Toast.makeText(getActivity(), "No data found for postpaid in QuickPay", Toast.LENGTH_SHORT).show();
							}
							else
							{
								payment_categories_spinner.setSelection(3);
								fill_method = "postpaid";
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
							prefvalue_1 = default_prefs.getString("pref_mylandline", "");
							prefvalue_2 = default_prefs.getString("pref_mylandlinecustno", "");
							if ((prefvalue_1.equals("")) || (prefvalue_2.equals("")))
							{
								payment_billno_text.setText("");
								additional_details_text.setText("");
								Toast.makeText(getActivity(), "No data found for landline in QuickPay", Toast.LENGTH_SHORT).show();
							}
							else
							{
								payment_categories_spinner.setSelection(4);
								fill_method = "landline";

							}
						}
						else if (item.getItemId() == 3)
						{
							prefvalue_1 = default_prefs.getString("pref_myinsurance", "");
							prefvalue_2 = default_prefs.getString("pref_myinsurancedate", "");
							if ((prefvalue_1.equals("")) || (prefvalue_2.equals("")))
							{
								payment_billno_text.setText("");
								additional_details_text.setText("");
								Toast.makeText(getActivity(), "No data found for insurance in QuickPay", Toast.LENGTH_SHORT).show();
							}
							else
							{
								payment_categories_spinner.setSelection(2);
								fill_method = "insurance";
							}
						}
						return true;
					}
				});
				break;
			}
			case R.id.autoselect_button:
			{
				if (payment_billno_text.getText().toString().equals("") || payment_billno_text.getText().toString().length() < 9)
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
				PopupMenu plans_popup = new PopupMenu(getActivity(), v);
				plans_popup.getMenu().add(0, 0, 0, "Rs 10");
				plans_popup.getMenu().add(0, 1, 1, "Rs 20");
				plans_popup.getMenu().add(0, 2, 2, "Rs 100");
				plans_popup.getMenu().add(0, 3, 3, "Current plans");
				plans_popup.show();

				plans_popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
				{
					@Override
					public boolean onMenuItemClick(MenuItem item)
					{
						TextView getdata = (TextView) paybills_view.findViewById(R.id.payment_amount_text);
						if (item.getItemId() == 0)
						{
							getdata.setText("10");
						}
						else if (item.getItemId() == 1)
						{
							getdata.setText("20");
						}
						else if (item.getItemId() == 2)
						{
							getdata.setText("100");
						}

						return true;
					}
				});
				break;
			}
			case R.id.proceed_bills_button:
			{
				if (payment_categories_spinner.getSelectedItem().toString().equals("Electricity Bills"))
				{
					if (payment_vendors_spinner.getSelectedItemPosition() == 1)
					{
						if ((payment_billno_text.getText().toString().trim().length() >= 9)
								&& (payment_billno_text.getText().toString().trim().length() <= 11))
							e1 = "";
						else
						{
							e1 = "Bill number should be atleast 9 to 11 digits long";
							payment_billno_text.setError(e1);
						}
					}
					else if (payment_vendors_spinner.getSelectedItemPosition() == 2)
					{
						if ((payment_billno_text.getText().toString().trim().length() >= 9)
								&& (payment_billno_text.getText().toString().trim().length() <= 11))
							e1 = "";
						else
						{
							e1 = "Bill number should be atleast 9 to 11 digits long";
							payment_billno_text.setError(e1);
						}
					}
					else if (payment_vendors_spinner.getSelectedItemPosition() == 3)
					{
						if (payment_billno_text.getText().toString().trim().length() == 11)
							e1 = "";
						else
						{
							e1 = "TATA Power Bill number should be 11 digits long";
							payment_billno_text.setError(e1);
						}
					}
					else if (payment_vendors_spinner.getSelectedItemPosition() == 4)
					{
						if (payment_billno_text.getText().toString().trim().length() == 9)
							e1 = "";
						else
						{
							e1 = "Reliance Power Bill number should be 9 digits long";
							payment_billno_text.setError(e1);
						}
					}
				}
				else if (payment_categories_spinner.getSelectedItem().toString().equals("Insurance Premiums"))
				{
					if (payment_vendors_spinner.getSelectedItemPosition() == 1)
					{
						if (payment_billno_text.getText().toString().trim().length() == 8)
							e1 = "";
						else
						{
							e1 = "ICICI Policy number should be 8 digits long";
							payment_billno_text.setError(e1);
						}
					}
					else if (payment_vendors_spinner.getSelectedItemPosition() == 2)
					{
						if (payment_billno_text.getText().toString().trim().length() == 10)
							e1 = "";
						else
						{
							e1 = "TATA AIA Policy number should be 10 digits long";
							payment_billno_text.setError(e1);
						}
					}
				}
				else if (payment_categories_spinner.getSelectedItem().toString().equals("Mobile Postpaid"))
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
							e1 = "Phone number should be 10 digits long";
							payment_billno_text.setError(e1);
						}
					}
					else
					{
						e1 = "Phone number not valid";
						payment_billno_text.setError(e1);
					}
				}
				else if (payment_categories_spinner.getSelectedItem().toString().equals("Landline Bills"))
				{
					if ((payment_billno_text.getText().toString().trim().length() >= 9)
							&& (payment_billno_text.getText().toString().trim().length() <= 11))
						e1 = "";
					else
					{
						e1 = "Landline number including std code should be atleast 9 digits long";
						payment_billno_text.setError(e1);
					}
				}
				else if (payment_categories_spinner.getSelectedItem().toString().equals("Gas Bills"))
				{
					if (payment_billno_text.getText().toString().trim().length() == 12)
						e1 = "";
					else
					{
						e1 = "Mahanagar Gas number should be 12 digits long";
						payment_billno_text.setError(e1);
					}
				}

				// regex = "^[0-9]{0,5}$";
				// matcher =
				// Pattern.compile(regex).matcher(payment_amount_text.getText().toString().trim());
				if (payment_amount_text.getText().toString().trim().length() < 8)
				{
					e3 = "";
				}
				else
				{
					e3 = "Invalid rupee value";
					payment_amount_text.setError(e3);
				}

				if ((payment_categories_spinner.getSelectedItemPosition() == 0) || (payment_vendors_spinner.getSelectedItemPosition() == 0)
						|| (payment_modes_spinner.getSelectedItemPosition() == 0) || (circle_spinner.getSelectedItemPosition() == 0)
						|| payment_billno_text.getText().toString().trim().equals("")
						|| payment_amount_text.getText().toString().trim().equals(""))

				{
					Toast.makeText(getActivity(), "Please fill in the required data", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if (e1.equals("") && e2.equals("") && e3.equals(""))
					{
						if (Float.parseFloat(payment_amount_text.getText().toString().trim()) >= 10)
						{

							if (payment_modes_spinner.getSelectedItem().toString().equals("Credit Cards")
									|| payment_modes_spinner.getSelectedItem().toString().equals("Debit Cards")
									|| payment_modes_spinner.getSelectedItem().toString().equals("Net Banking"))
							{
								// call_otherpayments();

								Payments paybills = new Payments(getActivity());
								paybills.show_payments_dialog(
										main_flag,
										payment_categories_spinner.getSelectedItem().toString().trim(),
										payment_modes_spinner.getSelectedItem().toString(),
										formatter.format(additional_charges
												+ Float.parseFloat(payment_amount_text.getText().toString().trim())));
							}
							else
							{
								if (check.isnetwork_available())
								{
									ptype = "Wallet";
									Payments.p_state = "SUCCESS";
									methodname = "transaction_main";
									new paybills_asynctask(getActivity()).execute(methodname);
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
							// Toast.makeText(getActivity(),
							// "Minimum amount should be higher than 10 rupees",
							// Toast.LENGTH_SHORT).show();
						}

					}
					else
					{
						// Toast.makeText(getActivity(),
						// "Please resolve the following errors\n" + e1 + "\n" +
						// e2 + "\n" + e3,
						// Toast.LENGTH_SHORT).show();
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

	public void call_clear_fields()
	{
		// set hint
		payment_billno_text.setHint("Select Category");
		payment_billno_text.setInputType(InputType.TYPE_CLASS_NUMBER);
		// set text
		payment_billno_text.setText("");
		additional_details_text.setText("");
		payment_amount_text.setText("");
		payment_remarks_text.setText("");
		// set visibility
		payment_billno_text.setVisibility(View.INVISIBLE);
		autoselect_button.setVisibility(View.GONE);
		additional_details_text.setVisibility(View.INVISIBLE);
		payment_vendors_spinner.setVisibility(View.INVISIBLE);
		payment_modes_spinner.setVisibility(View.INVISIBLE);
		circle_spinner.setVisibility(View.INVISIBLE);
		payment_amount_text.setVisibility(View.INVISIBLE);
		payment_remarks_text.setVisibility(View.INVISIBLE);
	}

	// ///

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
			// Log.e("report length   ", String.valueOf(txns.length()));
			JSONObject e = txns.getJSONObject(0);
			int flag = Integer.parseInt(e.getString("flag"));
			// Log.e("flag   ", String.valueOf(flag));

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
							new paybills_asynctask(mcontext).execute(methodname);
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
						Payments paybills = new Payments(mcontext);
						paybills.show_payments_dialog(main_flag, payment_categories_spinner.getSelectedItem().toString().trim(),
								payment_modes_spinner.getSelectedItem().toString(),
								formatter.format(additional_charges + Float.parseFloat(payment_amount_text.getText().toString().trim())));
					}
					else
					{
						if (retry_count < 1)
						{
							methodname = "transaction_main";
							new paybills_asynctask(mcontext).execute(methodname);
							retry_count = retry_count + 1;
						}
						else
						{
							methodname = "call_refund";
							new paybills_asynctask(mcontext).execute(methodname);
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
			Log.e("crash", "error on payment_method" + e);
		}
	}

	// /result intent for contacts
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data)
	{
		super.onActivityResult(reqCode, resultCode, data);
		try
		{
			switch (reqCode)
			{
			case (11):
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
							TextView getdata = (TextView) paybills_view.findViewById(R.id.payment_billno_text);
							getdata.setText(String.valueOf(number));
						}
					}
					break;
				}
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on onresultactivity\n" + e.toString());
		}
	}

	public void set_layout()
	{
		try
		{
			RelativeLayout layout = (RelativeLayout) paybills_view.findViewById(R.id.amount_relativelayout);
			for (int i = 0; i < layout.getChildCount(); i++)
			{
				View child = layout.getChildAt(i);
				child.setEnabled(true);
			}
			layout = (RelativeLayout) paybills_view.findViewById(R.id.billno_relativelayout);
			for (int i = 0; i < layout.getChildCount(); i++)
			{
				View child = layout.getChildAt(i);
				child.setEnabled(true);
			}
			TextView payment_remarks = (TextView) paybills_view.findViewById(R.id.payment_remarks_text);
			payment_remarks.setEnabled(true);
			Button proceed_button = (Button) paybills_view.findViewById(R.id.proceed_bills_button);
			proceed_button.setClickable(true);
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
				// Log.e("inside cursor output", c.getString(0));
				Operator = c.getString(c.getColumnIndex("Operator"));
				Circle = c.getString(c.getColumnIndex("Circle"));
				if (Operator.equals("TATA Docomo"))

				{
					payment_vendors_spinner.setSelection(7, true);
				}
				else
				{
					payment_vendors_spinner.setSelection(Arrays.asList(postpaid_vendor_names).indexOf(Operator), true);
				}

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
			Log.e(getClass().getSimpleName(), "Could not create or Open the database");
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack())
		{
			web.goBack();
			return true;
		}
		return super.getActivity().onKeyDown(keyCode, event);
	}

	// ////////////// ASYNC TASK FOR SENDING DATA TO WEBSERVICE /////////

	static class paybills_asynctask extends AsyncTask<String, Integer, String>
	{
		ProgressDialog progress;

		public paybills_asynctask(Context context)
		{
			mcontext = context;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(mcontext, "Processing Transaction", "Please wait...");
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

					PropertyInfo subcategoryprop = new PropertyInfo();
					subcategoryprop.setName("subcategory");
					subcategoryprop.setValue(payment_categories_spinner.getSelectedItem().toString());
					subcategoryprop.setType(String.class);
					request.addProperty(subcategoryprop);

					PropertyInfo circleprop = new PropertyInfo();
					circleprop.setName("circle");
					circleprop.setValue(circle_spinner.getSelectedItem().toString());
					circleprop.setType(Integer.class);
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
					int op_id = 0;
					if (payment_categories_spinner.getSelectedItem().toString().equals("Electricity Bills"))
					{
						if (payment_vendors_spinner.getSelectedItem().toString().equals("BSES Rajdhani Power Limited - Delhi"))
						{
							op_id = 43;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("BSES Yamuna Power Limited - Delhi"))
						{
							op_id = 44;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("Tata Power Delhi Limited - Delhi"))
						{
							op_id = 45;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("Reliance Energy Limited"))
						{
							op_id = 46;
						}
					}
					else if (payment_categories_spinner.getSelectedItem().toString().equals("Insurance Premiums"))
					{
						if (payment_vendors_spinner.getSelectedItem().toString().equals("ICICI Prudential Life Insurance"))
						{
							op_id = 48;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("Tata AIA Life Insurance"))
						{
							op_id = 49;
						}
					}
					else if (payment_categories_spinner.getSelectedItem().toString().equals("Mobile Postpaid"))
					{
						if (payment_vendors_spinner.getSelectedItem().toString().equals("Airtel"))
						{
							op_id = 31;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("Vodafone"))
						{
							op_id = 33;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("BSNL Mobile"))
						{
							op_id = 36;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("Reliance CDMA"))
						{
							op_id = 35;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("Reliance GSM"))
						{
							op_id = 34;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("Idea"))
						{
							op_id = 32;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("Tata Indicom"))
						{
							op_id = 39;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("LOOP Mobile"))
						{
							op_id = 40;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("Tata Docomo GSM"))
						{
							op_id = 38;
						}

					}
					else if (payment_categories_spinner.getSelectedItem().toString().equals("Landline Bills"))
					{
						if (payment_vendors_spinner.getSelectedItem().toString().equals("Airtel Landline"))
						{
							op_id = 42;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("BSNL Landline"))
						{
							op_id = 37;
						}
						else if (payment_vendors_spinner.getSelectedItem().toString().equals("MTNL Landline"))
						{
							op_id = 41;
						}
					}
					else if (payment_categories_spinner.getSelectedItem().toString().equals("Gas Bills"))
					{
						if (payment_vendors_spinner.getSelectedItem().toString().equals("Mahanagar Gas"))
						{
							op_id = 47;
						}
					}
					operator_idprop.setValue(op_id);
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
					charges_prop.setValue(String.valueOf(additional_charges));
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
					parameter1_prop.setValue(additional_details_text.getText().toString().trim());
					parameter1_prop.setType(String.class);
					request.addProperty(parameter1_prop);

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
					outp = str;
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
				if (result.equals("transactioncall_executed"))
				{
					payment_status(outp);
				}
				else if (resultstatus.equals("call_refund_executed"))
				{
					Toast.makeText(mcontext, "Transaction amount will be refunded to your account shortly", Toast.LENGTH_SHORT).show();
				}

			}
			catch (Exception e)
			{
				Log.e("error", "error on paybills_asynctask post execute\n" + e);
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
