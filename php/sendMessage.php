<?php
    function sendNotification($token, $title, $message) {
        // FCM 서버에 보낼 데이터
        $data = [
            'to' => $token, // 앱의 토큰값
            'notification' => [
                'title' => $title,
                'body' => $message
            ],
            'data' => [
                'key1' => 'value1',
                'key2' => 'value2'
            ]
        ];

        // FCM 서버에 전송할 헤더
        $headers = [
            'Authorization: key=AAAAGutsteg:APA91bHPjSWkon-1wqdLhcgeIdqP3B2R7fTilkvs4sPIWrxtnPBNEffII3v_ZkBXiWReJbZ7SLaVhT0God271TfapMT78tFl9aoxo6pYcwxi1M0qWypx75FvWQeoBxH9AI8sH8z0KnzB',
            'Content-Type: application/json'
        ];

        // cURL 초기화
        $ch = curl_init();

        // cURL 옵션 설정
        curl_setopt($ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));

        // cURL 실행
        $result = curl_exec($ch);

        // cURL 에러 체크
        if ($result === false) {
            echo 'FCM 전송 실패: ' . curl_error($ch);
        }

        // cURL 세션 종료
        curl_close($ch);

        // 결과 출력
        echo 'FCM 전송 결과: ' . $result;
    }  
?>