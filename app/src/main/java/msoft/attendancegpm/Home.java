package msoft.attendancegpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Home extends AppCompatActivity {
    int id;
    String res;
    int schid=-1;
    TextView name,sch;
    Button rf,btnLogOut;
    SQLiteDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db=new SQLiteDB(this);
        Intent i=getIntent();
        String s=i.getStringExtra("staff");
        name=findViewById(R.id.txtName);
        name.setText(s.split("~")[1]);
        id=Integer.parseInt(s.split("~")[0]);
        sch=findViewById(R.id.txtSchedule);
        task t=new task();
        t.execute();
        rf=findViewById(R.id.btnRefresh);
        rf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task t=new task();
                t.execute();
            }
        });
        btnLogOut=findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteUser();
                startActivity(new Intent(Home.this,MainActivity.class));
                finish();
            }
        });
    }

    public void takeAttd(View view) {
        if(schid==-1)
            return;
        Intent i=getIntent();
        i.setClass(getApplicationContext(),Attendance.class);
        i.putExtra("id",schid);
        startActivity(i);
    }

    class task extends AsyncTask<String,Integer,String> {
        String sche;
        @Override
        protected String doInBackground(String[] str) {
            String response="N/A";
            try {
                SoapObject soapObject = new SoapObject("http://tempuri.org/", "GetSchedule");
                soapObject.addProperty("id",id);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet=true;
                envelope.setOutputSoapObject(soapObject);


                HttpTransportSE httpTransportSE = new HttpTransportSE("http://attendance.gpmiraj.ac.in/GPM.asmx");
                httpTransportSE.call("http://tempuri.org/GetSchedule", envelope);
                response = ((SoapPrimitive) envelope.getResponse()).toString();

                Home.this.res=response;

            } catch (Exception ex) {
                System.out.println("Error" + ex.toString());
            }
            return response;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String o) {
            if(!o.equals("-1")) {
                ((Button)findViewById(R.id.btnTake)).setEnabled(true);
                schid = Integer.parseInt(o.split("~")[0]);
                sch.setText(o.split("~")[1]);
            }
        }
    }
}
