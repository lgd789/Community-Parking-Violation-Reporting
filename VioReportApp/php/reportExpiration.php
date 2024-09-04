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

    $query="SELECT report.g_id, member.token FROM report JOIN member ON report.id = member.id WHERE report.g_id IN (SELECT g_id FROM report GROUP BY g_id HAVING COUNT(*) = 1)";
    $result=$conn->query($query);
     
    if ($result->num_rows > 0) {
        while($row = mysqli_fetch_assoc($result)) {
            $token = $row['token'];
            $title = '신고 만료 알림';
            $message = '신고번호 : '.$row['g_id']."의 신고가 만료되었습니다.";
        
            sendNotification($token, $title, $message);
        }
    }
 
?>