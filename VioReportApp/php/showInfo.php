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
    $reportId = $_POST['reportId'];

    $sql = "SELECT r_id, id, time, imagePath FROM report WHERE g_id = ?";

    // Prepared statement 생성
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $reportId);

    // 쿼리 실행
    $stmt->execute();

    // 결과 가져오기
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            echo $row["imagePath"]. "/" . $row["r_id"]. "/" . $row["id"]. "/" . $row["time"]. "/"; 
        }
    } else {
        echo "false";
    }

    // 연결 종료
    $stmt->close();
    $conn->close();
?>
