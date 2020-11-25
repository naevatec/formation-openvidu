package io.openvidu.js.java;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.TokenOptions;

@RestController
@RequestMapping("/api-ov")
public class SessionController {

	// OpenVidu object as entrypoint of the SDK
	private OpenVidu openVidu;

	// Collection to pair session names and OpenVidu Session objects
	private Map<String, Session> mapSessions = new ConcurrentHashMap<>();
	// Collection to pair session names and tokens (the inner Map pairs tokens and
	// role associated)
	private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

	// URL where our OpenVidu server is listening
	private String OPENVIDU_URL="https://demo.naevatec.com:4443/";
	// Secret shared with our OpenVidu server
	private String SECRET ="N43V40v";

	// Fixed session name
	private String sessionName = "your_user_naevatec";


	public SessionController() {
		this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
	}

	@RequestMapping(value = "/get-token", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> getToken(HttpSession httpSession)
			throws ParseException {
		System.out.println("Getting a token from OpenVidu Server | {sessionName}=" + sessionName);

		/* CHECK LOGIN */
		try {
			checkUserLogged(httpSession);
		} catch (Exception e) {
			return getErrorResponse(e);
		}
		/* ************ */

		/* EX3: which are the roles of the logged user? */
		OpenViduRole role = LoginController.users.get(httpSession.getAttribute("loggedUser")).role;

		 /*// Optional data to be passed to other users when this user connects to the
		// video-call. In this case, a JSON with the value we stored in the HttpSession
		// object on login
		String serverData = "{\"serverData\": \"" + httpSession.getAttribute("loggedUser") + "\"}";
        */

		 /* EX3:  retrieve the token from OV. You may do this after a whole and secure login*/
		 // Build tokenOptions object with the serverData and the role
		TokenOptions tokenOptions = new TokenOptions.Builder().role(role).build();

		JSONObject responseJson = new JSONObject();

		/* EX3: does the session already exist? */
		Session session = null;
		List<Session> activeSessions = this.openVidu.getActiveSessions();

		for(Session s: activeSessions){
			if (s.getSessionId().equals(sessionName))
				session = s;
		}

		//EX3: if session doesn't exist create new
		if (session == null){
			System.out.println("New session " + sessionName);
			try {
				session = this.openVidu.createSession();
			} catch (OpenViduJavaClientException e) {
				return getErrorResponse(e);
			} catch (OpenViduHttpException e) {
				return getErrorResponse(e);
			}
		}

		//EX3: connect to the session
		try {
			String token = session.generateToken(tokenOptions);

			// Prepare the response with the token
			responseJson.put(0, token);

			// Return the response to the client
			return new ResponseEntity<>(responseJson, HttpStatus.OK);

		} catch (OpenViduJavaClientException e) {
			return getErrorResponse(e);
		} catch (OpenViduHttpException e) {
			return getErrorResponse(e);
		}

	}

	@RequestMapping(value = "/remove-user", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> removeUser(@RequestBody String sessionNameToken, HttpSession httpSession)
			throws Exception {

		try {
			checkUserLogged(httpSession);
		} catch (Exception e) {
			return getErrorResponse(e);
		}
		System.out.println("Removing user | {sessionName, token}=" + sessionNameToken);

		// Retrieve the params from BODY
		JSONObject sessionNameTokenJSON = (JSONObject) new JSONParser().parse(sessionNameToken);
		String sessionName = (String) sessionNameTokenJSON.get("sessionName");
		String token = (String) sessionNameTokenJSON.get("token");

		// If the session exists
		if (this.mapSessions.get(sessionName) != null && this.mapSessionNamesTokens.get(sessionName) != null) {

			// If the token exists
			if (this.mapSessionNamesTokens.get(sessionName).remove(token) != null) {
				// User left the session
				if (this.mapSessionNamesTokens.get(sessionName).isEmpty()) {
					// Last user left: session must be removed
					this.mapSessions.remove(sessionName);
				}
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				// The TOKEN wasn't valid
				System.out.println("Problems in the app server: the TOKEN wasn't valid");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else {
			// The SESSION does not exist
			System.out.println("Problems in the app server: the SESSION does not exist");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<JSONObject> getErrorResponse(Exception e) {
		JSONObject json = new JSONObject();
		json.put("cause", e.getCause());
		json.put("error", e.getMessage());
		json.put("exception", e.getClass());
		return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void checkUserLogged(HttpSession httpSession) throws Exception {
		if (httpSession == null || httpSession.getAttribute("loggedUser") == null) {
			throw new Exception("User not logged");
		}
	}

}
