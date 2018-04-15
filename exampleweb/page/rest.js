
/**
Standard boiler plate code to create a  connection
*/
function createRequest() {
    var result = null;
    if (window.XMLHttpRequest) {
        result = new XMLHttpRequest();
        if (typeof result.overrideMimeType !== 'undefined') {
            result.overrideMimeType('text/xml');
        }
    } else if (window.ActiveXObject) {
        result = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return result;
}

/**
Default behaviour for a server error.
*/
function responseError(message, code) {
    if (message === "") {
        console.log("Unknown message: Code:"+code);
	    alert("Unknown message: Code:"+code);
    } else {
	    console.log("Messages:" + message + ". Code:" + code);
	    alert("Code:" + code + ". Error:'" + message + "'");
    }
}

/**
Get some data from the server. This is normally JSON but could be any format.
If the response is 200 (success) then call the 'funcOK' function with the response.
If the response is not 200 then call the 'funcFail' function with the response and the error status.
A GET request does not send any body text so is simpler.  
*/
function serverGetData(funcOk, funcErr, url) {
    var oReq = createRequest();
    if (oReq != null) {
		/*
		Build the request with the URI provided
		*/
        request = window.location.protocol + "//" + window.location.host + "/" + url;
        console.log("GET:" + request);
        oReq.open("GET", request, true);
		/**
		Listen for the response. We are not waiting here!
		*/
        oReq.onreadystatechange = function () {
            if (oReq.readyState == 4 /* complete */) {
                if (oReq.status == 200) {
                    funcOk(oReq.responseText);
                } else {
                    funcErr(oReq.responseText, oReq.status);
                }
            }
        };
		/**
		Finally - Send the message.
		*/ 
        oReq.send();
    } else {
        window.console.log("AJAX (XMLHTTP) not supported.");
    }
}

/**
Send some data to the server. This is normally a JavaScript object converted to JSON.
If the response is between 200 and 299 (success) then call the 'funcOK' function with the response.
If the response is not between 200 and 299 then call the 'funcFail' function with the response and the error status.
A POST request has a message in the body text so is a little more complex.  
*/
function serverPostData(data, funcOk, funcErr, url) {
    var oReq = createRequest();
    if (oReq != null) {
		/*
		Build the request with the URI provided
		*/
        request = window.location.protocol + "//" + window.location.host + "/" + url;
        console.log("POST:" + request)
		/**
		Listen for the response. We are not waiting here!
		*/
        oReq.open("POST", request, true);
        oReq.onreadystatechange = function () {
            if (oReq.readyState == 4 /* complete */) {
                if ((oReq.status >= 200) && (oReq.status < 300)) {
                    funcOk(oReq.responseText);
                } else {
                    funcErr(oReq.responseText, oReq.status);
                }
            }
        };
		/**
		Finally - Send the message and the JavaScript object converted to JSON.
		*/ 
        oReq.send(JSON.stringify(data));
    } else {
        window.console.log("AJAX (XMLHTTP) not supported.");
    }
}
