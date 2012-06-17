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
showCompanyInputForm(null);
endPage();
?>