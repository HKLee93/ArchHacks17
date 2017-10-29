<?php

if(isset($_POST['start'])){

	$file = fopen("status.txt", 'w');
	echo fwrite($file, "true");
	fclose($file);
	//header("Location: loading.html");
	exit;
}

?>