package msoft.attendancegpm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Attendance extends AppCompatActivity {
    List<Long> ls;
    ButtonAdapter adapter;
    List<Long> present;
    LinearLayout pb;
    Button btnSubmit;
    List<Long> absent;
    GridLayout view;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        id=getIntent().getIntExtra("id",0);
        view=findViewById(R.id.gridView);
        adapter=new ButtonAdapter(this,R.layout.toggle_button);
        //view.setAdapter(adapter);
        btnSubmit=findViewById(R.id.btnSubmit);
        pb=findViewById(R.id.pb);
        task t=new task();
        t.execute();
        present=new ArrayList<Long>();
        absent=new ArrayList<Long>();
    }
    class ButtonAdapter extends BaseAdapter{
        List<Long> Roll;
        Context context;
        LayoutInflater inflater;
        int layout;
        public ButtonAdapter(Context context,int layout) {
            Roll =new ArrayList<Long>();
            this.context = context;
            this.layout=layout;
            inflater=LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return Roll.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addAll(List<Long> Roll){
            this.Roll.addAll(Roll);
            this.notifyDataSetChanged();
            int r=0,c=0;
            for(int i=0;i<this.Roll.size();i++,c++)
            {
                if(c==5)
                {
                    c=0;
                    r++;
                }
                GridLayout.Spec row=GridLayout.spec(r,1);
                GridLayout.Spec col=GridLayout.spec(c,1);
                GridLayout.LayoutParams prm = new GridLayout.LayoutParams(row,col);
                prm.setMargins(5,5,5,5);
                prm.width=120;
                view.addView(getView(i,null,null),prm);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=inflater.inflate(layout,null);
            ToggleButton btn=view.findViewById(R.id.btn);
            btn.setId(Integer.parseInt(Roll.get(position)+""));
            btn.setText(Roll.get(position).toString());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton v1=(ToggleButton)v;
                    if(v1.isChecked()){
                        v1.setBackgroundResource(R.color.green);
                    }
                    else
                        v1.setBackgroundResource(R.color.red);
                    add(v1);
                }
            });


            return view;
        }
    }
    public void submit(View view) {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(Attendance.this);
        builder.setMessage("Do you want Submit Attendance ?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                Toast.makeText(Attendance.this, "Submitting", Toast.LENGTH_SHORT).show();
                                task2 t=new task2();
                                t.execute();
                            }
                        });
        builder.setNegativeButton("No",new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                Toast.makeText(Attendance.this, "Canceled", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    public  class task2 extends AsyncTask<Long,Integer,Long>{
       // ProgressDialog pd=new ProgressDialog(getApplicationContext());
        @Override
        protected Long doInBackground(Long... longs) {
            for(Long l:present){
                try {
                    SoapObject soapObject = new SoapObject("http://tempuri.org/", "InsertAtt");
                    soapObject.addProperty("id",id);
                    soapObject.addProperty("rollno",l);
                    soapObject.addProperty("status",0);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet=true;
                    envelope.setOutputSoapObject(soapObject);


                    HttpTransportSE httpTransportSE = new HttpTransportSE("http://attendance.gpmiraj.ac.in/GPM.asmx");
                    httpTransportSE.call("http://tempuri.org/InsertAtt", envelope);

                    String response=envelope.getResponse().toString();
                } catch (Exception ex) {
                    System.out.println("Error" + ex.toString());
                }
            }
            for(Long l:absent){
                try {
                    SoapObject soapObject = new SoapObject("http://tempuri.org/", "InsertAtt");
                    soapObject.addProperty("id",id);
                    soapObject.addProperty("rollno",l);
                    soapObject.addProperty("status",1);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet=true;
                    envelope.setOutputSoapObject(soapObject);


                    HttpTransportSE httpTransportSE = new HttpTransportSE("http://attendance.gpmiraj.ac.in/GPM.asmx");
                    httpTransportSE.call("http://tempuri.org/InsertAtt", envelope);

                    String response=envelope.getResponse().toString();
                } catch (Exception ex) {
                    System.out.println("Error" + ex.toString());
                }
            }
            return (long)-1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            Attendance.this.finish();
        }
    }

    class task extends AsyncTask<List<Long>,Integer,List<Long>> {

        @Override
        protected void onPostExecute(List<Long> longs) {
            super.onPostExecute(longs);
            ls=longs;
            adapter.addAll(ls);
            absent.addAll(ls);
        }

        @Override
        protected List<Long> doInBackground(List<Long>... lists) {
            List<Long> res=new ArrayList<Long>();
            try {
                SoapObject soapObject = new SoapObject("http://tempuri.org/", "GetRollno");
                soapObject.addProperty("id",id);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet=true;
                envelope.setOutputSoapObject(soapObject);


                HttpTransportSE httpTransportSE = new HttpTransportSE("http://attendance.gpmiraj.ac.in/GPM.asmx");
                httpTransportSE.call("http://tempuri.org/GetRollno", envelope);

                SoapObject response=(SoapObject) envelope.getResponse();
                for(int i=0;i<response.getPropertyCount();i++){
                    Long in=Long.parseLong(response.getProperty(i).toString());
                    res.add(in);
                }

            } catch (Exception ex) {
                System.out.println("Error" + ex.toString());
            }
            return res;
        }

    }
    public void add(ToggleButton btn){
        Long roll=Long.parseLong(btn.getText().toString());
        if(btn.isChecked()){
            if(present.indexOf(roll)==-1){
                present.add(roll);
                absent.remove(roll);
            }
        }
        else {
            if(absent.indexOf(roll)==-1){
                absent.add(roll);
                present.remove(roll);
            }
        }
    }



}
