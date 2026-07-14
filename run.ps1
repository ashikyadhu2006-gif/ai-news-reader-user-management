$projectDir = $PSScriptRoot
Set-Location $projectDir

$mvnCmd = "mvn"
try {
    Get-Command mvn -ErrorAction Stop | Out-Null
    Write-Host "Found global Maven installation." -ForegroundColor Green
} catch {
    $localMavenDir = Join-Path $projectDir ".maven"
    $mvnCmd = Join-Path $localMavenDir "apache-maven-3.9.6\bin\mvn.cmd"
    
    if (-not (Test-Path $mvnCmd)) {
        Write-Host "Maven not found globally. Downloading portable Apache Maven 3.9.6..." -ForegroundColor Cyan
        if (-not (Test-Path $localMavenDir)) {
            New-Item -ItemType Directory -Path $localMavenDir | Out-Null
        }
        $zipPath = Join-Path $localMavenDir "maven.zip"
        
        $url = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
        Invoke-WebRequest -Uri $url -OutFile $zipPath
        
        Write-Host "Extracting Maven package..." -ForegroundColor Cyan
        Expand-Archive -Path $zipPath -DestinationPath $localMavenDir -Force
        Remove-Item $zipPath
    } else {
        Write-Host "Found local Maven at $mvnCmd" -ForegroundColor Green
    }
}

Write-Host "Executing: mvn clean javafx:run" -ForegroundColor Green
& $mvnCmd clean javafx:run
