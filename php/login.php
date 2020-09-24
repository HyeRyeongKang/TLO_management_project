<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');
        
    //POST 값을 읽어온다.
    $userID=isset($_POST['userID']) ? $_POST['userID'] : '';
    $userPassword = isset($_POST['userPassword']) ? $_POST['userPassword'] : '';
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    
    if (($userID != "") && ($userPassword != "")) {
        $sql="select * from user where userID='$userID' and userPassword='$userPassword'";
        $stmt = $con->prepare($sql);
        $stmt->execute();
        
        if ($stmt->rowCount() == 0)
        {
	$data = array();
            $data["success"] = "false";
	array_push($data);
	if (!$android) {
                echo "<pre>";
                print_r($data);
                echo '</pre>';
            }else
            {
                header('Content-Type: application/json; charset=utf8');
                $json = json_encode(array("root"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
                echo $json;
            }
        }
        else {
            $data = array();

            while($row=$stmt->fetch(PDO::FETCH_ASSOC))
            {
                extract($row);
        	    $data["success"] = "true";
	    
                array_push($data,
                    array(
		'userId'=>$userID,
                        'userPassword'=>$userPassword                                         
                ));
            }

            if (!$android) {
                echo "<pre>";
                print_r($data);
                echo '</pre>';
            }else
            {
                header('Content-Type: application/json; charset=utf8');
                $json = json_encode(array("root"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
                echo $json;
            }
        }
    }
    
    else {
        echo "fail";
    }
?>

<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST">
         아이디: <input type = "text" name = "userID" />
         비밀번호: <input type = "text" name = "userPassword" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>