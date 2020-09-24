<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');



//POST 값을 읽어온다.
$phone=isset($_POST['phone']) ? $_POST['phone'] : '';
$const_site = isset($_POST['const_site']) ? $_POST['const_site'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($phone != "" ){

    $sql="select * from checklist AS c left outer join user AS u on u.const_site=c.const_site where u.phone='$phone'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){

        echo "'";
        echo $phone;
        echo "'은 찾을 수 없습니다.";
    }
    else{

           $data = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

            extract($row);

            array_push($data,
                array('phone'=>$row["phone"],
                      'const_site'=>$row["const_site"],
                      'created_at'=>$row["created_at"],
                      'exceed_limit_level'=>$row["exceed_limit_level"],
                      'sens_code'=>$row["sens_code"],
                      'sect_seq'=>$row["sect_seq"],
                      'name'=>$row["name"],
                      'is_admin'=>$row["is_admin"],
                      'measure_date'=>$row["measure_date"]
                      
                      
                      
            ));
        }


        if (!$android) {
            echo "<pre>";
            print_r($data);
            echo '</pre>';
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("response"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
            
                   }
    }
}
else {
    echo "검색할 사용자를 입력하세요 ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST">
         아이디: <input type = "text" name = "phone" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>