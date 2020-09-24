package msoft.attendancegpm;
import androidx.appcompat.app.AppCompatActivity;

import android.os.*;
import android.widget.*;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;
public class StudentInfo extends AppCompatActivity{
  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student_info);
    new GetStud().execute(new Long[]{new Long(17340)});

  }
  class GetStud extends AsyncTask<Long,Integer,String>{
    @Override
    protected void onPreExecute(){
      super.onPreExecute();
    }
    @Override
    protected String doInBackground(Long... longs){
      Long rollno=longs[0];
      String res="No Student Found";
      SoapObject soapObject = new SoapObject("http://tempuri.org/", "StudInfo");
      soapObject.addProperty("rollno",rollno);

      SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
      envelope.dotNet=true;
      envelope.setOutputSoapObject(soapObject);

      try{
        HttpTransportSE httpTransportSE=new HttpTransportSE("http://attendance.gpmiraj.ac.in/GPM.asmx");
        httpTransportSE.call("http://tempuri.org/StudInfo",envelope);
        res=((SoapPrimitive)envelope.getResponse()).toString();
      }
      catch(Exception ex){
        Toast.makeText(StudentInfo.this,""+ex.toString(),Toast.LENGTH_SHORT).show();
      }
      return res;
    }
    @Override
    protected void onPostExecute(String s){
      TextView tv=findViewById(R.id.txtInfo);
      tv.setText(s);
      super.onPostExecute(s);
    }
  }
}
