package com.ezeepay.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ezeepay.services.Booktickets_Activity.booktickets_asynctask;
import com.ezeepay.services.Fragment_paybills.paybills_asynctask;
import com.ezeepay.services.Fragment_recharge.recharge_asynctask;

public class Payments
{

	static String ptype = "";
	static String bankcd = "";
	String address;
	static int pay_flag = 0;
	static String murl = "";
	public static Context mcontext;
	private String cust_loginid, cust_main_balance;
	private static String cust_username = "";
	private static String cust_email = "";
	private static String cust_phone = "";
	static String p_state = "";
	private static String mops = "";
	Runnable methodname;// = "";
	static String is_error = "";
	private static Boolean inside_page = false;
	private static WebView sample_webview;
	private static ImageView cardtype_image;

	public Payments(Context context)
	{
		mcontext = context;
		SharedPreferences prefs = mcontext.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
		cust_loginid = prefs.getString("loginid", "");
		cust_username = prefs.getString("username", "");
		cust_email = prefs.getString("email", "");
		cust_phone = prefs.getString("phone", "");
		cust_main_balance = prefs.getString("balance", "");

	}

	public static void show_payments_dialog(final String main_flag, final String payment_category, final String payment_type,
			final String txn_amount)
	{

		int retry_count = 0;

		// try
		{
			final String banks_category[] = mcontext.getResources().getStringArray(R.array.banks_category);
			final String banks_category_id[] = mcontext.getResources().getStringArray(R.array.banks_category_id);
			final String creditcards_cat[] = mcontext.getResources().getStringArray(R.array.creditcards_cat);
			int creditcards_images[] =
			{ 0, R.drawable.cards_visa02, R.drawable.cards_mastercard, R.drawable.cards_americanexpress };
			final String debitcards_cat[] = mcontext.getResources().getStringArray(R.array.debitcards_cat);
			int debitcards_images[] =
			{ 0, R.drawable.cards_visa02, R.drawable.cards_mastercard, R.drawable.cards_maestro, R.drawable.cards_maestro };
			final String months_category[] = mcontext.getResources().getStringArray(R.array.months_category);
			final String years_category[] = mcontext.getResources().getStringArray(R.array.years_category);

			final TextView cardholder_text, cardno_text, cvvno_text, cvv_label;

			final Spinner paymentoption_spinner, expirymonth_spinner, expiryyear_spinner;

			LayoutInflater inflater = LayoutInflater.from(mcontext);
			final View payment_view = inflater.inflate(R.layout.paymentmethod, null);

			// // intialize views
			paymentoption_spinner = (Spinner) payment_view.findViewById(R.id.paymentoption_spinner);

			sample_webview = (WebView) payment_view.findViewById(R.id.sample_webview);

			// ((ViewGroup) payment_view).addView(sample_webview, new
			// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 700));

			// sample_webview.setVisibility(View.GONE);
			final Button pay_button = (Button) payment_view.findViewById(R.id.pay_button);

			final LinearLayout userinput_layout = (LinearLayout) payment_view.findViewById(R.id.userinput_linearlayout);
			userinput_layout.setVisibility(View.GONE);

			cardno_text = (TextView) payment_view.findViewById(R.id.cardno_text);
			cardtype_image = (ImageView) payment_view.findViewById(R.id.cardtype_image);
			cardholder_text = (TextView) payment_view.findViewById(R.id.cardholder_text);
			expirymonth_spinner = (Spinner) payment_view.findViewById(R.id.expirymonth_spinner);
			expiryyear_spinner = (Spinner) payment_view.findViewById(R.id.expiryyear_spinner);
			cvvno_text = (TextView) payment_view.findViewById(R.id.cvvno_text);
			cvv_label = (TextView) payment_view.findViewById(R.id.cvv_label);

			inside_page = false;

			// //// reset count
			retry_count = 0;

			ArrayAdapter<String> month_adapter = new ArrayAdapter<String>(mcontext, android.R.layout.simple_spinner_item, months_category);
			month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			expirymonth_spinner.setAdapter(month_adapter);

			ArrayAdapter<String> year_adapter = new ArrayAdapter<String>(mcontext, android.R.layout.simple_spinner_item, years_category);
			year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			expiryyear_spinner.setAdapter(year_adapter);

			if (payment_type.equals("Credit Cards"))
			{
				paymentoption_spinner.setAdapter(new customspinner_adapter((Activity) mcontext, R.layout.spinneritems_withimages,
						creditcards_cat, creditcards_images));
				ptype = "CC";
				bankcd = "CC";
			}

			else if (payment_type.equals("Debit Cards"))
			{
				paymentoption_spinner.setAdapter(new customspinner_adapter((Activity) mcontext, R.layout.spinneritems_withimages,
						debitcards_cat, debitcards_images));
				ptype = "DC";
				bankcd = "VISA";

			}
			else
			{
				ArrayAdapter<String> bank_adapter = new ArrayAdapter<String>((Activity) mcontext, android.R.layout.simple_spinner_item,
						banks_category);
				bank_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				paymentoption_spinner.setAdapter(bank_adapter);
				ptype = "NB";
				bankcd = "NB";
				userinput_layout.setVisibility(View.GONE);
			}

			cardno_text.addTextChangedListener(new TextWatcher()
			{
				public void afterTextChanged(Editable s)
				{
					char s2 = 'd';
					if (s.toString().equals(""))
					{
						cardtype_image.setImageResource(0);
					}
					else
					{
						if (String.valueOf(s.charAt(0)).equals("4"))
						{

							cardtype_image.setImageResource(R.drawable.cards_visa02);
						}
						else if (String.valueOf(s.charAt(0)).equals("5"))
						{

							cardtype_image.setImageResource(R.drawable.cards_mastercard);
						}
						else if (s.length() > 1)
						{
							if ((s.equals("62")))
							{

								cardtype_image.setImageResource(R.drawable.cards_maestro);
							}
						}
						else
						{
							cardtype_image.setImageResource(0);
						}
					}
					// Log.e("fff",String.valueOf(s.charAt(0)).equals("4"));
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after)
				{
				}

				public void onTextChanged(CharSequence s, int start, int before, int count)
				{

				}
			});

			paymentoption_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
				{
					expirymonth_spinner.setVisibility(View.VISIBLE);
					expiryyear_spinner.setVisibility(View.VISIBLE);
					cvvno_text.setVisibility(View.VISIBLE);
					cvv_label.setVisibility(View.VISIBLE);
					if (payment_type.equals("Net Banking"))
						bankcd = banks_category_id[paymentoption_spinner.getSelectedItemPosition()];
					else
						userinput_layout.setVisibility(View.VISIBLE);
					if (paymentoption_spinner.getSelectedItem().equals("Visa Card"))
						bankcd = "VISA";
					else if (paymentoption_spinner.getSelectedItem().equals("Master Card"))
						bankcd = "MAST";
					else if (paymentoption_spinner.getSelectedItem().equals("Maestro Card"))
					{
						bankcd = "MAES";
						expirymonth_spinner.setVisibility(View.GONE);
						expiryyear_spinner.setVisibility(View.GONE);
						cvvno_text.setVisibility(View.GONE);
						cvv_label.setVisibility(View.GONE);
					}
					else if (paymentoption_spinner.getSelectedItem().equals("SBI Maestro Card"))
					{
						bankcd = "SMAE";
						expirymonth_spinner.setVisibility(View.GONE);
						expiryyear_spinner.setVisibility(View.GONE);
						cvvno_text.setVisibility(View.GONE);
						cvv_label.setVisibility(View.GONE);
					}
				}

				public void onNothingSelected(AdapterView<?> arg0)
				{
					Toast.makeText(mcontext, "nothing selected", Toast.LENGTH_SHORT).show();
				}
			});

