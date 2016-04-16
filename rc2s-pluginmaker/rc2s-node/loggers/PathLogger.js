var fs = require("fs");

var writeData = function(datas, log) {
	datas += JSON.stringify(log) + "\n";

	fs.writeFile("logs.json", datas, (err) => {
		if (err)
			throw err;

		console.log("[+] Wrote log !");
	});
}

module.exports = function(app) {

	var calls = 0;

	// Proper Logger
	app.use(function(req, res, next) {
		console.log("[~] Logger working...");

		var log = {
			path : req.url,
			//header : req.headers,
			body : req.body,
			query : req.query,
			date : Date.now()
		}

		try {
			var stat = fs.statSync("logs.json");

			fs.readFile("logs.json", (err, data) => {
				if (err)
					throw err;

				writeData(data.toString(), log);
			});
		} catch (err) {
			console.log("[!] File not found...");

			writeData("", log);
		}

		console.log("[+] Logger ended working !");
		next();
	});
}