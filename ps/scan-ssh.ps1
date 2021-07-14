$FileOut = ".\Computers.csv"
$Subnet = "192.168.88."

1..254|ForEach-Object {
    
	$ipaddress = "$SubNet$_"
	$port = 22
	$connection = New-Object System.Net.Sockets.TcpClient($ipaddress, $port)
	if ($connection.Connected) {
		 Write-Host $ipaddress + ": Success" 
	} else { 
		 Write-Host $ipaddress + ": Failed" 
	}
	$connection = $null
}
