<?php
    $host = "localhost";
    $user = "root";
    $password = "";
    $database = "app";

    // MySQL 데이터베이스 연결
    $conn = new mysqli($host, $user, $password, $database);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    $id = $conn->real_escape_string($_POST['id']);
    $pass = $_POST['pass'];

    // 첫 번째 쿼리 실행
    $query = "SELECT id, pass FROM member WHERE id=?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $stmt->store_result(); // 결과 집합을 서버에 저장

    if ($stmt->num_rows == 1) {
        $stmt->bind_result($id, $hashedPass); // $hashedPass 변수 추가
        $stmt->fetch();
        
        // 입력된 비밀번호와 저장된 비밀번호의 일치 여부 확인
        if (hash('sha256', $pass) === $hashedPass) {
            echo "true";
        } else {
            echo "passError";
        }
    } else {
        echo "idError";
    }

    $stmt->close();
    $conn->close();
?>
