/**
 * Object that piggy-back the browser console and show their messages on a DIV
 * 
 * Inspired by Node.js ClIM module (https://github.com/epeli/node-clim)
 * Based on previous version of console.js
 * 
 * @constructor
 * 
 * @param {String}
 *            id: id attribute of the DIV tag where to show the messages
 * @param console:
 *            reference to the original browser console
 */

function Console (id, console){
  var console_div = document.getElementById(id);
  
  function createMessage(msg, clazz){
    //sanitize the input
    msg = msg.toString().replace(/</g, '&lt;');
      var span = document.createElement('SPAN');
      if (clazz != undefined) {
        span.classList.add(clazz);
      }
      span.appendChild(document.createTextNode(msg));
      return span;
  };
  
  this._append = function (element) {
    console_div.appendChild(element);
    console_div.appendChild(document.createElement('BR'));
  };

/*Event logs*/
 
  this.logEvent = function (event){
    var msg = "[" + event.type + "]";

    if (event.value != undefined)
      msg = msg + "(" + event.value + ")";

    if (event.reason != undefined)
      msg = msg + " Reason: " + event.reason;
    
    this._append(createMessage(event.type , event.type));
  };

  this.logSessionEvent = function (event){
    var msg = "[" + event.type + "]";

    if (event.value != undefined)
      msg = msg + "(" + event.value + ")";

    if (event.reason != undefined)
      msg = msg + " Reason: " + event.reason;

    this._append(createMessage(msg , "session"));
  };

  this.logPublisherEvent = function (event){
    var msg = "[" + event.type + "]";

    if (event.value != undefined)
      msg = msg + "(" + event.value + ")";

    if (event.reason != undefined)
      msg = msg + " Reason: " + event.reason;

    this._append(createMessage(msg , "publisher"));
  };
 
  this.logSubscriberEvent = function (event){
    var msg = "[" + event.type + "]";

    if (event.value != undefined)
      msg = msg + "(" + event.value + ")";

    if (event.reason != undefined)
      msg = msg + " Reason: " + event.reason;

    this._append(createMessage(msg , "subscriber"));
  };


/*Standard logs*/

/**
	 * Show an Error message both on browser console and on defined DIV
	 * 
	 * @param msg:
	 *            message or object to be shown
	 */
	this.error = function(msg) {
		console.error(msg);
		this._append(createMessage(msg, "error-inline"));
		div.scrollTop = div.scrollHeight;
	};

	/**
	 * Show an Warn message both on browser console and on defined DIV
	 * 
	 * @param msg:
	 *            message or object to be shown
	 */
	this.warn = function(msg) {
		console.warn(msg);
		this._append(createMessage(msg, "warning-inline"));
		div.scrollTop = div.scrollHeight;
	};

	/**
	 * Show an info highlighted message both on browser console and on defined DIV
	 * 
	 * @param msg:
	 *            message or object to be shown
	 */
	this.filterevent = function(msg){
		console.info(msg);
		this._append(createMessage(msg, "event-inline" ));
		div.scrollTop = div.scrollHeight;
	}
	/**
	 * Show an Info message both on browser console and on defined DIV
	 * 
	 * @param msg:
	 *            message or object to be shown
	 */
	this.info = this.log = function(msg) {
		console.info(msg);
		this._append(createMessage(msg));
	};

	/**
	 * Show an Debug message both on browser console and on defined DIV
	 * 
	 * @param msg:
	 *            message or object to be shown
	 */
	this.debug = function(msg) {
		console.log(msg);
		// this._append(createMessage(msg, "#0000FF"));
	};
}

