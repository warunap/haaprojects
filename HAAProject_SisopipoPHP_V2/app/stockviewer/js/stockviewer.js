
var reloadTimeInterval = 20;
var curr_stock_code = "all";
var kline_show_type = "Daily";
var stock_data_cache = {};
stock_data_cache["000001"] = new StockInfo();
stock_data_cache["600000"] = new StockInfo();
var loading_flag = false;
(function ($) {
	function addKlineBtn(btndiv, kline, name, imagelink) {
		return $("<input>").attr("type", "button").attr("value", name).addClass("func").appendTo(btndiv).click(function () {
			kline.attr("src", imagelink);
			kline_show_type = name;
		});
	}
	function addStockTableLine(table, stockInfo, data_name_arr) {
		var dataArr = [];
		for (var i = 0; i < data_name_arr.length; i++) {
			dataArr[i] = stockInfo.get(data_name_arr[i]);
		}
		table.addTableLine(dataArr);
	}
	function showStockInfo(stockCode) {
		var o = stock_data_cache[stockCode];
		var stock_container = $("#stock_container");
		stock_container.html("");
		var titlediv = $("<div>").appendTo(stock_container);
		$("<font>").css("font-size", "30px").css("color", "red").text(o.name + " " + o.code).appendTo(titlediv);
		var stockInfodiv = $("<div>").addClass("stock_data").appendTo(stock_container);
		var datadiv = $("<div>").addClass("datacontainer").appendTo(stockInfodiv);
		var priceTable = $("<table>").attr("border", "1").addClass("datatable").appendTo(datadiv);
		priceTable.addTableLine(["Price", o.CurrentPrice]);
		priceTable.addTableLine(["Top Price", o.HighestPriceToday]);
		priceTable.addTableLine(["Lowest Price", o.LowestPriceToday]);
		priceTable.addTableLine(["Open Price", o.PriceOpenToday]);
		priceTable.find("tr td:nth-col(1)").addClass("name");
		priceTable.find("tr td:nth-col(2)").addClass("num");
		var table = $("<table>").attr("border", "1").addClass("datatable").appendTo(datadiv);
		table.addTableLine(["Sale 5", o.SellPrice1, o.SellNum1]);
		table.addTableLine(["Sale 4", o.SellPrice2, o.SellNum2]);
		table.addTableLine(["Sale 3", o.SellPrice3, o.SellNum3]);
		table.addTableLine(["Sale 2", o.SellPrice4, o.SellNum4]);
		table.addTableLine(["Sale 1", o.SellPrice5, o.SellNum5]);
		table.addTableLine(["Buy 1", o.BuyPrice1, o.BuyNum1]);
		table.addTableLine(["Buy 2", o.BuyPrice2, o.BuyNum2]);
		table.addTableLine(["Buy 3", o.BuyPrice3, o.BuyNum3]);
		table.addTableLine(["Buy 4", o.BuyPrice4, o.BuyNum4]);
		table.addTableLine(["Buy 5", o.BuyPrice5, o.BuyNum5]);
		table.find("tr td:nth-col(1)").addClass("name");
		table.find("tr td:nth-col(3)").addClass("num");
		table.find("tr:lt(5) td:nth-col(2)").addClass("sale");
		table.find("tr:gt(4) td:nth-col(2)").addClass("buy");
		var btndiv = $("<div>").css("clear", "both").addClass("imgcontainer").appendTo(stockInfodiv);
		var kline = $("<img>").attr("id", "kline_show_" + o.code);
		addKlineBtn(btndiv, kline, "Time", o.ImgKlineTime);
		addKlineBtn(btndiv, kline, "Daily", o.ImgKlineDaily);
		addKlineBtn(btndiv, kline, "Weekly", o.ImgKlineWeekly);
		addKlineBtn(btndiv, kline, "Monthly", o.ImgKlineMonthly);
		btndiv.find("input[value=" + kline_show_type + "]").click();
		kline.appendTo(btndiv);
	}
	function refreshStockData() {
		sinajs.refreshSinaStockData(stock_data_cache);
	}
	function updateStockList() {
		var ul = $("#stock_list>ul");
		ul.html("");
		for (code in stock_data_cache) {
			if (code != "undefined") {
				var stockInfo = stock_data_cache[code];
				var tcode = code + "";
				$("<li>").attr("code", code).click(function () {
					showStockInfo($(this).attr("code"));
				}).appendTo(ul).addClass("func").text(stockInfo.code + stockInfo.name);
			} else {
			}
		}
	}
	function updateStockView() {
		var stock_container = $("#stock_container");
		stock_container.html("");
		if (curr_stock_code == "all") {
			stock_container.html("all");
		} else {
			showStockInfo(curr_stock_code);
		}
	}
	function resetReloadSecond() {
		var input = $("#reloadSecond");
		var sec = parseInt(input.val());
		if (isNaN(sec) || sec < 1) {
			console.msg("error number format");
			input.addClass("error");
		} else {
			input.removeClass("error");
			reloadTimeInterval = sec;
			console.msg("set reload interval time to " + reloadTimeInterval);
		}
	}
	function addStock() {
		var stockCode = $("input[name=stock_name]").val();
		if (stockCode == "") {
			alert("Please input the stock code!");
			return;
		}
		stock_data_cache[stockCode] = new StockInfo();
		curr_stock_code = stockCode;
		refreshStockData();
	}
	$(document).ready(function () {
		$("#reloadSecond").val(reloadTimeInterval);
		$("#reloadSecond").bind("onchange", function () {
			resetReloadSecond();
		});
		$("#btnview").click(function () {
			addStock();
		});
		refreshStockData();
		window.setInterval(function () {
			refreshStockData();
			updateStockList();
			updateStockView();
		}, reloadTimeInterval * 1000);
	});
})(jQuery);

