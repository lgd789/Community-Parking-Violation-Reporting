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

    // 쿼리 실행
    $sql = "SELECT carNumber, latitude, longitude, g_id, type, COUNT(*) AS count FROM report GROUP BY g_id ORDER BY g_id DESC";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        $output = "";
        while ($row = $result->fetch_assoc()) {
            $output .= $row["carNumber"] . "/" . $row["latitude"] . "/" . $row["longitude"] . "/" . $row["g_id"] . "/" . $row["type"] . "/" . $row["count"] . "/";
        }
        echo $output;
    } else {
        echo "false";
    }

    $conn->close(); // 연결 종료
?>
