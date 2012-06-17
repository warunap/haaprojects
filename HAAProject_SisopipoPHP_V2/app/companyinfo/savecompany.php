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
$company = getCompanyFromHttpPost();
$count = saveOrUpdateCompany($company);
if($count>0){
	print 'success!';
}else{
	print "failed!";
}
endPage();
?>