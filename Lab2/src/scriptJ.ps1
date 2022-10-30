$javaFile = $args[0] # Nume fisier java
#Write-Host $param1

$matrixNumber = $args[1]

$noThreads = $args[2] # No of threads
#Write-Host $param2

#$noRuns = $args[3] # No of runs
$noRuns = 10

# Executare class Java

$suma = 0

for ($i = 0; $i -lt $noRuns; $i++){
    Write-Host "Rulare" ($i+1)
    $a = java $javaFile $matrixNumber $noThreads # rulare class java
    Write-Host $a
    $suma += $a
    Write-Host ""
}
$media = $suma / $i
#Write-Host $suma
Write-Host "Timp de executie mediu:" $media

# Creare fisier .csv
if (!(Test-Path outJ.csv)){
    New-Item outJ.csv -ItemType File
    #Scrie date in csv
    Set-Content outJ.csv 'Tip Matrice,Nr threads,Timp executie'
}

# Append
Add-Content outJ.csv "N=10000,M=10,n=m=5,$($noThreads),$($media)"