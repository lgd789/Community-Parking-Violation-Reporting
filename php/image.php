<?php
    if(isset($_FILES['file'])) {
        $target_dir = "uploads/";
        $target_file = $target_dir.basename($_FILES["file"]["name"]);

        if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file)) {
        # echo "The file ". htmlspecialchars(basename( $_FILES["file"]["name"])). " has been uploaded.";
        } else {
        # echo "Sorry, there was an error uploading your file.";
        }
        echo $target_file;

        header('Content-Type: text/html; charset=UTF-8');
        putenv("LC_ALL=ko_KR.UTF-8");
        setlocale(LC_ALL, 'ko_KR.UTF-8');
        
        //exec("python detection1.py ".$img_path." 2>&1", $output); 

        // $output = $output[0];
        // $output = mb_convert_encoding($output, 'UTF-8', 'EUC-KR');
        // echo mb_convert_encoding($output[0], 'UTF-8', 'EUC-KR');
    } else {
        # echo "No file received.";
    }

?>
