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
    $g_id = intval($_POST['g_id']);
    $id = $_POST['id'];
    $content = $_POST['content'];

    // 입력 데이터 검증
    if ($g_id <= 0 || empty($id) || empty($content)) {
        echo json_encode(array("result" => "fail", "message" => "입력 데이터가 유효하지 않습니다."));
        exit();
    }

    // 쿼리를 사용하여 댓글 데이터 삽입
    $query = "INSERT INTO comments (g_id, id, content) VALUES (?, ?, ?)";
    $stmt = $conn->prepare($query);

    // 트랜잭션 시작
    $conn->begin_transaction();

    try {
        $stmt->bind_param("iss", $g_id, $id, $content);
        $stmt->execute();

        if ($stmt->affected_rows > 0) {
            $conn->commit();
            echo json_encode(array("result" => "success", "message" => "댓글 저장 완료"));
        } else {
            $conn->rollback();
            echo json_encode(array("result" => "fail", "message" => "댓글 저장 실패"));
        }
    } catch (Exception $e) {
        $conn->rollback();
        echo json_encode(array("result" => "fail", "message" => "댓글 저장 중 오류가 발생했습니다."));
    }

    // 데이터베이스 연결 종료
    $stmt->close();
    $conn->close();
?>

