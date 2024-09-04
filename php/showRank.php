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
    $limit = $_POST["limit"] === "-1" ? false : intval($_POST["limit"]);

    $sql = "SELECT id, point FROM member ORDER BY point DESC";
    if ($limit) {
        $sql .= " LIMIT $limit";
    }

    // 쿼리 실행
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            echo $row["id"]. "/" . $row["point"]. "/";
        }
    } else {
        echo "false";
    }

    // 연결 종료
    $conn->close();
?>
