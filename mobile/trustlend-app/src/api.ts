export type Loan = {
  id: string;
  lenderId: string;
  borrowerId: string;
  principal: string;
  apr: string;
  interestMethod: string;
  startDate: string;
  maturityDate: string;
  status: string;
  agreementVersion: number;
  productPolicyVersion: string;
  createdAt: string;
};

const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL ?? "http://localhost:8080";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json", ...(options?.headers ?? {}) },
    ...options,
  });

  const body = await response.text();
  if (!response.ok) {
    throw new Error(body || `Request failed with HTTP ${response.status}`);
  }

  return body ? (JSON.parse(body) as T) : (undefined as T);
}

export function createLoan(input: {
  lenderId: string;
  borrowerId: string;
  principal: string;
  apr: string;
  interestMethod: "NONE" | "SIMPLE";
  startDate: string;
  maturityDate: string;
}) {
  return request<Loan>("/api/v1/loans", {
    method: "POST",
    body: JSON.stringify(input),
  });
}

export function getLoan(loanId: string) {
  return request<Loan>(`/api/v1/loans/${loanId}`);
}

export function acceptAgreement(loanId: string, actorId: string) {
  return request<unknown>(`/api/v1/loans/${loanId}/agreement/accept`, {
    method: "POST",
    body: JSON.stringify({ actorId }),
  });
}

export function getApiBaseUrl() {
  return API_BASE_URL;
}
