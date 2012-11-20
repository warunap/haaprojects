
Jquery已经存在一个validation框架，功能比较强大，但使用起来还是比较复杂，将基于jQuery创建一个简单易用的validation框架。

框架功能设计：
=============================
1. 必输验证：
	1) input[text,password], 添加required属性和class "required";
	2) input[checkbox], 添加required属性和class "required";
	3) input[radio], 添加required属性和class "required";
	4) select,添加class "required",对"空值"选项option添加class="nullvalue",如该option被选中则认为为空;

2. 长度验证：
	1) input，声明minlength和maxlength属性
	2) input，声明minUTF8length和maxUTF8length属性,声明了这两个属性则忽略minlength和maxlength属性；

3. 格式验证：
	1) input,通过datatype声明格式类型
	2) input,通过pattern属性声明数据格式的正则表达式，如果有pattern属性则忽略datatype属性；

4. 范围验证：
	1) input，针对datatype="range"的Input，声明min和max属性(转为number进行比较)

5. 关系验证：
	1) input,添加equalTo属性，配置需要验证值是否相等的目标selector表达式;
	2) input,添加greatThan属性，配置需要验证值是否大于的目标selector表达式(无需lessThan，只需greatThan即可确定相互关系);


使用说明：
=============================
1. HTML5功能属性：如果浏览器支持HTML5标签属性，则忽略此框架验证；
		HTML5属性包括：
			type="range","email","color",""
			minlength=""
			

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

