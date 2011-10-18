//================================================================
var cache;                              //cache for list content from server
var listItemsArray;                     // the array of the list displaying
var strLastValue = "";                  //the last value of input 
var lastValueBeforeUpDown = "";         //the value of input before doing up or down operation.
var searchInputBox;                     //the object of input
var currentValueSelected = -1;          //the index of the selected list item
var checkInterval=50;                   //interval time of check the input 
var luck;
var submitButton;
//================================================================
var undeStart = "<div class=\"divMatchText\">";
var undeEnd = "</div>";
var selectDivStart = "<div style=\"width:100%;display:block;\" class=\"divNomalElement\"  ";
var selectDivEnd ="</div>";

/*
initial function
*/
var downListInitial = function(){
	cache = new Array();//initial cache
	submitButton = document.getElementById("submit");
	submitButton.onclick=submitFun;
	searchInputBox=document.getElementById("s");
	searchInputBox.autocomplete="off";
	searchInputBox.focussed=false;
	searchInputBox.onfocus = function(){this["focussed"] = true;}; 
	searchInputBox.onblur = function(){this["focussed"] = false;};
	searchInputBox.focus();
	var downlist = document.createElement("div");
	downlist.id = "listBox";
	downlist.className = "divDownListBox";	
	downlist.onmouseover = function(){this["isMouseOver"] = true;}; 
	downlist.onmouseout = function(){this["isMouseOver"] = false;};
	//downlist.onkeypress = chooseList;
	document.body.appendChild(downlist);
	searchInputBox.onkeyup = chooseList;
	setDownListPostion();
	window.setTimeout("checkTextChange()",checkInterval);
	hideDownList();
	luck = document.getElementById("luck");
	luck.onclick = iamLuckToGetBest;
	//document.onblur = function(){hideDownList();};
	window.onresize = function(){setDownListPostion();};
};
var iamLuckToGetBest = function(){
	if(searchInputBox.value!==null){
		var url = "k?s=" + encodeURIComponentNew(searchInputBox.value);
		var request= new lelenole_xmlhttprequest;
		var callback= function(req){
			var s = req.responseText;
			if(s!==null && s!==""){
				document.location=s;
			}
		};
		request.open(url,"get",callback);
	}
};
/*
function that to catch the operation of up,down,and enter.
*/
var chooseList = function(evt){
	var intKey = getKey(evt);
    if(intKey == 13){
		if(currentValueSelected==-1){
			doSubmit();
			return ;
		}
		choosedListItem();
		return false;
    }else if(intKey == 38){
		moveUpOrDown(-1);
		return false;
	}else if(intKey == 40){
		moveUpOrDown(1);
		return false;
	}
};
/*
function that to get the operation key value
*/
var getKey =function(evt){
	evt = (evt) ? evt : ((window.event) ? window.event : "");
	return evt.keyCode ? evt.keyCode : (evt.which ? evt.which :evt.charCode);
};
/*
function that to check the input change loopingly with a interval
*/
function checkTextChange(evt){
	
	if(!searchInputBox.focussed || searchInputBox.value.length === 0 ){
 		hideDownList();
		strLastValue = "";
		window.setTimeout("checkTextChange()",checkInterval);
		return false;
     }
	if(searchInputBox.value==strLastValue || lastValueBeforeUpDown!==""){
		window.setTimeout("checkTextChange()",checkInterval);
		return false;
	}
	getMatchingList(searchInputBox.value);
	strLastValue = searchInputBox.value;
	window.setTimeout("checkTextChange()",checkInterval);
}

/*
function that to get a matching list of the search string. 
*/
function getMatchingList(searchStr){
	var listContentString = cache[searchStr]; 
	if(listContentString==null){//if can't get from the cache then get it from server
  		var url = "l?s=" + encodeURIComponentNew(searchStr); //encode the search string to utf-8	
		//alert(url);
		var request= new lelenole_xmlhttprequest;
		var callback= function(req){
			listContentString = req.responseText;
			cache[searchStr]=listContentString;
			parseMessage(listContentString);
		};
		request.open(url,"get",callback);
		
	}else{
		parseMessage(listContentString);
	}
}