			final AlertDialog.Builder payment_dialog_builder = new AlertDialog.Builder(mcontext);
			payment_dialog_builder.setView(payment_view);
			final ProgressDialog pd = new ProgressDialog(mcontext);
			pd.setCancelable(false);
			pd.setMessage("Processing ...");
			final Dialog payment_dialog = payment_dialog_builder.create();
			payment_dialog.setOnKeyListener(new Dialog.OnKeyListener()
			{
				@Override
				public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event)
				{
					Boolean x;
					if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() != KeyEvent.ACTION_DOWN))
					{
						x = true;
						AlertDialog.Builder cancel_txn_builder = new AlertDialog.Builder(mcontext);
						cancel_txn_builder.setMessage("Transaction not complete ,are you sure?");
						cancel_txn_builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								payment_dialog.dismiss();
								if (inside_page)
								{
									p_state = "TIMEOUT";
									if (main_flag.equals("Recharge"))
									{
										recharge_asynctask m = new recharge_asynctask(mcontext);
										m.execute("transaction_main");
									}
									else if (main_flag.equals("Bill Payments"))
									{
										paybills_asynctask p = new paybills_asynctask(mcontext);
										p.execute("transaction_main");
									}
									else if (main_flag.equals("TicketBooking"))
									{
										booktickets_asynctask b = new booktickets_asynctask(mcontext);
										b.execute("blocktickets_main");
									}
								}

							}
						});
						cancel_txn_builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								dialog.cancel();
							}
						});
						AlertDialog cancel_txn_dialog = cancel_txn_builder.create();
						cancel_txn_dialog.show();
					}
					else
					{
						x = false;
					}
					return x;
				}
			});

			pay_button.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{

					try
					{
						if (ptype.equals("NB"))
						{
							if (paymentoption_spinner.getSelectedItemPosition() == 0)

								is_error = "Please select a valid bank name";
							else
								is_error = "";
						}
						else if (bankcd.equals("SMAE") || (bankcd.equals("MAES")))
						{
							if (cardholder_text.getText().toString().equals("") || cardno_text.getText().toString().equals(""))
							{
								is_error = "Please fill all the fields!!";
							}
							else
							{
								is_error = "";
							}
						}
						else
						{
							if (cardholder_text.getText().toString().equals("") || cardno_text.getText().toString().equals("")
									|| expirymonth_spinner.getSelectedItemPosition() == 0
									|| expiryyear_spinner.getSelectedItemPosition() == 0 || cvvno_text.getText().toString().equals(""))
							{

								is_error = "Please fill all the fields!!";
							}
							else
							{
								is_error = "";
							}
							// else
							// {
							// is_error = "";
							// regex = "^4[0-9]{12}(?:[0-9]{3})?$";
							// matcher =
							// Pattern.compile(regex).matcher(cardno_text.getText().toString().trim());
							// if (matcher.find())
							// {
							// is_error="";
							// }
							// else
							// {
							// is_error = "Enter a valid visa card mumber";
							// }
							// }
						}

						if (is_error.equals(""))
						{

							paymentoption_spinner.setVisibility(View.GONE);
							pay_button.setVisibility(View.GONE);
							userinput_layout.setVisibility(View.GONE);
							sample_webview.setVisibility(View.VISIBLE);

							String key, txnid, amount, productinfo, firstname, email, phone, surl, furl, curl, salt;
							String pg, bankcode = "", ccnum = "", ccname = "", ccvv = "";
							String ccexpmon = "";
							String ccexpyr = "";
							String tempe = "";

							// parameters values part 1
							key = "asjjZn";
							Random rand = new Random();
							String rndm = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
							txnid = hashCal("SHA-256", rndm).substring(0, 20);
							amount = txn_amount;
							productinfo = payment_category;
							if (ptype.equals("NB"))
							{
								firstname = cust_username;
							}
							else
							{
								firstname = cardholder_text.getText().toString().trim();
							}

							email = cust_email;
							phone = cust_phone;
							surl = "www.guru3d.com";
							furl = "www.bing.com";
							curl = "www.fudzilla.com";
							salt = "dHzwOBnl";
							// // hash string
							tempe = key + "|" + txnid + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||"
									+ salt;
							// // parameter values part 2
							pg = ptype;
							bankcode = bankcd;
							ccnum = cardno_text.getText().toString().trim();
							ccname = cardholder_text.getText().toString().trim();
							ccvv = cvvno_text.getText().toString().trim();
							ccexpmon = String.valueOf(expirymonth_spinner.getSelectedItemPosition());
							ccexpyr = expiryyear_spinner.getSelectedItem().toString();

							String querywithseam = "key=" + key + "&txnid=" + txnid + "&amount=" + amount + "&productinfo=" + productinfo
									+ "&firstname=" + firstname + "&email=" + email + "&phone=" + phone + "&surl=" + surl + "&furl=" + furl
									+ "&curl=" + curl + "&hash=" + hashCal("SHA-512", tempe) + "&pg=" + pg + "&bankcode=" + bankcode
									+ "&ccnum=" + ccnum + "&ccname=" + ccname + "&ccvv=" + ccvv + "&ccexpmon=" + ccexpmon + "&ccexpyr="
									+ ccexpyr;

							sample_webview.getSettings().setJavaScriptEnabled(true);
							sample_webview.getSettings().setBuiltInZoomControls(true);
							sample_webview.getSettings().setRenderPriority(RenderPriority.HIGH);
							sample_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
							// sample_webview.getSettings().se
							// sample_webview.getSettings().setLoadWithOverviewMode(true);
							// sample_webview.getSettings().setUseWideViewPort(true);
							// sample_webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
							// sample_webview.requestFocus(View.FOCUS_DOWN |
							// View.FOCUS_UP);
							sample_webview.getSettings().setUserAgentString("Android");
							pay_flag = 0;
							sample_webview.postUrl("https://secure.payu.in/_payment", EncodingUtils.getBytes(querywithseam, "BASE64"));
							sample_webview.setWebViewClient(new WebViewClient()
							{
								@Override
								public void onPageStarted(WebView view, String url, Bitmap favicon)
								{
									pd.show();
									inside_page = true;
								}

								@Override
								public boolean shouldOverrideUrlLoading(WebView view, String url)
								{
									try
									{
										view.loadUrl(url);
									}
									catch (Exception e)
									{
										Log.e("error", "error in shouldOverrideUrlLoading method\n" + e.toString());
									}
									return true;
								}

								@Override
								public void onPageFinished(WebView view, String url)
								{
									try
									{
										if (url.contains("mihpayid"))
										{
											murl = url;
											Log.e("mihpayid FOUND in pagefinish", url);
										}

										pd.dismiss();

										if (pay_flag == 0)
										{
											if (url.contains("www.guru3d.com"))
											{
												p_state = "SUCCESS";
												payment_dialog.dismiss();
												pay_flag = 3;
												if (main_flag.equals("Recharge"))
												{
													recharge_asynctask m = new recharge_asynctask(mcontext);
													m.execute("transaction_main");
												}
												else if (main_flag.equals("Bill Payments"))
												{
													paybills_asynctask p = new paybills_asynctask(mcontext);
													p.execute("transaction_main");
												}
												else if (main_flag.equals("TicketBooking"))
												{
													booktickets_asynctask b = new booktickets_asynctask(mcontext);
													b.execute("blocktickets_main");
												}
											}
											else if (url.contains("www.bing.com"))
											{
												p_state = "FAILED";
												payment_dialog.dismiss();
												pay_flag = 3;

												if (main_flag.equals("Recharge"))
												{
													recharge_asynctask m = new recharge_asynctask(mcontext);
													m.execute("transaction_main");
												}
												else if (main_flag.equals("Bill Payments"))
												{
													paybills_asynctask p = new paybills_asynctask(mcontext);
													p.execute("transaction_main");
												}
												else if (main_flag.equals("TicketBooking"))
												{
													booktickets_asynctask b = new booktickets_asynctask(mcontext);
													b.execute("blocktickets_main");
												}
											}
										}
										else
										{
											Log.e("secondtime", url);
										}
									}
									catch (Exception e)
									{
										Log.e("error", "error in page finish method\n" + e.toString());
									}
								}
							});
						}
						else
						{
							Toast.makeText(mcontext, "Please fill the required fields!!", Toast.LENGTH_SHORT).show();
						}
					}
					catch (Exception e)
					{
						Log.e("error", "error in pay_button select\n" + e.toString());
					}
				}
			});

			payment_dialog.show();

		}
		// catch (Exception e)
		{
			// Log.e("crash", "error on other payments overall method" +
			// e.toString());
		}
	}

	// public boolean onKeyDown(int keyCode, KeyEvent event)
	// {
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && sample_webview.canGoBack())
	// {
	// sample_webview.goBack();
	// return true;
	// }
	// return super.mcontext.onKeyDown(keyCode, event);
	// }

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

	// private int getScale(){
	// Display display = ((WindowManager)
	// mcontext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	// int width = display.getWidth();
	// Double val = new Double(width)/new Double(PIC_WIDTH);
	// val = val * 100d;
	// return val.intValue();
	// }

	// public static String mpincheck()
	// {
	//
	// AlertDialog.Builder b = new AlertDialog.Builder(mcontext);
	// b.setTitle("Please enter your mpin");
	// final EditText input = new EditText(mcontext);
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
	// Intent intent1 = new Intent(mcontext, Makepayments_Activity.class);
	// startActivity(intent1);
	// }
	// else
	// {
	// Toast.makeText(mcontext, "Invalid mpin!", Toast.LENGTH_SHORT).show();
	// onfundtransfer_button_select(view);
	// }
	//
	// }
	// });
	// b.setCancelable(false);
	// b.setNeutralButton("FORGOT MPIN", new DialogInterface.OnClickListener()
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
	// return null;
	// }

}
