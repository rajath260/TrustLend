export type Loan = {
  id: string; lenderId: string; borrowerId: string; principal: string; apr: string;
  interestMethod: string; startDate: string; maturityDate: string; status: string;
  agreementVersion: number; productPolicyVersion: string; createdAt: string;
};

export type ScheduleItem = {
  id: string; loanId: string; dueDate: string; principalDue: string; interestDue: string;
  totalDue: string; principalPaid: string; interestPaid: string; outstanding: string; status: string;
};

export type Payment = {
  id: string; loanId: string; amount: string; idempotencyKey: string;
  providerReference: string; status: string; createdAt: string;
};

export type Settlement = {
  id: string; loanId: string; originalPrincipal: string; totalInterest: string;
  totalObligation: string; totalPaid: string; outstanding: string;
  status: string; settlementDate: string;
};

export type RepaymentRecord = {
  userId: string;
  lendingActivity: RepaymentActivity;
  borrowingActivity: RepaymentActivity;
};

export type RepaymentActivity = {
  loanCount: number;
  settledLoanCount: number;
  principalAmount: string;
  principalRepaid: string;
  principalOutstanding: string;
};

const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL ?? "http://localhost:8080";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json", ...(options?.headers ?? {}) }, ...options,
  });
  const body = await response.text();
  if (!response.ok) throw new Error(body || `Request failed with HTTP ${response.status}`);
  return body ? (JSON.parse(body) as T) : (undefined as T);
}

export function createLoan(input: {
  lenderId: string; borrowerId: string; principal: string; apr: string;
  interestMethod: "NONE" | "SIMPLE"; startDate: string; maturityDate: string;
}) {
  return request<Loan>("/api/v1/loans", { method: "POST", body: JSON.stringify(input) });
}
export function getLoan(loanId: string) { return request<Loan>(`/api/v1/loans/${loanId}`); }
export function acceptAgreement(loanId: string, actorId: string) {
  return request<unknown>(`/api/v1/loans/${loanId}/agreement/accept`, {
    method: "POST", body: JSON.stringify({ actorId }),
  });
}
export function createSchedule(loanId: string, installments = 4) {
  return request<ScheduleItem[]>(`/api/v1/loans/${loanId}/schedule?installments=${installments}`, { method: "POST" });
}
export function getSchedule(loanId: string) {
  return request<ScheduleItem[]>(`/api/v1/loans/${loanId}/schedule`);
}
export function recordPayment(loanId: string, amount: string, idempotencyKey: string, providerReference: string) {
  return request<Payment>(`/api/v1/loans/${loanId}/payments`, {
    method: "POST", body: JSON.stringify({ amount, idempotencyKey, providerReference }),
  });
}
export function getPayments(loanId: string) {
  return request<Payment[]>(`/api/v1/loans/${loanId}/payments`);
}
export function settleLoan(loanId: string) {
  return request<Settlement>(`/api/v1/loans/${loanId}/settlement`, { method: "POST" });
}
export function getSettlement(loanId: string) {
  return request<Settlement>(`/api/v1/loans/${loanId}/settlement`);
}
export function getRepaymentRecord(userId: string) {
  return request<RepaymentRecord>(`/api/v1/users/${userId}/repayment-record`);
}
export function getApiBaseUrl() { return API_BASE_URL; }
