# setup-git.ps1 - money 프로젝트 GitHub 업로드 스크립트
# 실행: 이 파일이 있는 폴더에서 우클릭 > "PowerShell로 실행"
# 또는 PowerShell에서: cd C:\Users\User\IdeaProjects\Money; .\setup-git.ps1

Set-Location $PSScriptRoot

Write-Host "=== 기존 .git 폴더 정리 ===" -ForegroundColor Cyan
if (Test-Path ".git") {
    Remove-Item -Recurse -Force ".git"
    Write-Host "기존 .git 폴더 삭제 완료" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== git init (main 브랜치) ===" -ForegroundColor Cyan
git init -b main

Write-Host ""
Write-Host "=== remote 추가 ===" -ForegroundColor Cyan
git remote add origin https://github.com/kjh9211/money.git

Write-Host ""
Write-Host "=== git add . ===" -ForegroundColor Cyan
git add .
git status

Write-Host ""
Write-Host "=== git commit ===" -ForegroundColor Cyan
git commit -m "Initial commit"

Write-Host ""
Write-Host "=== GitHub push ===" -ForegroundColor Cyan
git push -u origin main

Write-Host ""
Write-Host "=== 완료! ===" -ForegroundColor Green
Write-Host "https://github.com/kjh9211/money 에서 확인하세요."

# 스크립트 종료 후 창이 바로 닫히지 않도록
Write-Host ""
Write-Host "아무 키나 누르면 종료됩니다..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
