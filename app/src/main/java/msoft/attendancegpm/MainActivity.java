package msoft.attendancegpm;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;

public class MainActivity extends AppCompatActivity {
    public String res="";
    EditText txtusername,txtpassword;
    CheckBox cb;
    Button cancel;
    Intent i;
    SQLiteDB db;
    int StaffId;
    //sqldb sdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sdb=new sqldb(getApplicationContext());
        setContentView(R.layout.activity_main);
        txtusername=(EditText)findViewById(R.id.txtUsername);
        txtpassword=findViewById(R.id.txtPassword);
        i=new Intent(getApplicationContext(),Home.class);
        Button b1=findViewById(R.id.btnLogin);
        cancel=findViewById(R.id.btnCancel);
        db=new SQLiteDB(this);
        cb=findViewById(R.id.checkBox);

        if(!db.isAvailable().equals("-1")) {
            i.putExtra("staff", db.isAvailable());
            startActivity(i);
            this.finish();
        }


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task t=new task();
                boolean conn=t.connection();
                if(conn==true)
                {
                    t.execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Connect To Internet",Toast.LENGTH_SHORT).show();
                }

            }
        });



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtusername.setText("");
                txtpassword.setText("");

            }
        });
    }


    class task extends AsyncTask<String,Integer,String>{
        final String u = txtusername.getText().toString();
        final String p = txtpassword.getText().toString();
        ProgressDialog pd;
        @Override
        protected String doInBackground(String[] str) {
            String response="N/A";
            try {
                SoapObject soapObject = new SoapObject("http://tempuri.org/", "GetLogin");
                soapObject.addProperty("username",u);
                soapObject.addProperty("password",p);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet=true;
                envelope.setOutputSoapObject(soapObject);


                AndroidHttpTransport httpTransportSE = new AndroidHttpTransport("http://attendance.gpmiraj.ac.in/GPM.asmx");
                httpTransportSE.call("http://tempuri.org/GetLogin", envelope);
                response = ((SoapPrimitive) envelope.getResponse()).toString();
                if(response.equals("-1"))
                    return "-1";
                i.putExtra("staff",response);
                int sid=Integer.parseInt(response.split("~")[0]);
                //sdb.saveUser(sid);
                StaffId=sid;

            } catch (Exception ex) {
                System.out.println("Error" + ex.toString());
            }
                return response;
        }

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(MainActivity.this);
            pd.setMessage("Logging in....");
            pd.show();
        }

        @Override
        protected void onPostExecute(String o) {
            pd.dismiss();
            if(o.equals("Error"))
                Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            else {
                if (cb.isChecked()) {
                    db.saveUser(o);
                }
                startActivity(i);
                MainActivity.this.finish();
            }
        }
        public  boolean connection()
        {
            ConnectivityManager cm =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            return  isConnected;
        }

    }
}
