<?php

    // MySQL 데이터베이스 연결 정보
    $host = "localhost";
    $user = "root";
    $password = "";
    $database = "app";

    // MySQL 데이터베이스 연결
    $conn = new mysqli($host, $user, $password, $database);

    // 연결 오류 확인
    if ($conn->connect_error) {
        die("MySQL 연결 오류: " . $conn->connect_error);
    }

    // 앱에서 받은 위도와 경도 값
    $appLatitude = $_POST['latitude'];
    $appLongitude = $_POST['longitude'];
    $radius = 300;

    $count = 0; // 반경 내 좌표 개수

    // 쿼리 실행하여 좌표들 가져오기
    $query = "SELECT DISTINCT latitude, longitude FROM report WHERE type != '차로 변경'";

    if ($result = $conn->query($query)) {
        // 가져온 좌표들과 앱에서 받은 위치와의 거리 계산 및 개수 확인
        while ($row = $result->fetch_assoc()) {
            $distance = calculateDistance($appLatitude, $appLongitude, $row['latitude'], $row['longitude']);
            if ($distance <= $radius) {
                $count++;
            }
        }

        // 결과 출력
        echo $count;
    }

    // MySQL 연결 종료
    $conn->close();

    // 두 지점 사이의 거리를 계산
    function calculateDistance($lat1, $1, $lat2, $lon2)
    {
        $earthRadius = 6371; // 지구 반지름 (단위: km)

        $dLat = deg2rad($lat2 - $lat1);
        $dLon = deg2rad($lon2 - $lon1);

        $a = sin($dLat / 2) * sin($dLat / 2) + cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * sin($dLon / 2) * sin($dLon / 2);
        $c = 2 * atan2(sqrt($a), sqrt(1 - $a));

        $distance = $earthRadius * $c; // 두 지점 사이의 거리 (단위: km)
        $distance = $distance * 1000; // 거리를 미터로 변환

        return $distance;
    }

?>
