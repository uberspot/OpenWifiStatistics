package Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Environment;

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
	
	/** Returns a FileOutputStream to the file in the internal storage with the given name.
	 * @param fileName the name of the file
	 * @return a FileOutputStream to the file
	 */
	public FileOutputStream getInnerFileOutputStream(String fileName){
		try{
			if(fileExists(fileName))
				return openFileOutput(fileName, Context.MODE_PRIVATE);
			return null;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public boolean saveObjectToInternalStorage(Object obj,  String fileName) {
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream( new BufferedOutputStream( openFileOutput(fileName, Context.MODE_PRIVATE) ) );
			output.writeObject(obj);
			output.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if(output!=null)
				try { output.close(); } catch (IOException e) { }
		}
		return false;
	}
	
	public Object loadObjectFromInternalStorage(String fileName) {
		Object obj = null; ObjectInputStream input = null;
		try {
			input = new ObjectInputStream ( new BufferedInputStream( openFileInput(fileName) ) );
			obj = input.readObject();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if(input!=null)
				try { input.close(); } catch (IOException e) { }
		}
		return obj;
	}
	
	public boolean savePreference(String prefName, String valueName, String value){
	      SharedPreferences.Editor editor = getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
	      editor.putString(valueName, value);
	      return editor.commit();
	}
	
	public String getPreference(String prefName, String valueName){
	      return getSharedPreferences(prefName, Context.MODE_PRIVATE).getString(valueName, "");
	}

	/** Save the given object to a file in external storage
	 * @param obj the object to save
	 * @param directory the directory in the sd card to save it into
	 * @param fileName the name of the file
	 * @param overwrite if set to true the file will be overwritter if it already exists
	 * @return true if the file was written succesfully, false otherwise
	 */
	public boolean saveObjectToExternalStorage(Object obj, String directory, String fileName, boolean overwrite) {
		if(!directory.startsWith(File.separator))
			directory = File.separator + directory;

		File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + directory);
		if(!dir.exists()) dir.mkdirs();
		
		File file = new File(dir, fileName);
		if(file.exists() && !overwrite)
			return false;
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream(file) ) );
			output.writeObject(obj);
			output.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if(output!=null)
				try { output.close(); } catch (IOException e) { }
		}
		return false;
	}
	
	public Object loadObjectFromExternalStorage(String fileName) {
		if(!fileName.startsWith(File.separator))
			fileName = File.separator + fileName;
		
		File file = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + fileName);
		Object obj = null; ObjectInputStream input = null;
		try {
			input = new ObjectInputStream ( new BufferedInputStream( new FileInputStream(file) ) );
			obj = input.readObject();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if(input!=null)
				try { input.close(); } catch (IOException e) { }
		}
		return obj;
	}
	
	public static boolean hasExternalStorage(boolean requireWriteAccess) {
	    String state = Environment.getExternalStorageState();

	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/** Save the given string to a file in external storage
	 * @param obj the object to save
	 * @param directory the directory in the sd card to save it into
	 * @param fileName the name of the file
	 * @param overwrite if set to true the file will be overwritter if it already exists
	 * @return true if the file was written succesfully, false otherwise
	 */
	public boolean saveStringToExternalStorage(String obj, String directory, String fileName, boolean overwrite) {
		if(!directory.startsWith(File.separator))
			directory = File.separator + directory;

		File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + directory);
		if(!dir.exists()) dir.mkdirs();
		
		File file = new File(dir, fileName);
		if(file.exists() && !overwrite)
			return false;
		BufferedOutputStream output = null;
		try {
			output = new BufferedOutputStream( new FileOutputStream(file) );
			output.write(obj.getBytes());
			output.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if(output!=null)
				try { output.close(); } catch (IOException e) { }
		}
		return false;
	}
}
