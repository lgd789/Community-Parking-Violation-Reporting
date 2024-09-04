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
    if($_POST["limit"] === "-1"){
        $limit = false;
    }
    else {
        $limit = intval($_POST["limit"]);
    }

    $id = $_POST["id"];
    if ($id === "-1") {
        $sql = "SELECT DISTINCT g_id, address, carNumber, latitude, longitude, type, COUNT(*) AS count 
                FROM report 
                GROUP BY g_id 
                ORDER BY g_id DESC";
        if ($limit) {
            $sql .= " LIMIT $limit";
        }
    } else {
        $sql = "SELECT DISTINCT g_id, address, carNumber, latitude, longitude, type, COUNT(*) AS count 
                FROM report 
                WHERE g_id IN (SELECT g_id FROM report WHERE id = ?) 
                GROUP BY g_id 
                ORDER BY g_id DESC";
    }

    // Prepared statement 생성
    $stmt = $conn->prepare($sql);
    if ($id !== "-1") {
        $stmt->bind_param("s", $id);
    }

    // 쿼리 실행
    $stmt->execute();

    // 결과 가져오기
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            echo $row["g_id"]. "/" . $row["address"]. "/" . $row["carNumber"]. "/". $row["count"]. "/". $row["latitude"]. "/". $row["longitude"]. "/". $row["type"]. "/";
        }
    } else {
        echo "false";
    }

    // 연결 종료
    $stmt->close();
    $conn->close();
?>