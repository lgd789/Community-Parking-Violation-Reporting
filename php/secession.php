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

    $id = $_POST['id'];

    $stmt = $conn->prepare("DELETE FROM member WHERE id = ?");
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $stmt->close();


    $conn->close();
?>
