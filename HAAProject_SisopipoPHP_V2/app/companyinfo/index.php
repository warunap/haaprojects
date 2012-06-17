<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 *
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
require_once '_initialization_companyinfo.php';
startPage('Company List');
loadModuleCSS('css/companyinfo.css');
showAppLinks();
$db = connectDB();
$page = _page_getpage('page');
_page_initalize($db,COMPANY_INFO_TABLE,$page);

$query = 'SELECT * from ' . COMPANY_INFO_TABLE .' LIMIT '. $page->offset .',' . $page->pagesize;
$result = $db->query($query) or die('Error, query failed');

function GetUrlLink($url){
	return '<a href="' . $url . '" target="_blank">'. $url .'</a>';
}

function addCompanyTableLine($company){
	print '<tr>';
	print '<td>'. $company->companyid.'</td>';
	print '<td>'. $company->companyname.'</td>';
	print '<td>'. GetUrlLink($company->webhost) .'</td>';
	print '<td>'. GetUrlLink($company->mailhost) .'</td>';
	print '<td>'. $company->tel.'</td>';
	print '<td>'. $company->phone.'</td>';
	print '<td>'. $company->email.'</td>';
	print '<td>'. $company->contactor.'</td>';
	print '<td>'. $company->address.'</td>';
	print '<td><a href="editcompany.php?companyid=' . $company->companyid. '"  target="_blank">Edit</td>';
	print '</tr>';
}

print '<center>';
print('<font size="30">Company List</font>');
print('<table class="greenlist">');
print "<tr>";
print "<th>Company ID</th>";
print "<th>Company Name</th>";
print "<th>Web Host</th>";
print "<th>Mail Host</th>";
print "<th>Tel</th>";
print "<th>Phone</th>";
print "<th>Email</th>";
print "<th>Contactor</th>";
print "<th>Address</th>";
print "<th>Edit</th>";
print "</tr>";


foreach($result as $row) {
	addCompanyTableLine(getCompanyFromRow($row));
}
print("</table>");
print '</center>';
endPage(); ?>