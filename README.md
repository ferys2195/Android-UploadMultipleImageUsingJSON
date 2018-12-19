# Upload-Image-Multiple-With-JSON
Library yang digunakan :
# interface uploading image like whatsapp
https://github.com/akshay2211/PixImagePicker.git 
# http request
https://github.com/amitshekhariitbhu/Fast-Android-Networking.git

# this for server side
<?php
header('Content-Type: application/json; charset=utf-8');

if($_SERVER['REQUEST_METHOD']=='POST'){

function keygen($length=40, $init=NULL)
{
	$key = '';
	list($usec, $sec) = explode(' ', microtime());
	mt_srand((float) $sec + ((float) $usec * 100000));
	
   	$inputs = array_merge(range('z','a'),range(0,9),range('A','Z'));

   	for($i=0; $i<$length; $i++)
	{
   	    $key .= $inputs{mt_rand(0,61)};
	}
	$result = $init.$key;
	return $result;
}

function getBytesFromHexString($hexdata){
	for($count = 0; $count < strlen($hexdata); $count+=2)
	$bytes[] = chr(hexdec(substr($hexdata, $count, 2)));
  return implode($bytes);
}

function getImageMimeType($imagedata){
	$imagemimetypes = array( 
			    "jpeg" => "FFD8", 
			    "png" => "89504E470D0A1A0A", 
			    "gif" => "474946",
			    "bmp" => "424D", 
			    "tiff" => "4949",
			    "tiff" => "4D4D"
			  );

	foreach ($imagemimetypes as $mime => $hexbytes){
		$bytes = getBytesFromHexString($hexbytes);
		    if (substr($imagedata, 0, strlen($bytes)) == $bytes)
		    return $mime;
	}

	return NULL;
}

$json = $_POST['gambar'];
$json = str_replace('"\"', NULL, $json);
$json = str_replace('\/', '/', $json);
$json = str_replace('\n', NULL, $json);

$decode = json_decode($json, TRUE);
$result = $decode['result'];

$dir = "produk/";
if (!file_exists($dir)) {
    mkdir($dir, 0777);
    chmod($dir, 0777);
}

$arr = array();

foreach ($result as $key) {

	$list			= $key['gambar'];

	$imgdata 		= base64_decode($list);

	$mimetype 		= getImageMimeType($imgdata);
					
	$newImageName	= keygen(25);
		
	$newImage 		= $newImageName.'.'.$mimetype;
		
	$path 			= $dir.$newImage;
					
	$move_file		= 	file_put_contents($path,$imgdata);
	 
}
if($move_file){
	array_push($arr,
			array(
				"alert"			=>'Berhasil diPosting'
		 	)
	 	);
	 
	 echo json_encode(array("result"=>$arr),JSON_PRETTY_PRINT);
}else{
	array_push($arr,
			array(
				"alert"			=>'Gagal Diposting'
		 	)
	 	);
	 
	 echo json_encode(array("result"=>$arr),JSON_PRETTY_PRINT);
}

}
?>
