package de.robingrether.idisguise.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.OutdatedServerException;

public class SLAPI {
	
	public static boolean saveMap(Map<?, Disguise> map, File file) {
		try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
			for(Entry<?, Disguise> entry : map.entrySet()) {
				output.write(String.format("%s -> %s", entry.getKey().toString(), entry.getValue().toString()));
				output.newLine();
			}
			output.flush();
			output.close();
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static Map<?, Disguise> loadMap(File file) {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			String line = input.readLine();
			if(line.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}.+")) {
				Map<UUID, Disguise> map = new ConcurrentHashMap<UUID, Disguise>();
				do {
					String[] parts = line.split(" -> ");
					try {
						map.put(UUID.fromString(parts[0]), Disguise.fromString(parts[1]));
					} catch(IllegalArgumentException|OutdatedServerException e) {
					}
				} while((line = input.readLine()) != null);
				input.close();
				return map;
			} else {
				Map<String, Disguise> map = new ConcurrentHashMap<String, Disguise>();
				do {
					String[] parts = line.split(" -> ");
					try {
						map.put(parts[0], Disguise.fromString(parts[1]));
					} catch(IllegalArgumentException|OutdatedServerException e) {
					}
				} while((line = input.readLine()) != null);
				input.close();
				return map;
			}
		} catch(Exception e) {
			return null;
		}
	}
	
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