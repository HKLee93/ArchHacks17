<?php
	
echo "Sending collected data to the server.\n";

if(!empty($_POST)){

	$file = fopen("data.csv", "w");
	foreach($_POST as $key => $value){
    	
    	echo fwrite($file, $key.", ".$value);
	}
	fclose($file);
}

	
?>
