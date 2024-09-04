<?php
    include 'sendMessage.php';

    $host = "localhost";
    $user = "root";
    $password = "";
    $database = "app";

    // MySQL 데이터베이스 연결
    $conn = new mysqli($host, $user, $password, $database);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    $id = $_POST['id'];
    $carNum = $_POST['carNum'];
    $reportType = $_POST['reportType'];
    $imagePath = $_POST['imagePath'];
    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $address = $_POST['address'];

    $imagePath = explode('/', $imagePath);
    $fileName = end($imagePath);
    $data = explode('_', str_replace('.jpg', '', $fileName));

    $date = $data[0];
    $time = $data[1];

    $error_range = 0.00029793738107748117;

    // 가장 최근의 r_id 가져오기
    $query = "SELECT r_id FROM report ORDER BY r_id DESC LIMIT 1";
    $result = $conn->query($query);
    $row = $result->fetch_assoc();
    $g_id = $row["r_id"] + 1;

    // 동일한 차량 번호의 이전 보고서 확인
    $query = "SELECT g_id, r_id, latitude, longitude, address, time FROM report WHERE carNumber = ? GROUP BY g_id";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $carNum);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $latitude2 = $row["latitude"];
            $longitude2 = $row["longitude"];
            $time2 = $row["time"];

            $line1 = $latitude2 - $latitude;
            $line2 = $longitude2 - $longitude;

            $sqrt = sqrt(pow($line1, 2) + pow($line2, 2));

            if ($error_range > $sqrt) {
                $currentTime = strtotime($time);
                $previousTime = strtotime($time2);
                $timeDifference = $currentTime - $previousTime;

                // 1분 이상 경과한 경우
                if ($timeDifference >= 60) {
                    $g_id = $row["g_id"];
                    $latitude = $row["latitude"];
                    $longitude = $row["longitude"];
                    $address = $row["address"];
                    break;
                } else {
                    echo "timeError/" . 60 - $timeDifference;
                    return;
                }
            }
        }
    }

    if (empty($id)) {
        $query = "INSERT INTO report (carNumber, type, date, time, latitude, longitude, address, imagePath, g_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        $stmt = $conn->prepare($query);
        $stmt->bind_param("ssssddssi", $carNum, $reportType, $date, $time, $latitude, $longitude, $address, $fileName, $g_id);
        $stmt->execute();
    } else {
        $query = "INSERT INTO report (id, carNumber, type, date, time, latitude, longitude, address, imagePath, g_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        $stmt = $conn->prepare($query);
        $stmt->bind_param("sssssddssi", $id, $carNum, $reportType, $date, $time, $latitude, $longitude, $address, $fileName, $g_id);
        $stmt->execute();

        $ini_array = parse_ini_file("admin//config.ini", true);
        $registrationPoint = $ini_array['POINT']['registrationPoint'];

        $query = "UPDATE member SET point = point + ? WHERE id = ?";
        $stmt = $conn->prepare($query);
        $stmt->bind_param("is", $registrationPoint, $id);
        $stmt->execute();
    }

    // 차량 소유자에게 알림 보내기
    $query = "SELECT token FROM member WHERE carNumber = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $carNum);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows == 0) {
        $row = $result->fetch_assoc();
        $token = $row['token'];
        $title = '차량 이동 요청';
        $message = '사용자의 차량이 신고되었습니다. 차량을 이동시켜주세요';
        sendNotification($token, $title, $message);
    }

    $conn->close();
?>
