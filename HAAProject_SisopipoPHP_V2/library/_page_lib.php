<?php
class Page{
	var $pagesize;
	var $offset;
	var $currpage;
	var $recordNum;
	var $pageNum;

	public function correct(){
		if($this->recordNum>0){

			$lastPagesize = $this->recordNum % $this->pagesize;
			$this->pageNum = intval($this->recordNum / $this->pagesize);
			if($lastPagesize>0){
				$this->pageNum++;
			}
			if($this->currpage > $this->pageNum){
				$this->currpage = $this->pageNum;
			}
			if($this->currpage < 0){
				$this->currpage = 0;
			}
			$this->offset = ($this->currpage - 1) * $this->pagesize;

			if($this->offset<0){
				$this->offset = 0;
			}
		}
	}
	
	public function __toString(){
		return $this->offset . ',' .$this->currpage. ',' .$this->recordNum;
	}
}

function _page_initalize($db,$tableName,$page){
	$query = 'SELECT count(1) from ' . $tableName;
	$row = $db->query($query)->fetch();
	$page->recordNum = $row[0];
	$page->correct();
}

function _page_getpage($name){
	return _page_getpageobj($name,20);
}

function _page_getpageobj($name,$pagesize){
	$page= new Page();
	$page->pagesize = $pagesize;
	$page->currpage = 1;
	if(isset($_GET[$name]))
	{
		$page->currpage = $_GET[$name];
	}
	return $page;
}
?>