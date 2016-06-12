package PMF.protectMyFile.PMF;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class inscription extends Activity {
	DatabaseHelper helper= new DatabaseHelper(this);
	private Button button;
	  protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.in);
	       button = (Button)findViewById(R.id.button1);
	       button.setOnClickListener(new OnClickListener() {
	           public void onClick(View arg0) {
	      EditText email=(EditText)findViewById(R.id.Email);   	
	      EditText username=(EditText)findViewById(R.id.username); 	   
	      EditText password=(EditText)findViewById(R.id.password);     
	      EditText check=(EditText)findViewById(R.id.check);     	
	      String emailstr=email.getText().toString();    
	      String usernamestr=username.getText().toString();    
	      String passwordstr=password.getText().toString();
	      String checkstr=check.getText().toString();
	           
	       
	   
      	Boolean user =false;
      	 user=helper.checkuser(usernamestr);
	      
	      if(!passwordstr.equals(checkstr))   
	        {
	        	 Toast.makeText(getApplicationContext(), "Mot de passe invalide!",
		        	      Toast.LENGTH_SHORT).show();
	        	 startActivity(new Intent(getApplicationContext(),inscription.class));
	        }  
	           
	         else
	            {
	    boolean uname_flag = helper.checkuser(usernamestr);
	    if(uname_flag)
	    {
	     //insert the detailes in database
	     contact c = new contact();
	     c.setusername(usernamestr);
	     c.setemail(emailstr);
	     c.setpassword(passwordstr);

	     helper.insertcontact(c);
	     startActivity(new Intent(getApplicationContext(),MainActivity.class));
	     Toast.makeText(getApplicationContext(), "Votre inscription a ete effectué avec succes !",Toast.LENGTH_SHORT).show();
	     
	    }
	    else
	    {
	     //popup msg
	     Toast unamemsg = Toast.makeText(inscription.this , "Ce nom d'utilistateur est déjà utilisé , Merci de choisir un autre" , Toast.LENGTH_SHORT);
	     unamemsg.show();
	    }
	            }
	        	 
	           
	           }
	           
	           
	           
	           
	           
	           });
	  
	  
	  
	  
	  
	  
	  }
	  
	  
	  
	  
	  
	  
}
