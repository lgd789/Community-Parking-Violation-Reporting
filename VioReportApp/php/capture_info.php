<?php
    // MySQL 데이터베이스 연결 정보
    $host = "";
    $user = "root";
    $password = "";
    $database = "app";

    $id = $_POST['id'];
    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $address = $_POST['address'];
    $imagePath =  $_POST['imagePath'];

    // 데이터베이스 연결
    $conn = new mysqli($host, $user, $password, $database);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // 파일명에서 날짜와 시간 추출
    $fileName = basename($imagePath);
    $data = explode('_', str_replace('.jpg', '', $fileName));
    $date = $data[0];
    $time = $data[1];

    // 가장 최근 r_id 조회
    $query = "SELECT r_id FROM report ORDER BY r_id DESC LIMIT 1";
    $result = $conn->query($query);
    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $r_id = $row['r_id'];
    } else {
        $r_id = 0;
    }
    $g_id = $r_id + 1;

    // 보고서 데이터 삽입 쿼리
    $insertQuery = "INSERT INTO report (id, date, time, latitude, longitude, address, imagePath, g_id) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    $stmt = $conn->prepare($insertQuery);
    if (!$stmt) {
        die("Query preparation failed: " . $conn->error);
    }

    // Prepared Statement 재활용하여 쿼리 실행
    $stmt->bind_param("sssssssi", $id, $date, $time, $latitude, $longitude, $address, $fileName, $g_id);
    $stmt->execute();
    $stmt->close();

    // 데이터베이스 연결 종료
    $conn->close();
?>
