<?php

$dataReady = false;
while(!$dataReady){

  $contents = file_get_contents("data.csv");
  if($contents != ""){
    $dataReady = true;
  }
  else{
    $dataReady = false;
  }

}
echo json_encode(array("success" => true, "message" => "success"));
exit;

?>
