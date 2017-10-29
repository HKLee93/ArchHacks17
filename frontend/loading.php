<?php

$dataReady = false;
while(!$dataReady){

  if(file_exists(__DIR__ . "/vis_info2.csv")){
    $dataReady = true;
  }
  else{
    $dataReady = false;
  }
  
}
echo json_encode(array("success" => true, "message" => "success"));
exit;

?>
