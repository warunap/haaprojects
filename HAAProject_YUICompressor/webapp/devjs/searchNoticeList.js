//================================================================
var arrOptions = new Array();
var strLastValue = "";
var searchInputBox;
var currentValueSelected = -1;
//================================================================
var countForId = 0; 
//================================================================
var undeStart = "<span class=\"spanMatchText\">";
var undeEnd = "</span>";
var selectSpanStart = "<span style=\"width:100%;display:block;\" class=\"spanNormalElement\" onmouseover=\"SetHighColor(this)\" ";
var selectSpanEnd ="</span>";
 
//=================================================================================================
window.onload = function(){
	document.getElementById("submit").onclick=submitFun;
    searchInputBox=document.getElementById("s");
    searchInputBox.autocomplete="off";
    var elemSpan = document.createElement("span");
    elemSpan.id = "listBox";
    elemSpan.className = "spanTextDropdown";
    document.body.appendChild(elemSpan);
    //searchInputBox.onkeypress = GiveOptions;
    //searchInputBox.onkeydown = GiveOptions;
    searchInputBox.onkeyup = GiveOptions;
    SetElementPosition();

}
//=============================================================================================
function GiveOptions(evt){
	var intKey = -1;
	evt = (evt) ? evt : ((window.event) ? window.event : "");
	intKey = evt.keyCode ? evt.keyCode : (evt.which ? evt.which :evt.charCode);
	if(searchInputBox.value.length == 0){
            HideTheBox();
            strLastValue = "";
            return false;
        }
        if(intKey == 13){
		if(document.getElementById("listBox").style.display=="none" && currentValueSelected==-1){
			document.getElementById("searchForm").submit();
			return;
		}
		GrabHighlighted();
		return false;
        }else if(intKey == 38){
		MoveHighlight(-1);
		return false;
        }else if(intKey == 40){
		MoveHighlight(1);
		return false;
	}

        if(searchInputBox.value.indexOf(strLastValue)!=0
		||arrOptions.length==0
		||(strLastValue.length==0&&searchInputBox.value.length>0)
		||(searchInputBox.value.length<= strLastValue.length)){
		strLastValue = searchInputBox.value;
		TypeAhead(searchInputBox.value);
        } 
	else
        {        
 		BuildList(searchInputBox.value);
		strLastValue = searchInputBox.value;
        } 
}
//===================================================================

var submitFun = function(){
	if(currentValueSelected!=-1){
		return false;
	}
}
//=======================================================================
function TypeAhead(xStrText){
  	var url = "l?s=" + xStrText;
	var request= new lelenole_xmlhttprequest;
	request.open(url,"get",parseMessage);
}
//===========================================================
function parseMessage(req) {
	var entry = req.responseText.split(",");
	arrOptions = new Array();
	for(var i=0;i<entry.length;i++){
		var e = entry[i].split(" ");
		var key= e[0];
		var value=e[1];
		arrOptions[i]=e;
	}
	BuildList(searchInputBox.value);
	strLastValue = searchInputBox.value;
}
//=============================================================================
function BuildList(theText){
	document.getElementById("listBox").style.display="block";
	var inner = "";
	var theMatches = MakeMatches(theText);
	for(var i=0;i<theMatches.length;i++){
		inner +=theMatches[i];
	}
	if(theMatches.length>0){
		document.getElementById("listBox").innerHTML = inner;
	}
	else{
		HideTheBox();
	}
}
//================================================================================    
function SetElementPosition(){
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

//================================================================
function MakeMatches(xCompareStr){
	countForId = 0;
	var matchArray = new Array();
	for(var i=0;i<arrOptions.length;i++){
		var regExp = new RegExp(xCompareStr,"ig");
		if((arrOptions[i][0].search(regExp))>=0){
			matchArray[matchArray.length]=CreateUnderline(arrOptions[i],xCompareStr,i);
		}
		else{
			continue;
		}
	}
	return matchArray;
}

//================================================================   
function CreateUnderline(xStr,xTextMatch,xVal){
	selectSpanMid = "onclick='SetText(" + xVal + ")'" +   " id='OptionsList_" +    countForId + "' theArrayNumber='"+ xVal +"'>";
	countForId++;
	var regExp = new RegExp(xTextMatch,"ig");
	var start = xStr[0].search(regExp);
	var matchedText = xStr[0].substring(start,start + xTextMatch.length);
	var Replacestr = xStr[0].replace(regExp,undeStart+matchedText+undeEnd);
	var num = "<span class=\"number\">"+xStr[1]+"</span>";
	return selectSpanStart + selectSpanMid + num+Replacestr + selectSpanEnd;
}
//================================================================
function SetHighColor(searchInputBox){
	if(searchInputBox){
		currentValueSelected = searchInputBox.id.slice(searchInputBox.id.indexOf("_")+1,
		searchInputBox.id.length);
        }
 	for(var i = 0; i < countForId; i++){
		document.getElementById('OptionsList_' + i).className ='spanNormalElement';
        }
	var listItem = document.getElementById('OptionsList_' +	currentValueSelected)
	if(listItem){
		listItem.className = 'spanHighElement';
	}
}

//================================================================
function SetText(xVal){
	searchInputBox.value = arrOptions[xVal][0]; //set text value
	document.getElementById("listBox").style.display = "none";
	currentValueSelected = -1; //remove the selected index
}
//================================================================
function GrabHighlighted(){
	if(currentValueSelected >= 0){
		xVal = document.getElementById("OptionsList_" +	currentValueSelected).getAttribute("theArrayNumber");
		SetText(xVal);
		HideTheBox();
	}
}

//================================================================
function HideTheBox(){
	document.getElementById("listBox").style.display = "none";
	currentValueSelected = -1;
}
//================================================================
function MoveHighlight(xDir){
	if(currentValueSelected >= -1 && document.getElementById("listBox").style.display == "block"){
		newValue = parseInt(currentValueSelected) + parseInt(xDir);
		newValue = newValue>=countForId?-1:newValue<-1?-1:newValue;
		currentValueSelected = newValue;
		SetHighColor (null);
	}
}

//================================================================
function ReDraw(){
	BuildList(document.getElementById("s").value);
}
//================================================================

