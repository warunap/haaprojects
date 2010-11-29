<?php
/** Define ABSPATH as this files directory */

define( 'ABSPATH', dirname(__FILE__) . '/' );

echo 'dirname(__FILE__) --> ' . ABSPATH;
echo "<br/>";

echo 'dirname(ABSPATH) --> ' . dirname(ABSPATH);
echo "<br/>";

if ( file_exists( ABSPATH . 'index.php') ) {
	echo "exists index.php";
}


?>