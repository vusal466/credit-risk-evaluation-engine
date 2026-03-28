$body = @{
    customerId = 123456
    customerName = "John Doe"
    requestedAmount = 10000.00
    loanPurpose = "HOME"
    loanTermMonths = 24
    monthlyIncome = 2500.00
    employmentType = "SALARIED"
    creditScore = 720
    existingDebtAmount = 500.00
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/loans" -Method Post -ContentType "application/json" -Body $body
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.ErrorDetails) {
        Write-Host "Details: $($_.ErrorDetails.Message)"
    }
}
