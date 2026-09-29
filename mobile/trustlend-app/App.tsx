import React, { useState } from "react";
import { SafeAreaView, StyleSheet, Text, TextInput, View, Pressable, ActivityIndicator, ScrollView } from "react-native";
import { acceptAgreement, createLoan, getApiBaseUrl, getLoan, Loan } from "./src/api";

type Screen = "home" | "create" | "details";

const DEMO_LENDER_ID = "00000000-0000-0000-0000-000000000001";
const DEMO_BORROWER_ID = "00000000-0000-0000-0000-000000000002";

export default function App() {
  const [screen, setScreen] = useState<Screen>("home");
  const [loan, setLoan] = useState<Loan | null>(null);
  const [principal, setPrincipal] = useState("20000");
  const [apr, setApr] = useState("0");
  const [interestMethod, setInterestMethod] = useState<"NONE" | "SIMPLE">("NONE");
  const [maturityDate, setMaturityDate] = useState("2027-02-01");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  async function handleCreateLoan() {
    setLoading(true);
    setMessage("");
    try {
      const created = await createLoan({
        lenderId: DEMO_LENDER_ID,
        borrowerId: DEMO_BORROWER_ID,
        principal,
        apr,
        interestMethod,
        startDate: new Date().toISOString().slice(0, 10),
        maturityDate,
      });
      setLoan(created);
      setScreen("details");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Unable to create loan");
    } finally {
      setLoading(false);
    }
  }

  async function handleAccept() {
    if (!loan) return;
    setLoading(true);
    setMessage("");
    try {
      await acceptAgreement(loan.id, DEMO_BORROWER_ID);
      setLoan(await getLoan(loan.id));
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Unable to accept agreement");
    } finally {
      setLoading(false);
    }
  }

  if (screen === "create") {
    return (
      <SafeAreaView style={styles.container}>
        <ScrollView contentContainerStyle={styles.content}>
          <Pressable onPress={() => setScreen("home")}><Text style={styles.back}>‹ Back</Text></Pressable>
          <Text style={styles.eyebrow}>CREATE LOAN</Text>
          <Text style={styles.title}>Set clear terms.</Text>
          <Text style={styles.subtitle}>The borrower will review and explicitly accept the agreement.</Text>

          <Field label="Principal (₹)" value={principal} onChangeText={setPrincipal} keyboardType="decimal-pad" />
          <Field label="APR (%)" value={apr} onChangeText={(value) => {
            setApr(value);
            setInterestMethod(value === "0" ? "NONE" : "SIMPLE");
          }} keyboardType="decimal-pad" />
          <Field label="Maturity date" value={maturityDate} onChangeText={setMaturityDate} />

          <View style={styles.card}>
            <Text style={styles.cardTitle}>Interest</Text>
            <Text style={styles.option}>{interestMethod === "NONE" ? "0% — interest-free" : `${apr}% APR — simple interest (MVP)`}</Text>
            <Text style={styles.hint}>MVP product policy currently allows APR from 0% to 15%. This is a product-policy limit, not a statement of law.</Text>
          </View>

          {message ? <Text style={styles.error}>{message}</Text> : null}
          <Pressable style={styles.button} onPress={handleCreateLoan} disabled={loading}>
            {loading ? <ActivityIndicator color="#FFFFFF" /> : <Text style={styles.buttonText}>Create agreement</Text>}
          </Pressable>
        </ScrollView>
      </SafeAreaView>
    );
  }

  if (screen === "details" && loan) {
    return (
      <SafeAreaView style={styles.container}>
        <ScrollView contentContainerStyle={styles.content}>
          <Pressable onPress={() => setScreen("home")}><Text style={styles.back}>‹ Home</Text></Pressable>
          <Text style={styles.eyebrow}>LOAN DETAILS</Text>
          <Text style={styles.title}>₹{loan.principal}</Text>
          <Text style={styles.subtitle}>{loan.interestMethod === "NONE" ? "Interest-free" : `${loan.apr}% APR • simple interest`}</Text>

          <View style={styles.card}>
            <Row label="Status" value={loan.status} />
            <Row label="Start" value={loan.startDate} />
            <Row label="Maturity" value={loan.maturityDate} />
            <Row label="Agreement" value={`v${loan.agreementVersion}`} />
            <Row label="Policy" value={loan.productPolicyVersion} />
          </View>

          {loan.status === "PENDING_BORROWER_ACCEPTANCE" ? (
            <Pressable style={styles.button} onPress={handleAccept} disabled={loading}>
              {loading ? <ActivityIndicator color="#FFFFFF" /> : <Text style={styles.buttonText}>Accept as borrower</Text>}
            </Pressable>
          ) : null}

          {message ? <Text style={styles.error}>{message}</Text> : null}
          <Text style={styles.note}>Demo identity is used in this MVP shell. Production authentication will replace it.</Text>
        </ScrollView>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.eyebrow}>TRUSTLEND</Text>
        <Text style={styles.title}>Lend with clarity.</Text>
        <Text style={styles.subtitle}>Formalize a trusted-person loan with clear terms, repayment tracking, and settlement.</Text>
        <View style={styles.card}>
          <Text style={styles.cardTitle}>MVP financial flow</Text>
          <Text style={styles.flow}>Agreement → Acceptance → Repayment → Settlement</Text>
          <Pressable style={styles.button} onPress={() => { setMessage(""); setScreen("create"); }}>
            <Text style={styles.buttonText}>Create a loan</Text>
          </Pressable>
        </View>
        <Text style={styles.note}>API: {getApiBaseUrl()}</Text>
        <Text style={styles.note}>TrustLend does not calculate a trust score. It records factual repayment activity.</Text>
      </View>
    </SafeAreaView>
  );
}

