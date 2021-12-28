<?php
header('Content-Type: application/json');
$stringRawData = ( file_get_contents('php://input', TRUE) );
// if given json is not valid give an error and stop the program
if(!isValidJSON($stringRawData)){
    giveJsonError("Invalid json!");
}

############### Given data is a valid data, process and save it #######################
// parse json data, returns associative array 
$arrPostedJson = json_decode($stringRawData, true);

// extract paramters only
$arrPostedJson = $arrPostedJson['data'];

$objConn = connectToDatabase('form_application'); 
$objStmt = returnPreparedStmt($objConn, $arrPostedJson['name'], $arrPostedJson['phone'], $arrPostedJson['city']);

$objStmt->execute();


function returnPreparedStmt($objConn, $strName=null, $strPhone=null, $strCity=null){
    $strName = mysqli_real_escape_string($objConn, $strName);
    $strPhone = mysqli_real_escape_string($objConn, $strPhone);
    $strCity = mysqli_real_escape_string($objConn, $strCity);

    $stmt = $objConn->prepare("INSERT INTO form_data (`name`, `phone`, `city`) VALUES (?, ?, ?)");
    $stmt->bind_param("sss", $strName, $strPhone, $strCity);
    return $stmt;
}

function connectToDatabase($strDatabaseName){
    // Connect to database

    list($strHostName, $strUserName, $strPassword) = array("localhost", "intern", "internpasswd");
    $conn = mysqli_connect($strHostName, $strUserName, $strPassword, $strDatabaseName);

    if($conn->connect_error){
        giveJsonError("Connection error!", 500);
    }else{ 
        return $conn;
    }

}

function isValidJSON($str) {
    json_decode($str);
    return json_last_error() == JSON_ERROR_NONE;
}


function giveJsonError($message, $code=400){
    // function creates a json file containing the given message as an error
    // print the messages and stops the program.
    $error["error"] = array("code"=>$code, "message"=>$message);
    print_r(json_encode($error));
    exit(); 
}
?>