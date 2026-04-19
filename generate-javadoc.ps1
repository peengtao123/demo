# Maven Javadoc Generation Script with Chinese Support
# Usage: Run from project root directory: .\generate-javadoc.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Maven Javadoc Generator (Chinese)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Set UTF-8 encoding environment variables
Write-Host "[1/4] Setting UTF-8 encoding..." -ForegroundColor Yellow
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8 -Duser.language=zh -Duser.country=CN"
Write-Host "  Done: JAVA_TOOL_OPTIONS and MAVEN_OPTS set" -ForegroundColor Green
Write-Host ""

# Step 2: Clean previous build
Write-Host "[2/4] Cleaning previous build..." -ForegroundColor Yellow
mvn clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "  Error: Clean failed!" -ForegroundColor Red
    exit 1
}
Write-Host "  Done: Clean completed" -ForegroundColor Green
Write-Host ""

# Step 3: Compile project
Write-Host "[3/4] Compiling project..." -ForegroundColor Yellow
mvn compile -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "  Error: Compilation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "  Done: Compilation completed" -ForegroundColor Green
Write-Host ""

# Step 4: Generate Javadoc via site lifecycle
Write-Host "[4/4] Generating Javadoc documentation..." -ForegroundColor Yellow
mvn site -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "  Warning: Some warnings occurred (possibly due to Chinese path)" -ForegroundColor Yellow
} else {
    Write-Host "  Done: Generation completed" -ForegroundColor Green
}
Write-Host ""

# Check output file
$javadocPath = ".\target\site\apidocs\index.html"
if (Test-Path $javadocPath) {
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  SUCCESS: Javadoc generated!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Location: $javadocPath" -ForegroundColor Cyan
    Write-Host ""
    
    # Ask to open browser
    $openBrowser = Read-Host "Open in browser? (Y/N)"
    if ($openBrowser -eq "Y" -or $openBrowser -eq "y") {
        Start-Process $javadocPath
        Write-Host "  Browser opened" -ForegroundColor Green
    }
} else {
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  FAILED: Javadoc generation failed" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Possible reasons:" -ForegroundColor Yellow
    Write-Host "  1. Project path contains Chinese characters" -ForegroundColor White
    Write-Host "     Current path: $(Get-Location)" -ForegroundColor White
    Write-Host "  2. Recommendation: Move project to English-only path" -ForegroundColor White
    Write-Host "     Example: D:\projects\demo" -ForegroundColor White
    Write-Host ""
    Write-Host "Alternative solutions:" -ForegroundColor Yellow
    Write-Host "  - Use IDE Javadoc generation (VS Code, IntelliJ IDEA)" -ForegroundColor White
    Write-Host "  - View other Site reports: target\site\index.html" -ForegroundColor White
}

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
