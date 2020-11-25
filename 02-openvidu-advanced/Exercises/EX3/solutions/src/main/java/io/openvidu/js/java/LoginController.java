package io.openvidu.js.java;
/**
 * BASED ON openvidu-tutorials/openvidu-js-java
 *
 */

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.openvidu.java.client.OpenViduRole;

@RestController
@RequestMapping("/myapp")
public class LoginController {

	public class MyUser {

		String username;
		String email;
		String pass;
		OpenViduRole role;

		public MyUser(String username, String pass, String email, OpenViduRole role) {
			this.username = username;
			this.pass = pass;
			this.role = role;
		}
	}

	public static Map<String, MyUser> users = new ConcurrentHashMap<>();
    /* here usually you may have at least a DB to manage users properly  and being an Spring-boot app you may have
    security managed with it*/

    /*EX3: declare all the users in the exercise that can at least publish and subscribe video.
    *      the users should have the appropriate Openvidu.role/s */

	public LoginController() {
		users.put("user1", new MyUser("user1", "pass", "user1@naevatec.com", OpenViduRole.PUBLISHER));
		users.put("user2", new MyUser("user2", "pass", "user2@naevatec.com", OpenViduRole.PUBLISHER));
		users.put("user3", new MyUser("user3", "pass", "user3@naevatec.com", OpenViduRole.PUBLISHER));
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Object> login(@RequestBody String userPass, HttpSession httpSession) throws ParseException {

		System.out.println("Logging in | {user, pass}=" + userPass);
		// Retrieve params from POST body
		JSONObject userPassJson = (JSONObject) new JSONParser().parse(userPass);
		String user = (String) userPassJson.get("user"); //may be nickname or email
		String pass = (String) userPassJson.get("pass");

		if (login(user, pass)) { // Correct user-pass
			// Validate session and return OK 
			// Value stored in HttpSession allows us to identify the user in future requests
			httpSession.setAttribute("loggedUser", user);
			return new ResponseEntity<>(HttpStatus.OK);
		} else { // Wrong user-pass
			// Invalidate session and return error
			httpSession.invalidate();
			return new ResponseEntity<>("User/Pass incorrect", HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseEntity<Object> logout(HttpSession session) {
		System.out.println("'" + session.getAttribute("loggedUser") + "' has logged out");
		session.invalidate();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/* This is a basic check not use in prod here you may have a proper login check*/
	private boolean login(String user, String pass) {
		boolean logged = false;
		if (users.containsKey(user)){
			logged =users.get(user).pass.equals(pass);
		}
		if (!logged){
			Collection<MyUser> tmpusers = users.values();
			for (MyUser u: tmpusers) {
				logged = logged || u.email.equals(user) && u.pass.equals(pass);
			}
		}
		return logged;
	}

}
