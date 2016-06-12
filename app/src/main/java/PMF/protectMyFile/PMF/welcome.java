package PMF.protectMyFile.PMF;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class welcome extends Activity{
	
	//  private EditText  text2=null;
	 //  private EditText  password=null;
	   private TextView text;
	   private Button button;
	   private Button button2;
	  // int counter = 3;
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.welcome);
	      text = (TextView)findViewById(R.id.textView1);
	      button2 = (Button)findViewById(R.id.button2);
	    //  button2.setBackgroundColor(Color.GRAY);
		   button2.setOnClickListener(new OnClickListener() {


										  public void onClick(View arg0) {

			startActivity(new Intent(getApplicationContext(),inscription.class));
										  }
									  }
		   );
	      
	      button = (Button)findViewById(R.id.button1);
	   //   button.setBackgroundColor(Color.GRAY);
	      button.setOnClickListener(new OnClickListener() {

	   
	    	  public void onClick(View arg0) {
	     
	   // Intent intent = new Intent("android.intent.action.MAIN");
	      //intent.setClass(MainActivity.this, List.class);
	      startActivity(new Intent(getApplicationContext(),MainActivity.class));
	    	  }
	      }
	    		  );	
	  	
	    	  
	      
}
	      
}
