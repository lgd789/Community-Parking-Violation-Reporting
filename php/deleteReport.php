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

    $r_id = intval($_POST['r_id']);

    echo $r_id;

    $stmt = $conn->prepare("DELETE FROM report WHERE r_id = ?");
    $stmt->bind_param("i", $r_id);
    if ($stmt->execute()) {
        echo "삭제되었습니다.";
    } else {
        echo "삭제에 실패했습니다.";
    }

    $stmt->close();
    $conn->close();
?>
