$javaFile = $args[0] # Nume fisier java

$noRuns = $args[1] # No of runs

$caz = $args[2]

$noThreads = $args[3] # No of threads
$noReaderThreads = $args[4]


# Executare class Java

$suma = 0

for ($i = 0; $i -lt $noRuns; $i++){
    Write-Host "Rulare" ($i+1)
    $a = java $javaFile $caz $noThreads $noReaderThreads # rulare class java
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
    Set-Content outJ.csv 'Caz,Nr threads,Timp executie'
}

# Append
Add-Content outJ.csv "nrPolinoame=5,nrMonoame=100,gr=10000,$($noThreads),$($noReaderThreads),$($media)"