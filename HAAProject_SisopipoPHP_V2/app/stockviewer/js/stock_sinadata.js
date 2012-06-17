
(function ($) {
	var sinajs = window.sinajs = {fetchSinaStockData:function (stock_data_cache, tryCount) {
		try {
			for (stockCode in stock_data_cache) {
				if (stockCode != "undefined") {
					var data = eval("hq_str_sh" + stockCode);
					var arr = data.split(",");
					var stockInfo = new StockInfo();
					stockInfo.code = stockCode;
					stockInfo.name = arr[0];
					stockInfo.PriceOpenToday = arr[1];
					stockInfo.PriceCloseYestoday = arr[2];
					stockInfo.CurrentPrice = arr[3];
					stockInfo.HighestPriceToday = arr[4];
					stockInfo.LowestPriceToday = arr[5];
					stockInfo.BuyPriceNow = arr[6];
					stockInfo.SellPriceNow = arr[7];
					stockInfo.TurnoverNum = arr[8];
					stockInfo.TurnoverMoney = arr[9];
					stockInfo.BuyNum1 = arr[10];
					stockInfo.BuyPrice1 = arr[11];
					stockInfo.BuyNum2 = arr[12];
					stockInfo.BuyPrice2 = arr[13];
					stockInfo.BuyNum3 = arr[14];
					stockInfo.BuyPrice3 = arr[15];
					stockInfo.BuyNum4 = arr[16];
					stockInfo.BuyPrice4 = arr[17];
					stockInfo.BuyNum5 = arr[18];
					stockInfo.BuyPrice5 = arr[19];
					stockInfo.SellNum1 = arr[20];
					stockInfo.SellPrice1 = arr[21];
					stockInfo.SellNum2 = arr[22];
					stockInfo.SellPrice2 = arr[23];
					stockInfo.SellNum3 = arr[24];
					stockInfo.SellPrice3 = arr[25];
					stockInfo.SellNum4 = arr[26];
					stockInfo.SellPrice4 = arr[27];
					stockInfo.SellNum5 = arr[28];
					stockInfo.SellPrice5 = arr[29];
					stockInfo.Date = arr[30];
					stockInfo.Time = arr[31];
					stockInfo.ImgKlineDaily = "http://image.sinajs.cn/newchart/daily/n/sh" + stockCode + ".gif";
					stockInfo.ImgKlineTime = "http://image.sinajs.cn/newchart/min/n/sh" + stockCode + ".gif";
					stockInfo.ImgKlineWeekly = "http://image.sinajs.cn/newchart/weekly/n/sh" + stockCode + ".gif";
					stockInfo.ImgKlineMonthly = "http://image.sinajs.cn/newchart/monthly/n/sh" + stockCode + ".gif";
					stock_data_cache[stockCode] = stockInfo;
				}
			}
		}
		catch (e) {
			console.msg("loading data ...");
			if (!tryCount) {
				tryCount = 0;
			}
			tryCount++;
			if (tryCount > 50) {
				console.msg("Failed to load stock data: [ERROR:" + e + "]");
				return;
			}
			setTimeout(function () {
				sinajs.fetchSinaStockData(stock_data_cache, tryCount);
			}, 100);
		}
	}, refreshSinaStockData:function (stock_data_cache) {
		console.msg("loading stock data... ");
		var script = $("#stock_load_script");
		if (script.length > 0) {
			script.remove();
		}
		script = document.createElement("script");
		var loadurl = "http://hq.sinajs.cn/list=";
		for (c in stock_data_cache) {
			if (c != "undefined") {
				loadurl += "sh" + c + ",";
			}
		}
		loadurl = loadurl.substring(0, loadurl.length - 1);
		script.src = loadurl;
		script.id = "stock_load_script";
		script.charset = "gb2312";
		document.body.appendChild(script);
		this.fetchSinaStockData(stock_data_cache);
		console.reset();
	}};
})(jQuery);

