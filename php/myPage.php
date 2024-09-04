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

    $id = $_POST["id"];

    // 보고서 개수 조회 쿼리 최적화
    $sql = "SELECT COUNT(*) AS reportCnt FROM report WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($result->num_rows == 1) {
        $row = $result->fetch_assoc();
        $reportCnt = $row['reportCnt'];
    } else {
        $reportCnt = 0;
    }
    $stmt->close();

    // 히스토리 개수 조회 쿼리 최적화
    $sql = "SELECT COUNT(*) AS historyCnt FROM history WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($result->num_rows == 1) {
        $row = $result->fetch_assoc();
        $historyCnt = $row['historyCnt'];
    } else {
        $historyCnt = 0;
    }
    $stmt->close();

    $reportCnt += $historyCnt;

    // 회원 정보 및 순위 조회 쿼리 최적화
    $sql = "SELECT id, point, carNumber, RANK() OVER (ORDER BY point DESC) AS rank FROM member WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows == 1) {
        $row = $result->fetch_assoc();
        $userPoint = $row['point'];
        $userRank = $row['rank'];
        $carNumber = $row['carNumber'];
    } else {
        $userPoint = 0;
        $userRank = 0;
        $carNumber = "";
    }
    $stmt->close();

    // 결과 출력
    if ($carNumber === "") {
        echo $reportCnt . "/" . $userRank . "/" . $userPoint . "/";
    } else {
        echo $reportCnt . "/" . $userRank . "/" . $userPoint . "/" . $carNumber . "/";
    }

    // MySQL 연결 종료
    $conn->close();
?>