package PMF.protectMyFile.PMF;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExplorateurActivity extends ListActivity implements OnSharedPreferenceChangeListener {
	/**
	 * Repr�sente le texte qui s'affiche quand la liste est vide
	 */
	String fnameS;
	String fname;
	private TextView mEmpty = null;
	/**
	 * La liste qui contient nos fichiers et r�pertoires
	 */
	private ListView mList = null;
	/**
	 * Notre Adapter personnalis� qui lie les fichiers � la liste
	 */
	private FileAdapter mAdapter = null;
	
	/**
	 * Repr�sente le r�pertoire actuel
	 */
	private File mCurrentFile = null;
	/**
	 * Couleur voulue pour les r�pertoires
	 */
	private int mColor = 0;
	/**
	 * Indique si l'utilisateur est � la racine ou pas
	 * pour savoir s'il veut quitter
	 */
	private boolean mCountdown = false;
	
	/**
	 * Les pr�f�rences partag�es de cette application
	 */
	private SharedPreferences mPrefs = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorateur);
        
        // On r�cup�re la ListView de notre activit�
        mList = (ListView) getListView();
        
        // On v�rifie que le r�pertoire externe est bien accessible
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        	// S'il ne l'est pas, on affiche un message
        	mEmpty = (TextView) mList.getEmptyView();
        	mEmpty.setText("Vous ne pouvez pas acc�der aux fichiers");
        } else {
        	// S'il l'est...
        	// On d�clare qu'on veut un menu contextuel sur les �l�ments de la liste
        	registerForContextMenu(mList);
        	
        	// On r�cup�re les pr�f�rences de l'application 
        	mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        	// On indique que l'acitivt� est � l'�coute des changements de pr�f�rence
        	mPrefs.registerOnSharedPreferenceChangeListener(this);
        	// On r�cup�re la couleur voulue par l'utilisateur, par d�faut il s'agira du rouge
        	mColor = mPrefs.getInt("repertoireColorPref", Color.RED);
        	
        	// On r�cup�re la racine de la carte SD pour qu'elle soit 
        	mCurrentFile = Environment.getExternalStorageDirectory();
        	
        	// On change le titre de l'activit� pour y mettre le chemin actuel
        	setTitle(mCurrentFile.getAbsolutePath());
        	
        	// On r�cup�re la liste des fichiers dans le r�pertoire actuel
        	File[] fichiers = mCurrentFile.listFiles();
        	
        	// On transforme le tableau en une structure de donn�es de taille variable
        	ArrayList<File> liste = new ArrayList<File>();
        	for(File f : fichiers)
        		liste.add(f);
        	
        	mAdapter = new FileAdapter(this, android.R.layout.simple_list_item_1, liste);
        	// On ajoute l'adaptateur � la liste
        	mList.setAdapter(mAdapter);
        	// On trie la liste
        	mAdapter.sort();
        	
        	// On ajoute un Listener sur les items de la liste
        	mList.setOnItemClickListener(new OnItemClickListener() {

        		// Que se passe-il en cas de cas de clic sur un �l�ment de la liste ?
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long id) {
					File fichier = mAdapter.getItem(position);
					// Si le fichier est un r�pertoire...
					if(fichier.isDirectory())
						// On change de r�pertoire courant
						updateDirectory(fichier);
					else
						// Sinon on lance l'irzm
						seeItem(fichier);
				}
			});
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_explorateur, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_options:
            	// Intent explicite
                Intent i = new Intent(this, ExploreurPreference.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View vue,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, vue, menuInfo);
        
        MenuInflater inflater = getMenuInflater();
        // On r�cup�re des informations sur l'item par apport � l'Adapter
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;

        // On r�cup�re le fichier consern� par le menu contextuel
        File fichier = mAdapter.getItem(info.position);
        // On a deux menus, en fonction qu'il s'agit d'un r�pertoire ou d'un fichier
        if(!fichier.isDirectory())
        	inflater.inflate(R.menu.context_file, menu);
        else
        	inflater.inflate(R.menu.context_dir, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // On r�cup�re la position de l'item concern�
        File fichier = mAdapter.getItem(info.position);
        switch (item.getItemId()) {
          //  case R.id.deleteItem:
           // 	mAdapter.remove(fichier);
            //	fichier.delete();
            	//return true;
            case R.id.signer:
            try {
				mAdapter.signe(fichier);
			} catch (DigestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
              fname = fichier.getAbsolutePath();
            return true;
            case R.id.seeItem:
            	seeItem(fichier);
            	return true;
        }
        return super.onContextItemSelected(item);
    }
    
    /**
     * Utilis� pour visualiser un fichier
     * @param pFile le fichier � visualiser
     */
    private void seeItem(File pFile) {
    	// On cr�� un Intent
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	
    	// On d�termine son type MIME
    	MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = pFile.getName().substring(pFile.getName().indexOf(".") + 1).toLowerCase();
        String type = mime.getMimeTypeFromExtension(ext);
    	
        // On ajoute les informations n�cessaires
    	i.setDataAndType(Uri.fromFile(pFile), type);
    	
    	try {
			startActivity(i);
			// Et s'il n'y a pas d'activit� qui puisse g�rer ce type de fichier
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "Oops, you have no application that can launch this file", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
    }
    
    /**
     * Utilis� pour naviguer entre les r�pertoires
     * @param pFile le nouveau r�pertoire dans lequel aller
     */
    
    public void updateDirectory(File pFile) {
    	// On change le titre de l'activit�
    	setTitle(pFile.getAbsolutePath());
    	
    	// L'utilisateur ne souhaite plus sortir de l'application
    	mCountdown = false;
    	
    	// On change le repertoire actuel
    	mCurrentFile = pFile;
    	// On vide les r�pertoires actuels
    	setEmpty();

    	// On r�cup�re la liste des fichiers du nouveau r�pertoire
    	File[] fichiers = mCurrentFile.listFiles();
    	
    	// Si le r�pertoire n'est pas vide...
    	if(fichiers != null)
    		// On les ajoute �  l'adaptateur
    		for(File f : fichiers)
    			mAdapter.add(f);
    	// Et on trie l'adaptateur
    	mAdapter.sort();
    }
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// Si on a appuy� sur le retour arri�re
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		// On prend le parent du r�pertoire courant
    		File parent = mCurrentFile.getParentFile();
    		// S'il y a effectivement un parent
    		if(parent != null)
    			updateDirectory(parent);
    		else {
    			// Sinon, si c'est la premi�re fois qu'on fait un retour arri�re
    			if(mCountdown != true) {
    				// On indique � l'utilisateur qu'appuyer dessus une seconde fois le fera sortir
    				Toast.makeText(this, "We are already at the root! Click a second time to exit", Toast.LENGTH_SHORT).show();
    	    		mCountdown  = true;
    			} else
    				// Si c'est la seconde fois on sort effectivement
    				finish();
    		}
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    /**
     * On enl�ve tous les �l�ments de la liste
     */
    
    public void setEmpty() {
    	// Si l'adapteur n'est pas vide...
    	if(!mAdapter.isEmpty())
    		// Alors on le vide !
    		mAdapter.clear();
    }
    
    /**
     * L'adaptateur sp�cifique � nos fichiers
     */
    
    private class FileAdapter extends ArrayAdapter<File> {
    	/**
    	 * Permet de comparer deux fichiers
    	 *
    	 */
        private class FileComparator implements Comparator<File> {

    		public int compare(File lhs, File rhs) {
    			// si lhs est un r�pertoire et pas l'autre, il est plus petit
    			if(lhs.isDirectory() && rhs.isFile())
    				return -1;
    			// dans le cas inverse, il est plus grand
    			if(lhs.isFile() && rhs.isDirectory())
    				return 1;
    			
    			//Enfin on ordonne en fonction de l'ordre alphab�tique sans tenir compte de la casse
    			return lhs.getName().compareToIgnoreCase(rhs.getName());
    		}
        	
        }
        
    	public FileAdapter(Context context, int textViewResourceId,
				List<File> objects) {
			super(context, textViewResourceId, objects);
			mInflater = LayoutInflater.from(context);
		}

		
		public void  signe  (File fichier) throws Exception ,IOException
				

		{
			
		try{	
				FileInputStream in = null;
				
			           InputStream is = new FileInputStream(fname);
			           SecureRandom random=SecureRandom.getInstance("SHA1PRNG", "Crypto"); 
			           KeyPairGenerator KeyGen= KeyPairGenerator.getInstance("RSA", "BC");
                        int bitsize=2048;
                        KeyGen.initialize(bitsize, random);
                        KeyPair pair=KeyGen.generateKeyPair();
                        PrivateKey priv= pair.getPrivate();
                        PublicKey pub=pair.getPublic();
                        Signature rsa = Signature.getInstance("SHA1withRSA","Crypto");
                        rsa.initSign(priv);
                        BufferedInputStream bufin=new BufferedInputStream(is);
                        byte[] buffer = new byte[8192];
                        int len;
                        while(bufin.available()!=0)
                        {
                        	len=bufin.read(buffer);
                        	rsa.update(buffer,0, len);
                        	rsa.sign(buffer,0,len);
                            
                        };
                        bufin.close();
                      //  byte[]  realSig=rsa.sign();
                     
		}
		catch (Exception priv) {
			//throw new Exception(priv);
			
			// Toast.makeText(getApplicationContext(), "le document n'a pas �t� sign�", 
	        	    //  Toast.LENGTH_SHORT).show();
			Log.i("bn1", "le doc n a pas �t� sign�");
		}
				
			
				  Log.i("bn", "le doc a �t� sign�");
                Toast.makeText(getApplicationContext(), "le document a �t� sign�", 
		        	      Toast.LENGTH_SHORT).show();
		        
		       
		}

		private LayoutInflater mInflater = null;
		
		/**
		 * Construit la vue en fonction de l'item
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView vue = null;
			
			if(convertView != null)
				vue = (TextView) convertView;
			else
				vue = (TextView) mInflater.inflate(android.R.layout.simple_list_item_activated_1, null);
			File item = getItem(position);
			//Si c'est un r�pertoire, on choisit la couleur dans les pr�f�rences
			if(item.isDirectory())
				vue.setTextColor(mColor);
			else
				// Sinon c'est du noir
				vue.setTextColor(Color.BLACK);
			vue.setText(item.getName());
			return vue;
		}
		
		/**
		 * Pour trier rapidement les �l�ments de l'adaptateur
		 */
		public void sort () {
			super.sort(new FileComparator());
		}
    }
    
    /**
     * Se d�clenche d�s qu'une pr�f�rence a chang�
     */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		mColor = sharedPreferences.getInt("repertoireColorPref", Color.BLACK);
		mAdapter.notifyDataSetInvalidated();
	}
}
