<?php
	
echo "Sending collected data to the server.\n";

if(!empty($_POST)){

	$file = fopen("x.csv", "w");
	$index = 0;
	foreach($_POST as $key => $value){
    	
    	echo fwrite($file, $key.", ".$value."\n");
    	$index++;
	}
	fclose($file);
}

	
?>