function Field(props: { label: string; value: string; onChangeText: (value: string) => void; keyboardType?: "default" | "decimal-pad" }) {
  return (
    <View style={styles.field}>
      <Text style={styles.label}>{props.label}</Text>
      <TextInput style={styles.input} value={props.value} onChangeText={props.onChangeText} keyboardType={props.keyboardType} />
    </View>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return <View style={styles.row}><Text style={styles.label}>{label}</Text><Text style={styles.value}>{value}</Text></View>;
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#F7F8FA" },
  content: { flexGrow: 1, padding: 24, justifyContent: "center" },
  eyebrow: { fontSize: 12, fontWeight: "700", letterSpacing: 2 },
  title: { fontSize: 34, fontWeight: "800", marginTop: 10 },
  subtitle: { fontSize: 17, lineHeight: 25, marginTop: 12, color: "#4B5563" },
  card: { backgroundColor: "#FFFFFF", borderRadius: 18, padding: 20, marginTop: 28 },
  cardTitle: { fontSize: 18, fontWeight: "700" },
  flow: { marginTop: 12, lineHeight: 22, color: "#374151" },
  field: { marginTop: 20 },
  label: { fontSize: 13, color: "#6B7280", fontWeight: "600" },
  input: { marginTop: 7, backgroundColor: "#FFFFFF", borderRadius: 12, padding: 14, fontSize: 16 },
  option: { marginTop: 10, fontSize: 15, fontWeight: "600" },
  hint: { marginTop: 8, color: "#6B7280", lineHeight: 19 },
  row: { flexDirection: "row", justifyContent: "space-between", paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: "#EEF0F3" },
  value: { fontSize: 14, fontWeight: "600", maxWidth: "60%", textAlign: "right" },
  button: { marginTop: 20, padding: 15, borderRadius: 12, backgroundColor: "#111827" },
  buttonText: { color: "#FFFFFF", textAlign: "center", fontWeight: "700" },
  back: { fontSize: 16, fontWeight: "700", marginBottom: 20 },
  error: { marginTop: 16, color: "#B91C1C", lineHeight: 20 },
  note: { marginTop: 20, fontSize: 13, lineHeight: 19, color: "#6B7280" }
});
