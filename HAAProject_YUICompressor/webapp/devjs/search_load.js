window.onload = function(){
	if(queryString!=null ){
		document.getElementById("s").value =queryString;
	}
	lightKeyWords("main");
	downListInitial();
}
