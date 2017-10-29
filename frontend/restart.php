<?php

//resets the response.txt:
$file = fopen("response.txt", w);
echo fwrite($file, "false");
fclose($file);

//deletes the csv:
$file = fopen("data.csv", w);
echo fwrite($file, "");
fclose($file);

header("Location: start.html");
exit();
 ?>
