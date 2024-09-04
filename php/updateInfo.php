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
    $id = $_POST["id"];
    $curPass = $conn->real_escape_string($_POST["curPass"]);
    $newPass = $conn->real_escape_string($_POST["newPass"]);

    // SHA256로 암호화된 비밀번호 생성
    $hashedCurPass = hash('sha256', $curPass);
    $hashedNewPass = hash('sha256', $newPass);

    // Prepared statement를 사용하여 SQL Injection 방지
    $stmt = $conn->prepare("SELECT * FROM member WHERE id = ? AND pass = ?");
    $stmt->bind_param("ss", $id, $hashedCurPass);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->close();

        // Prepared statement를 사용하여 SQL Injection 방지
        $stmt = $conn->prepare("UPDATE member SET pass = ? WHERE id = ?");
        $stmt->bind_param("ss", $hashedNewPass, $id);
        $stmt->execute();
        $stmt->close();

        echo 'true';
    } else {
        echo 'false';
    }

    $conn->close();
?>
