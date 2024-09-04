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
    $password = $_POST["password"];
    $carNumber = $_POST["carNumber"];
    $token = $_POST["token"];

    // 중복 회원 가입 여부 확인하기
    $result = $conn->prepare("SELECT id FROM member WHERE id = ?");
    $result->bind_param("s", $id);
    $result->execute();
    $result->store_result();
    if ($result->num_rows > 0) {
        $result->close();
        $conn->close();
        echo json_encode(array("result" => "fail", "message" => "이미 가입된 아이디입니다."));
        exit();
    }

    // 비밀번호를 SHA256으로 해시화하기
    $hashedPassword = hash('sha256', $password);

    // 회원 정보 저장하기
    if (empty($carNumber)) {
        $query = "INSERT INTO member (id, pass, token) VALUES (?, ?, ?)";
        $stmt = $conn->prepare($query);
        $stmt->bind_param("sss", $id, $hashedPassword, $token);
    } else {
        $query = "INSERT INTO member (id, pass, carNumber, token) VALUES (?, ?, ?, ?)";
        $stmt = $conn->prepare($query);
        $stmt->bind_param("ssss", $id, $hashedPassword, $carNumber, $token);
    }

    if ($stmt->execute()) {
        echo json_encode(array("result" => "success", "message" => "회원가입이 완료되었습니다."));
    } else {
        echo json_encode(array("result" => "fail", "message" => "회원가입에 실패했습니다."));
    }

    $stmt->close();
    $conn->close();
?>
