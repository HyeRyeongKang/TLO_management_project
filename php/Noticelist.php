<?php
	$con = mysqli_connect("localhost", "root", "", "logintest");
	mysqli_query($con,'set names utf8');
	$result = mysqli_query($con, "SELECT * FROM notice ORDER BY noticeDate DESC;");
	$response = array();

	while($row = mysqli_fetch_array($result))
	{
		array_push($response, array("noticeContent"=>$row["noticeContent"], "noticeName"=>$row["noticeName"], "noticeDate"=>$row["noticeDate"]));
	}
	echo json_encode(array("response"=>$response));
	mysqli_close($con);
?>
