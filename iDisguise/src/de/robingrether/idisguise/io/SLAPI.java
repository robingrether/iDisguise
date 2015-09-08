package de.robingrether.idisguise.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SLAPI {
	
	public static boolean save(Object obj, File file) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static Object load(File file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Object obj = ois.readObject();
			ois.close();
			return obj;
		} catch(Exception e) {
			return null;
		}
	}
	
}