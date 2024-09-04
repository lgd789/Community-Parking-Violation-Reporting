<?php
    $ini_array = parse_ini_file("admin//config.ini", true);
    $reportCount_1 = $ini_array['COLOR']['reportCount_1'];
    $reportCount_2 = $ini_array['COLOR']['reportCount_2'];
    $reportCount_3 = $ini_array['COLOR']['reportCount_3'];
 
    echo $reportCount_1."/". $reportCount_2."/".$reportCount_3."/"; 
?>