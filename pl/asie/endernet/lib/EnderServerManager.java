package pl.asie.endernet.lib;

import java.io.File;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import pl.asie.endernet.EnderNet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class EnderServerManager {
	private HashMap<String, EnderServer> servers = new HashMap<String, EnderServer>();

	public void clear() {
		servers = new HashMap<String, EnderServer>();
	}
	
	public boolean can(EnderServer server, String permission) {
		if(server.permissions.size() == 0) return true; // no perms - all allowed
		if(server.permissions.contains("none")) return false; // none as a permission - nothing allowed
		return server.permissions.contains(permission);
	}
	
	public EnderServer get(String name) {
		return servers.get(name);
	}
	
	public String getAddress(String name) {
		EnderServer server = get(name);
		if(server != null) { System.out.println("Getting address of server -> " + server.address); return server.address; }
		else return null;
	}
	
	public String getPassword(String name) {
		EnderServer server = get(name);
		if(server != null && server.password != null) return server.password;
		else return "";
	}
	
	public void loadFromJSON(File file) {
		Gson gson = new Gson();
		String data = FileUtils.load(file);
		if(data == null) return; // No data
		
		Type arrayType = new TypeToken<ArrayList<EnderServer>>(){}.getType();
		try {
			ArrayList<EnderServer> serverList = gson.fromJson(data, arrayType);
			for(EnderServer es: serverList) {
				servers.put(es.name, es);
			}
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public void saveToJSON(File file) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		ArrayList<EnderServer> serverList = new ArrayList<EnderServer>();
		serverList.addAll(servers.values());
		String data = gson.toJson(serverList);
		FileUtils.save(data, file);
	}

	public void add(EnderServer es) {
		servers.put(es.name, es);
	}
	
	public EnderServer findByAddress(String remoteAddr) {
		try {
			InetAddress remote = InetAddress.getByName(remoteAddr);
			// Check if such address exists
			for(EnderServer es: servers.values()) {
				InetAddress server = InetAddress.getByName(es.address.split(":")[0]);
				if(server.equals(remote)) return es;
			}
			return null;
		} catch(Exception e) { e.printStackTrace(); return null; }
	}
	
	public boolean canReceive(String remoteAddr, String permission) {
		if(permission == null || permission.equals("") || permission.equals("none")) return true;
		if(remoteAddr.equals("127.0.0.1") || !EnderNet.onlyAllowDefined) return true;
		EnderServer es = findByAddress(remoteAddr);
		if(es != null) {
			return this.can(es, permission);
		} else return false;
	}
}
