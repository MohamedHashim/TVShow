package com.mohamedhashim.tvshow;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Mohamed Hashim on 30/10/2016.
 */
public class ScanNfc extends AppCompatActivity {

    private ImageView cardHand;
    private ImageView deviceHand, back_btn;
    private TextView mTextView, toolbartxt,UID,name_txt,company_name,table_number,attendee_type_txt,event_name;
    private NfcAdapter mNfcAdapter;
    ProgressDialog dialog;
    String name, id,mail, nfc_id, event_title, company, phone_number, table, photo, site_id,attendee_type;
    String checkin_url = "http://ae.nfcvalet.com/mobile/Test?";
    String TV_url="http://ae.nfcvalet.com/mobile/postdesktop?";
    private Button button;
    TextView a1,a2,a3,a4,a5;

    private final String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        themeColor = R.color.intro1_bg;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_nfc);
//        AnimationPageOne.getInstance().destroyInstance();
//        initAnimation();
        mTextView = (TextView) findViewById(R.id.textView_explanation);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        toolbartxt = (TextView) findViewById(R.id.toolbartxt);
//        toolbartxt.setText("NFC Scanner");
        UID= (TextView) findViewById(R.id.UID);
        name_txt= (TextView) findViewById(R.id.name);
        company_name= (TextView) findViewById(R.id.company_name);
        table_number= (TextView) findViewById(R.id.table_number);
        attendee_type_txt= (TextView) findViewById(R.id.attendee_type);
        event_name= (TextView) findViewById(R.id.event_name);

        a1= (TextView) findViewById(R.id.a1);
        a2= (TextView) findViewById(R.id.a2);
        a3= (TextView) findViewById(R.id.a3);
        a4= (TextView) findViewById(R.id.a4);
        a5= (TextView) findViewById(R.id.a5);
        a1.setVisibility(View.INVISIBLE);
        a2.setVisibility(View.INVISIBLE);
        a3.setVisibility(View.INVISIBLE);
        a4.setVisibility(View.INVISIBLE);
        a5.setVisibility(View.INVISIBLE);

//        back_btn = (ImageView) findViewById(R.id.back_btn);
//        back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled");
        } else {
            mTextView.setText("Scan NFC Card");
        }
        button= (Button) findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(ScanNfc.this);
                dialog.setMessage("Loading ...");
//            dialog.setTitle("Connecting server");
                dialog.show();
                dialog.setCancelable(true);
                RequestQueue MyRequestQueue2 = Volley.newRequestQueue(getApplicationContext());
                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, TV_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Attendee Response", response);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Sent Successfully!",Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Server is not responding", Toast.LENGTH_LONG).show();
                    }
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> MyData = new HashMap<String, String>();
                        MyData.put("name", name_txt.getText().toString());
                        MyData.put("company_name", company_name.getText().toString());
                        MyData.put("table_number", table_number.getText().toString());
                        MyData.put("attendee_type", attendee_type_txt.getText().toString());
                        MyData.put("event_name", event_name.getText().toString());

                        return MyData;
                    }
                };
                MyRequestQueue2.add(MyStringRequest);
            }
        });
//        handleIntent(getIntent());
        button.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            id = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
            mTextView.setText(
                    "NFC Card\n" + id);
            UID.setText(id);
            dialog = new ProgressDialog(ScanNfc.this);
            dialog.setMessage("Loading ...");
//            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(true);
            RequestQueue MyRequestQueue2 = Volley.newRequestQueue(getApplicationContext());
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, checkin_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    a1.setVisibility(View.VISIBLE);
                    a2.setVisibility(View.VISIBLE);
                    a3.setVisibility(View.VISIBLE);
                    a4.setVisibility(View.VISIBLE);
                    a5.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    Log.d("Attendee Response", response);

                    JSONObject jsono = null;
                    JSONObject object = null;
                    try {
                        jsono = new JSONObject(response);
                        object = jsono.getJSONObject("attendee");
                        name = object.getString("first_name") + " " + object.getString("last_name");
                        mail = object.getString("email");
                        nfc_id = object.getString("nfc_attendee_id");
                        event_title = object.getString("event_name");
                        company = object.getString("c_name");
                        phone_number = object.getString("phone_number");
                        table = object.getString("t_number");
                        photo = object.getString("photo");
                        attendee_type = object.getString("attendee_type");



                        name_txt.setText(name);
                        company_name.setText(company);
                        table_number.setText(table);
                        attendee_type_txt.setText(attendee_type);
                        event_name.setText(event_title);
                        dialog.dismiss();
//                        Intent intent = new Intent(Peugeot_NFC_activity.this, Peugeot_In_Out_Activity.class);
//                        intent.putExtra("name", name);
//                        intent.putExtra("mail", mail);
//                        intent.putExtra("nfc_id", nfc_id);
//                        intent.putExtra("event_title", event_title);
//                        intent.putExtra("company", company);
//                        intent.putExtra("phone_number", phone_number);
//                        intent.putExtra("table", table);
//                        intent.putExtra("photo", photo);
//                        intent.putExtra("attendee_type", attendee_type);
//
//                        startActivity(intent);
//                        finish();

                        Log.d("Response", response + "\n" + object + "\n" + name + "\n" + nfc_id + "\n" + event_title + "\n" + company + "\n" + phone_number + "\n" + table + "\n" + photo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", error.toString());
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Server is not responding", Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("nfc_attendee_id", UID.getText().toString());

                    return MyData;
                }
            };
            MyRequestQueue2.add(MyStringRequest);
        }
    }
//    private void initAnimation() {
//        cardHand = (ImageView) findViewById(R.id.card_hand);
//        deviceHand = (ImageView) findViewById(R.id.device_hand);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AnimationPageOne.getInstance().initAnimation(cardHand, deviceHand, new WeakReference<Context>(ScanNfc.this));
//            }
//        }, 600);
//    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
}