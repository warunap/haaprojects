框架名称：
=============================
SimpleValidator

框架初衷：
=============================
Jquery已经存在一个validation框架，功能比较强大，但使用起来比较繁雜;
现在各browser已逐渐支持HTML5，HTML5本身包含部分验证功能，而不用另外代码去验证。
设计此框架的目的是基于jQuery建創建一個简单易用、主要依賴於屬性定義進行驗證、且充分利用HTML5验证功能的框架。

框架验证逻辑设计：
=============================
	1. 必输验证：
	-----------
		1) input[text,password], 添加required属性和class "required";
		2) input[checkbox], 添加required属性和class "required";
		3) input[radio], 添加required属性和class "required";
		4) select,添加class "required",对"空值"选项option添加class="nullvalue",如该option被选中则认为为空;
		
		說明：針對input标签，如果browser支持HTML5 required属性则不需框架验证。
	
	2. 长度验证：
	-----------
		1) input，声明minlength和maxlength属性
			说明：maxlength是browser自身支持属性，无需框架验证。
	
		2) input，声明minUTF8ByteLength(或minDBCSByteLength)和maxUTF8ByteLength(或maxDBCSByteLength)属性,声明了这两个属性则忽略minlength属性；
			说明：minUTF8ByteLength等属性是验证对应编码的byte字节数。
	
	3. 格式验证：
	-----------
		1) input,通过pattern属性声明数据格式的正则表达式;
			说明：
			>>如果browser支持HTML5 pattern属性，则不进行框架验证；
			>>如果有pattern属性则忽略datatype属性；
			
		2) input,通过type声明格式类型，如果浏览器支持此type類型(或為HTML5新增類型)，则忽略此框架验证；
			说明:如果一个type类型是browser不支持的或不认识的，则会默认当作text类型。
	
	4. 范围验证：
	-----------
		1) input，针对type="range"的Input，声明min和max属性(转为number进行比较)(如浏览器支持此类型则忽略框架验证) 
	
	5. 关系验证：
	-----------
		1) input,添加equalTo属性，配置需要验证值是否相等的目标selector表达式;
		2) input,添加greatThan属性，配置需要验证值是否大于的目标selector表达式(无需lessThan，只需greatThan即可确定相互关系);

框架功能设计：
=============================
	1. 输入框失去焦点(blur)或表单提交(submit)的时候触发验证；如果是长度相关验证属性则改变(change)的时候触发；
	2. 错误提示
	-------------
		1) 将输入框标记为红色边框，错误信息以红色字体直接显示于输入框后方；
		2) 当验证成功后，应去掉红色边框和后方红色字体提示；
	3. 提交表单时应该将整个表单区域遮罩住，不让用户再进行操作；
	

API 设计：
=============================
1. $(form).bindSimpleValidator(): 将form表单绑定验证框架
	1) 为所有输入框(class=“notvalid”除外)添加验证监测事件；
	2) 表单提交验证监测；

2. $(form).unbindSimpleValidator(): 将form表单取消绑定验证框架

3. $(input).bindSimpleValidator(): 针对某一输入框单独绑定验证；

4. $(input).unbindSimpleValidator(): 针对某一输入框单独取消绑定验证；

5. $(form).simpleValidate():验证表单内容是否合法，返回true/false;
	
6. $(input).simpleValidate():验证某一输入框内容是否合法，返回true/false;


技术实现：
=============================
1. jQuery: jQuery提供强大的dom操作功能，极大方便功能实现；
2. 使用modernizr判断浏览器对HTML5的支持情况；


参考：
=============================
1. http://modernizr.com/docs/
	Modernizr is a JavaScript library that detects HTML5 and CSS3 features in the user’s browser.
	Why use Modernizr?
	Taking advantage of cool new web technologies is great fun, 
	until you have to support browsers that lag behind. 
	Modernizr makes it easy for you to write conditional JavaScript and CSS to handle each situation, 
	whether a browser supports a feature or not. It’s perfect for doing progressive enhancement easily.

