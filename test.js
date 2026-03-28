const http = require('http');

const data = JSON.stringify({
  customerId: 123456,
  customerName: "John Doe",
  requestedAmount: 10000.00,
  loanPurpose: "HOME",
  loanTermMonths: 24,
  monthlyIncome: 2500.00,
  employmentType: "SALARIED",
  creditScore: 720,
  existingDebtAmount: 500.00
});

const options = {
  hostname: 'localhost',
  port: 8081,
  path: '/api/loans',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length
  }
};

const req = http.request(options, res => {
  console.log(`statusCode: ${res.statusCode}`);
  res.on('data', d => {
    process.stdout.write(d);
  });
});

req.on('error', error => {
  console.error(error);
});

req.write(data);
req.end();
