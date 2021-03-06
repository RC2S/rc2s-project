var http 	= require('http');
var logger	= require('./LogUtils');
var config	= require('./config');

module.exports = function(errorsMapSerial, path, method, data, callback, contentType) {

	if(!config.che.authMethods.includes(method))
		return;

	var options = {
		host 	: config.che.host,
		port 	: config.che.port,
		path 	: config.che.mainPath + path,
		method 	: method,
		headers : {
			'Content-Type' : (contentType ? contentType : config.che.contentType)
		}
	}

	if(data != undefined)
		options.headers['Content-Length'] = Buffer.byteLength((contentType ? data : JSON.stringify(data)));

	var req = http.request(options, function(res) {
		var content = '';

		// Write basic log
		logger.writeHttpLog(errorsMapSerial, path, method, res.statusCode);

		res.setEncoding('utf8');

		res.on('data', function(chunk) {
			content += chunk;
		});
		
		res.on('end', function() {
			console.log('Request Path : ' + path);
			callback({
				statusCode 	: res.statusCode,
				headers 	: res.headers,
				content 	: (content ? JSON.parse(content) : null)
			});
		});
	});

	req.on('error', function(e) {
		// Write error log
		logger.writeHttpErrorLog(errorsMapSerial, e.message);
		callback({
			statusCode 	: 503,
			content 	: e.message
		});
	});

	if(data != undefined)
		req.write((contentType ? data : JSON.stringify(data)));

	req.end();
}