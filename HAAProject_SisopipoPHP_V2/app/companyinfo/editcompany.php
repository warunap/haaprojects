<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 * 
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
require_once '_initialization_companyinfo.php';
startPage('Add Company Info');
loadModuleCSS('css/companyinfo.css');
showAppLinks();


$companyid= HttpGetData("companyid");
if($companyid == null){
	header('Bad request!', true, 404);
	exit();
}
$db = connectDB();
$row = $db->query("SELECT * FROM ". COMPANY_INFO_TABLE . " limit 1")->fetch();

if($row == null){
	$errorMsg = "can't find company with id " . companyid ."!";
	header($errorMsg, true, 404);
	closeDB();
	exit();
}

$company = getCompanyFromRow($row);
showCompanyInputForm($company);

closeDB();
endPage();
?>