<?php
    // MySQL 데이터베이스 연결 정보
    $host = "localhost";
    $user = "root";
    $password = "";
    $database = "app";

    // MySQL 데이터베이스 연결
    $conn = new mysqli($host, $user, $password, $database);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // POST 요청으로 받은 데이터 파싱하기
    $id = $conn->real_escape_string($_POST["id"]);
    $carNumber = $conn->real_escape_string($_POST["carNum"]);

    $stmt = $conn->prepare("UPDATE member SET carNumber = ? WHERE id = ?");
    $stmt->bind_param("ss", $carNumber, $id);
    $stmt->execute();

    // 영향을 받은 행의 수 확인
    $stmt->store_result();

    if ($stmt->num_rows;) {
        echo 'true';
    } else {
        echo 'false';
    }

    // 연결 종료
    $stmt->close();
    $conn->close();
?>
