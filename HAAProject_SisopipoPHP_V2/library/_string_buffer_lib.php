<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 * 
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
/**
 * @author Sourcemaker Development
 * @package org.sourcemaker.util
 */
class StringBuffer extends Object
implements CharSequence{

	private $str;

	public function __construct(){

	}

	public function append($str){
		$str = String::valueOf($str);
		$this->str.= $str;
	}

	public function getString(){
		return new String( $this->str );
	}

	public function charAt($index){
		$str = new String( $this->str );
		return $str->charAt($index);
	}

	public function length(){
		$str = new String( $this->str );
		return $str->length();
	}

	public function toString(){
		return $this->str;
	}
}

?>