<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 *
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
require_once '../../_initialization.php';
require_once '../../library/_page_lib.php';
define('COMPANY_INFO_TABLE','company');


function HttpPostData($key){
	return isset($_POST[$key]) ? $_POST[$key] : null;
}
function HttpGetData($key){
	return isset($_GET[$key]) ? $_GET[$key] : null;
}


/*
 *Company Object
 */
class Company{

	var $companyid;
	var $companyname;
	var $webhost;
	var $mailhost;
	var $tel;
	var $email;
	var $phone;
	var $contactor;
	var $address;


}
function getCompanyFromRow($row){
	if($row==null){
		return null;
	}

	$cp = new Company();
	$cp->companyid= $row["companyid"];
	$cp->companyname= $row["companyname"];
	$cp->webhost=$row["webhost"];
	$cp->mailhost=$row["mailhost"];
	$cp->tel=$row["tel"];
	$cp->phone=$row["phone"];
	$cp->email=$row["email"];
	$cp->contactor=$row["contactor"];
	$cp->address=$row["address"];

	return $cp;
}

function getCompanyFromHttpPost(){
	$cp = new Company();
	$cp->companyid= HttpPostData("companyid");
	$cp->companyname= HttpPostData("companyname");

	if($cp->companyid == null && $cp->companyname==null){
		return null;
	}

	$cp->webhost=HttpPostData("webhost");
	$cp->mailhost=HttpPostData("mailhost");
	$cp->tel=HttpPostData("tel");
	$cp->phone=HttpPostData("phone");
	$cp->email=HttpPostData("email");
	$cp->contactor=HttpPostData("contactor");
	$cp->address=HttpPostData("address");

	return $cp;
}
/*
 * show top links
 */
function showAppLinks(){
	print '<div class="linkmenu">';
	print '&nbsp;<a href="index.php">Index</a>';
	print '&nbsp;|&nbsp;<a href="addcompany.php">Add Company</a>';
	print '</div>';
}

function addNewInput($inputName,$defaultValue,$maxlength){
	$size = 100;
	if($size > $maxlength){
		$size = $maxlength;
	}
	print '<tr><td class="key noborder">'. $inputName.'</td><td class="noborder"><input type="text" class="data" name="'. $inputName .'" size="'. $size .'" maxlength="'. $maxlength .'" value="' . $defaultValue . '"  /></tr>';
}

function showCompanyInputForm($company){
	if($company==null){
		$company = new Company();
	}
	print '<form method="post" action="savecompany.php">';
	print '<input class="data" type="hidden" name="companyid" value="' . $company->companyid . '"  />';
	print '<center>';
	print('<table class="noborder greenlist">');
	addNewInput("companyname",$company->companyname,100);
	addNewInput("webhost",$company->webhost,120);
	addNewInput("mailhost",$company->mailhost,120);
	addNewInput("tel",$company->tel,25);
	addNewInput("phone",$company->phone,15);
	addNewInput("email",$company->email,100);
	addNewInput("contactor",$company->contactor,100);
	addNewInput("address",$company->address,255);
	print('</table>');
	print '<br/><input type="submit" value="submit" />';

	print '</center>';
	print '</form>';
}

function saveOrUpdateCompany($company){
	try {
		if($company==null){
			return 0;
		}

		$db = connectDB();
		$sql='';
		if($company->companyid ==null){
			$sql.= 'insert into ' . COMPANY_INFO_TABLE . '(companyname,webhost,mailhost,tel,phone,email,contactor,address) values(';
			$sql.= '\'' .$company->companyname .'\'';
			$sql.= ',\'' . $company->webhost .'\'';
			$sql.= ',\'' . $company->mailhost .'\'';
			$sql.= ',\'' . $company->tel .'\'';
			$sql.= ',\'' . $company->phone .'\'';
			$sql.= ',\'' . $company->email .'\'';
			$sql.= ',\'' . $company->contactor .'\'';
			$sql.= ',\'' . $company->address .'\'';
			$sql.= ')';
		}
		else{
			$sql.= 'update ' . COMPANY_INFO_TABLE ;
			$sql.= ' set companyname=\'' .$company->companyname .'\'';
			$sql.= ',webhost=\'' . $company->webhost .'\'';
			$sql.= ',mailhost=\'' . $company->mailhost .'\'';
			$sql.= ',tel=\'' . $company->tel .'\'';
			$sql.= ',phone=\'' . $company->phone .'\'';
			$sql.= ',email=\'' . $company->email .'\'';
			$sql.= ',contactor=\'' . $company->contactor .'\'';
			$sql.= ',address=\'' . $company->address .'\'';
			$sql.= ' where companyid='. $company->companyid;
		}

		$count = $db->exec($sql);
		return $count;
	} catch (PDOException $e) {
		print "Error!: " . $e->getMessage() . "<br/>";
		die();
	}
}

?>