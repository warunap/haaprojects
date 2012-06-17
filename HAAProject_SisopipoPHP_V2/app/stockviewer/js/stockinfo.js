
function StockInfo() {
	this.data = {};
}
StockInfo.prototype.get = function (name) {
	if (!this.data) {
		this.data = {};
	}
	return this.data[name];
};
StockInfo.prototype.set = function (name, val) {
	if (!this.data) {
		this.data = {};
	}
	this.data[name] = val;
};
StockInfo.prototype.data_cname_arr =  ["股票名字",
	"今日开盘价",		"昨日收盘价",		"当前价格",		"今日最高价",		"今日最低价",
	"竞买价，即“买一”报价",
	"竞卖价，即“卖一”报价",
	"成交的股票数",		"成交金额",
	"买一股数",		"买一股价",
	"买二股数",		"买二股价",
	"买三股数",		"买三股价",
	"买四股数",		"买四股价",
	"买五股数",		"买五股价",
	"卖一股数",		"卖一股价",
	"卖二股数",		"卖二股价",
	"卖三股数",		"卖三股价",
	"卖四股数",		"卖四股价",
	"卖五股数",		"卖五股价",
	"日期",		"时间"];	
StockInfo.prototype.data_name_arr =  ["stock_name",
	"open_price",		"yest_close_price",		"price",		"top_price",		"lowest_price",
	"buy_price",		"sale_price",
	"deal_num",		"deal_amt",
	"buy1_num",		"buy1_price",
	"buy2_num",		"buy2_price",
	"buy3_num",		"buy3_price",
	"buy4_num",		"buy4_price",
	"buy5_num",		"buy5_price",
	"sale1_num",		"sale1_price",
	"sale2_num",		"sale2_price",
	"sale3_num",		"sale3_price",
	"sale4_num",		"sale4_price",
	"sale5_num",		"sale5_price",
	"date",		"time",
	"kline_img_daily","kline_img_time","kline_img_weekly","kline_img_monthly"];	