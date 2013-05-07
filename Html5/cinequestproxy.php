<?php
/**
 *
 */
 // cinequest location
define ('HOSTNAME', 'http://mobile.cinequest.org/mobileCQ.php');

// we're going to just pass on the query string
$url = HOSTNAME."?".$_SERVER["QUERY_STRING"];

// Use curl to request the data from cinequest
$session = curl_init($url);
curl_setopt($session, CURLOPT_HEADER, false);
curl_setopt($session, CURLOPT_RETURNTRANSFER, true);
$xml = curl_exec($session);

// now echo the results
header("Content-Type: text/xml");

echo $xml;
curl_close($session);
?>
