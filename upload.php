<?php
header('Content-Type: application/json');

$uploadDir = 'uploads/'; // 设置上传目录

if (!file_exists($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

$response = array();

if ($_FILES['file']['error'][0] == UPLOAD_ERR_OK) {
    $fileCount = count($_FILES['file']['name']);

    for ($i = 0; $i < $fileCount; $i++) {
        $tmpFilePath = $_FILES['file']['tmp_name'][$i];

        if ($tmpFilePath != "") {
            $newFilePath = $uploadDir . $_FILES['file']['name'][$i];

            if (move_uploaded_file($tmpFilePath, $newFilePath)) {
                $response[] = array(
                    'file' => $_FILES['file']['name'][$i],
                    'status' => 'success'
                );
            } else {
                $response[] = array(
                    'file' => $_FILES['file']['name'][$i],
                    'status' => 'failed'
                );
            }
        }
    }
} else {
    $response['error'] = 'File upload failed';
}

echo json_encode($response);
?>