/*
function that to parse the list message to build the list
*/
function parseMessage(content) {
	if(content==null || content ==""){
		hideDownList();
		return;
	}
	var entry = content.split(",");
	listItemsArray = new Array();
	for(var i=0;i<entry.length;i++){
		listItemsArray[i] = entry[i].split(" ");
	}
	buildDownList(searchInputBox.value);
}
/*
function to build the list
*/
function buildDownList(theText){
	showDownList();
	var inner = "";
	var theMatches = getMatchingListArray(theText);
	for(var i=0;i<theMatches.length;i++){
		inner +=theMatches[i];
	}
	if(theMatches.length>0){
		document.getElementById("listBox").innerHTML = inner;
	}
	else{
		hideDownList();
	}
}
/*
function to get the html items of the list
*/
function getMatchingListArray(text){
	var matchArray = new Array();
	for(var i=0;i<listItemsArray.length;i++){
		if(listItemsArray[i][0].length > text.length){
			matchArray[matchArray.length]=createAListItem(listItemsArray[i],text,i);
		}
	}
	return matchArray;
}
/*
function to create a html item of the list
*/  
function createAListItem(item,str,i){
	selectDivMid = "onmouseover=\"javascript:SetHighColor("+ i +");\"onclick=\"javascript:getListItem("+ i +");\"  id=\"OptionsList_" + i + "\" >";
	var num = "<div class=\"number\">"+item[1]+" results</div>";
	return selectDivStart + selectDivMid + num + item[0] + selectDivEnd;
}
/*
function to high list the selected item of the list
*/
function SetHighColor(id){	
	currentValueSelected = id;
 	for(var i = 0; i < listItemsArray.length; i++){
		document.getElementById("OptionsList_" + i).className ="divNomalElement";
    }
	var listItem = document.getElementById("OptionsList_" +currentValueSelected);
	if(listItem){
		listItem.className = "divHighElement";
	}
}

/*
function to set the input with the value of the selected item
*/
function SetText(id){
	searchInputBox.value = listItemsArray[id][0]; //set text value
	strLastValue = searchInputBox.value;
	hideDownList();
}
/*
function to set the input value to the selected item's and submit the form 
*/
var getListItem = function(id){
		currentValueSelected = id;
		SetText(currentValueSelected);
		hideDownList();
		doSubmit();
};
/*
function to set the input value to the selected item's and hide the down list
*/
function choosedListItem(){
	if(currentValueSelected >= 0){
		SetText(currentValueSelected);
		hideDownList();
	}
}
/*
function to hide the down list
*/
var hideDownList = function(){
	var listbox = document.getElementById("listBox");
	if(!listbox.isMouseOver){
		listbox.style.display = "none";
		clearStatus();
	}
}
/*
function to show the down list
*/
var showDownList = function(){
	document.getElementById("listBox").style.display = "block";
}
/*
function to check whether the down list is visiable
*/
var isDownListVisiable = function(){
	return document.getElementById("listBox").style.display == "block";
}
/*
function to clear the operation status
*/
var clearStatus = function(){
	currentValueSelected = -1;
	lastValueBeforeUpDown="";
};
var doSubmit = function(){
	submitButton.click();
};
/*
function that add to submit button
*/
var submitFun = function(){
	if(currentValueSelected!=-1){
		return false;
	}
};
/*
function to move up or down
*/
function moveUpOrDown(id){
	if(currentValueSelected >= -1 && isDownListVisiable() ){
		if(lastValueBeforeUpDown==""){
			lastValueBeforeUpDown=searchInputBox.value;
		}
		newValue = parseInt(currentValueSelected) + parseInt(id);
		newValue = newValue>=listItemsArray.length?-1:newValue<-1?-1:newValue;
		currentValueSelected = newValue;
		if(currentValueSelected!=-1){
			searchInputBox.value = listItemsArray[currentValueSelected][0]; //set text value
		}else{
			searchInputBox.value = lastValueBeforeUpDown;
			lastValueBeforeUpDown="";
		}
		SetHighColor(currentValueSelected);
		
	}
}
/*
function to set the position of the down list
*/   
function setDownListPostion(){
	var selectedPosX = 0;
	var selectedPosY = 0;
	var theElement = document.getElementById("s");
 	if (!theElement) return;
	var theElemHeight = theElement.offsetHeight;
	var theElemWidth = theElement.offsetWidth;
	while(theElement != null){
		selectedPosX += theElement.offsetLeft;
		selectedPosY += theElement.offsetTop;
		theElement = theElement.offsetParent;
	}
	xPosElement = document.getElementById("listBox");
	xPosElement.style.left=selectedPosX+"px";
	xPosElement.style.width=theElemWidth+"px";
	xPosElement.style.top= (selectedPosY + theElemHeight)+"px";
	xPosElement.style.display = "none";
	xPosElement.style.position = "absolute";
}


