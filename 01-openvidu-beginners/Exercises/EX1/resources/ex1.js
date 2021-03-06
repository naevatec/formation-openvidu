// HERE you have a skeleton for your js app (you can use this or not, it's up to you)

/* ---------------- DECLARE VARS -------------- */


/* -------------------------------------------- */


window.onload = function(){
   joinSession();
}

window.onbeforeunload = function(){
  leaveSession();
}


function joinSession() {

/* -----  INITIALISE SESSION (Events not necessary in ex1) --- */



/* ----------------------------------------------------------- */



/* ------------  GET TOKEN (Client) -------------------------- */
	      then(sessionId => createToken(sessionId)).
                   then(token => {
/* ----------------------------------------------------------- */


/* ------------- CONNECT SESSION ----------------------------- */
		session.connect(token)
			.then(() => {
				var publisher = OV.initPublisher("publisher");
				session.publish(publisher);
			})
			.catch(error => {
				console.log("There was an error connecting to the session:", error.code, error.message);
			});
	});
/* ----------------------------------------------------------- */

}


function leaveSession(){
/* ---------- LEAVE SESSION -----------------*/


/* ------------------------------------------*/
}


 /*
  * --------------------------
  * SERVER-SIDE RESPONSIBILITY
  * --------------------------
  */

var OPENVIDU_SERVER_URL = /* YOUR CONF : "https://" + openvidu + ":4443" */ "";
var OPENVIDU_SERVER_SECRET = /* YOUR SECRET: "" */ "";


function createSession(sessionId) { // See https://openvidu.io/docs/reference-docs/REST-API/#post-apisessions
	return new Promise((resolve, reject) => {
		$.ajax({
			type: //EX1:  which is the Method type? look for it in the reference
			url: //EX1:  which is the API? look for it in the reference
			data: JSON.stringify(/* EX1: parameters for the Request */),
			headers: {
				"Authorization": "Basic " + btoa("OPENVIDUAPP:" + OPENVIDU_SERVER_SECRET),
				"Content-Type": "application/json"
			},
			success: response => resolve(response.id),
			error: (error) => {
				if (error.status === 409) {
					resolve(sessionId);
				} else {
					console.warn('No connection to OpenVidu Server. This may be a certificate error at ' + OPENVIDU_SERVER_URL);
					if (window.confirm('No connection to OpenVidu Server. This may be a certificate error at \"' + OPENVIDU_SERVER_URL + '\"\n\nClick OK to navigate and accept it. ' +
						'If no certificate warning is shown, then check that your OpenVidu Server is up and running at "' + OPENVIDU_SERVER_URL + '"')) {
						location.assign(OPENVIDU_SERVER_URL + '/accept-certificate');
					}
				}
			}
		});
	});
}

function createToken(sessionId) { // See https://openvidu.io/docs/reference-docs/REST-API/#post-apitokens
	return new Promise((resolve, reject) => {
		$.ajax({
                        type: //EX1:  which is the Method type? look for it in the reference
                        url: //EX1:  which is the API? look for it in the reference
                        data: JSON.stringify(/* EX1: parameters for the Request */),
			headers: {
				"Authorization": "Basic " + btoa("OPENVIDUAPP:" + OPENVIDU_SERVER_SECRET),
				"Content-Type": "application/json"
			},
			success: response => resolve(response.token),
			error: error => reject(error)
		});
	});
}
