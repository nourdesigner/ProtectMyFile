package PMF.protectMyFile.PMF;

import android.os.Bundle;
//import android.view.Menu;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	DatabaseHelper helper= new DatabaseHelper(this);
   private EditText  username=null;
   private EditText  password=null;
   private TextView attempts;
   private Button button2;
   private Button button;
   int counter = 3;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      username = (EditText)findViewById(R.id.input1);
      password = (EditText)findViewById(R.id.input2);
      attempts = (TextView)findViewById(R.id.textView4);
      attempts.setText(Integer.toString(counter));
      button = (Button)findViewById(R.id.button1);


      
      button.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
    	  String str= username.getText().toString();
          String pass= password.getText().toString();
      
     String password=helper.searchPass(str);
     
    	  if(pass.equals(password))
    	  {
    		  Toast.makeText(getApplicationContext(), "Bienvenue "+str,
    			      Toast.LENGTH_SHORT).show();
    			      startActivity(new Intent(getApplicationContext(),Menu.class));
    		  
    	  }
    	  else
    	  {
    		   Toast.makeText(getApplicationContext(), "Le nom d'utilisateur ou la mot de passe est incorrecte !",
    				      Toast.LENGTH_SHORT).show();
    				      attempts.setBackgroundColor(Color.RED);	
    				      counter--;
    				      attempts.setText(Integer.toString(counter));
    				      if(counter==0){
    				         button.setEnabled(false);
    				      }

    	  }
     
  
  
    	  } });
}

 //  public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
    //  getMenuInflater().inflate(R.menu.main, menu);
  //    return true;
  //}

}
