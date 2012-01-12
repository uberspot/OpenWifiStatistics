package Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

/** Class containing some useful functions for easy usage of the storage capabilities in an Android device.
 */
public class StorageUtils extends ContextWrapper {

	/** Initial constructor
	 * @param base the context of the activity calling the utilities.
	 */
	public StorageUtils(Context base) {
		super(base);
	}
	
	/** Checks if a file with the given name already exists in the internal storage.
	 * @param fileName the name of the file to check
	 * @return true if the file exists, false otherwise
	 */
	public boolean fileExists(String fileName){
		String[] files = fileList();
		for(String file: files) {
			if(file.equalsIgnoreCase(fileName)){
				return true;
			}
		}
		return false;
	}
	
	/** Appends data to the given file.
	 * @param fileName the name of the file to append data to
	 * @param data an array of bytes to append to the file
	 * @return true if the operation was successful, false otherwise
	 */
	public boolean appendToFile(String fileName, byte[] data){
		FileOutputStream fos;
		try{
			fos = openFileOutput(fileName, Context.MODE_APPEND);
			fos.write(data);
			fos.close();
		} catch (Exception e){
    		return false;
    	}
		return true;
	}
	
	/** Returns a FileInputStream to the file in the internal storage with the given name.
	 * @param fileName the name of the file
	 * @return a FileInputStream to the file
	 */
	public FileInputStream getInnerFileInputStream(String fileName){
		try{
			if(fileExists(fileName))
				return openFileInput(fileName);
			return null;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public boolean savePreference(String prefName, String valueName, String value){
	      SharedPreferences.Editor editor = getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
	      editor.putString(valueName, value);
	      return editor.commit();
	}
	
	public String getPreference(String prefName, String valueName){
	      return getSharedPreferences(prefName, Context.MODE_PRIVATE).getString(valueName, "");
	}
}
