package pl.asie.endernet.http;

import java.util.Map;

import pl.asie.endernet.lib.EnderID;

import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

public class URIHandlerCanReceive implements IURIHandler {

	public boolean actuallyServe(IHTTPSession session) {
		Map<String, String> params = session.getParms();
		if(!params.containsKey("object")) return false;
		Gson gson = new Gson();
		EnderID block = gson.fromJson(params.get("object"), EnderID.class);
		return block.isReceiveable();
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		if(actuallyServe(session)) return new Response(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "EEYUP");
		else return new Response(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "NNOPE");
	}
}