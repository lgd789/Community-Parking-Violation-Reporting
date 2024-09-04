<?php
    // MySQL 데이터베이스 연결 정보
    $host = "localhost";
    $user = "root";
    $password = "";
    $database = "app";

    // 데이터베이스 연결
    $conn = new mysqli($host, $user, $password, $database);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // POST 요청으로 받은 데이터 검증
    $id = isset($_POST["id"]) ? $_POST["id"] : "";
    // 입력 값이 빈 값인 경우에 대한 처리 (예: required 필드인 경우)
    if (empty($id)) {
        die("ID를 입력해주세요.");
    }

    // 데이터베이스 쿼리 실행
    $stmt = $conn->prepare("SELECT id FROM member WHERE id = ?");
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $stmt->store_result();

    // 결과 확인
    if ($stmt->num_rows > 0) {
        echo "true"; // 데이터베이스에 해당 아이디가 존재하는 경우
    } else {
        echo "false"; // 데이터베이스에 해당 아이디가 존재하지 않는 경우
    }

    $stmt->close();
    $conn->close();
?>
