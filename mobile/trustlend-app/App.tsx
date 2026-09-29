import React, { useState } from "react";
import { SafeAreaView, StyleSheet, Text, TextInput, View, Pressable, ActivityIndicator, ScrollView } from "react-native";
import {
  acceptAgreement, createLoan, createSchedule, getApiBaseUrl, getLoan, getSchedule,
  getPayments, getSettlement, recordPayment, settleLoan, Loan, ScheduleItem, Settlement
} from "./src/api";

type Screen = "home" | "create" | "details" | "schedule" | "payment" | "settlement";
const DEMO_LENDER_ID = "00000000-0000-0000-0000-000000000001";
const DEMO_BORROWER_ID = "00000000-0000-0000-0000-000000000002";

export default function App() {
  const [screen, setScreen] = useState<Screen>("home");
  const [loan, setLoan] = useState<Loan | null>(null);
  const [schedule, setSchedule] = useState<ScheduleItem[]>([]);
  const [settlement, setSettlement] = useState<Settlement | null>(null);
  const [principal, setPrincipal] = useState("20000");
  const [apr, setApr] = useState("0");
  const [interestMethod, setInterestMethod] = useState<"NONE" | "SIMPLE">("NONE");
  const [maturityDate, setMaturityDate] = useState("2027-02-01");
  const [paymentAmount, setPaymentAmount] = useState("5000");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const run = async (action: () => Promise<void>) => {
    setLoading(true); setMessage("");
    try { await action(); } catch (e) { setMessage(e instanceof Error ? e.message : "Request failed"); }
    finally { setLoading(false); }
  };

  async function handleCreateLoan() {
    await run(async () => {
      const created = await createLoan({
        lenderId: DEMO_LENDER_ID, borrowerId: DEMO_BORROWER_ID, principal, apr, interestMethod,
        startDate: new Date().toISOString().slice(0, 10), maturityDate,
      });
      setLoan(created); setScreen("details");
    });
  }

  async function handleAccept() {
    if (!loan) return;
    await run(async () => { await acceptAgreement(loan.id, DEMO_BORROWER_ID); setLoan(await getLoan(loan.id)); });
  }

  async function handleSchedule() {
    if (!loan) return;
    await run(async () => { setSchedule(await getSchedule(loan.id)); setScreen("schedule"); });
  }

  async function handleCreateSchedule() {
    if (!loan) return;
    await run(async () => {
      const createdSchedule = await createSchedule(loan.id, 4);
      setSchedule(createdSchedule);
      setLoan(await getLoan(loan.id));
      setScreen("schedule");
    });
  }

  async function handlePayment() {
    if (!loan) return;
    await run(async () => {
      const stamp = Date.now();
      await recordPayment(loan.id, paymentAmount, `demo-${stamp}`, `mock-provider-${stamp}`);
      setLoan(await getLoan(loan.id)); setSchedule(await getSchedule(loan.id)); setScreen("schedule");
    });
  }

  async function handleSettlement() {
    if (!loan) return;
    await run(async () => { setSettlement(await settleLoan(loan.id)); setLoan(await getLoan(loan.id)); setScreen("settlement"); });
  }

  async function showExistingSettlement() {
    if (!loan) return;
    await run(async () => { setSettlement(await getSettlement(loan.id)); setScreen("settlement"); });
  }

  if (screen === "create") return <Page onBack={() => setScreen("home")}>
    <Text style={styles.eyebrow}>CREATE LOAN</Text><Text style={styles.title}>Set clear terms.</Text>
    <Text style={styles.subtitle}>The borrower will review and explicitly accept the agreement.</Text>
    <Field label="Principal (₹)" value={principal} onChangeText={setPrincipal} keyboardType="decimal-pad" />
    <Field label="APR (%)" value={apr} onChangeText={v => { setApr(v); setInterestMethod(v === "0" ? "NONE" : "SIMPLE"); }} keyboardType="decimal-pad" />
    <Field label="Maturity date" value={maturityDate} onChangeText={setMaturityDate} />
    <View style={styles.card}><Text style={styles.cardTitle}>Interest</Text>
      <Text style={styles.option}>{interestMethod === "NONE" ? "0% — interest-free" : `${apr}% APR — simple interest (MVP)`}</Text>
      <Text style={styles.hint}>Current MVP product policy: 0%–15% APR. This is a product-policy limit, not a statement of law.</Text>
    </View>
    <Action label="Create agreement" onPress={handleCreateLoan} loading={loading} />
    {message ? <Text style={styles.error}>{message}</Text> : null}
  </Page>;

  if (screen === "details" && loan) return <Page onBack={() => setScreen("home")}>
    <Text style={styles.eyebrow}>LOAN DETAILS</Text><Text style={styles.title}>₹{loan.principal}</Text>
    <Text style={styles.subtitle}>{loan.interestMethod === "NONE" ? "Interest-free" : `${loan.apr}% APR • simple interest`}</Text>
    <View style={styles.card}><Row label="Status" value={loan.status}/><Row label="Start" value={loan.startDate}/><Row label="Maturity" value={loan.maturityDate}/><Row label="Agreement" value={`v${loan.agreementVersion}`}/><Row label="Policy" value={loan.productPolicyVersion}/></View>
    {loan.status === "PENDING_BORROWER_ACCEPTANCE" ? <Action label="Accept as borrower" onPress={handleAccept} loading={loading}/> : null}
    {loan.status === "ACCEPTED" ? <Action label="Create repayment schedule" onPress={handleCreateSchedule} loading={loading}/> : null}
    {["ACTIVE","PARTIALLY_PAID","DUE","OVERDUE"].includes(loan.status) ? <>
      <Action label="View repayment schedule" onPress={handleSchedule} loading={loading}/>
      <Action label="Record mock payment" onPress={() => setScreen("payment")} loading={false}/>
      <Action label="Generate settlement" onPress={handleSettlement} loading={loading}/>
    </> : null}
    {loan.status === "SETTLED" ? <Action label="View settlement statement" onPress={showExistingSettlement} loading={loading}/> : null}
    {message ? <Text style={styles.error}>{message}</Text> : null}
    <Text style={styles.note}>Demo identities and mock payment references are used only for MVP development.</Text>
  </Page>;

  if (screen === "schedule" && loan) return <Page onBack={() => setScreen("details")}>
    <Text style={styles.eyebrow}>REPAYMENT SCHEDULE</Text><Text style={styles.title}>Loan ₹{loan.principal}</Text>
    {schedule.length === 0 ? <Text style={styles.subtitle}>No schedule loaded yet.</Text> : schedule.map((item, i) =>
      <View style={styles.card} key={item.id}><Text style={styles.cardTitle}>Installment {i + 1}</Text>
        <Row label="Due" value={item.dueDate}/><Row label="Total" value={`₹${item.totalDue}`}/><Row label="Paid" value={`₹${Number(item.principalPaid) + Number(item.interestPaid)}`}/><Row label="Outstanding" value={`₹${item.outstanding}`}/><Row label="Status" value={item.status}/>
      </View>)}
    <Action label="Record payment" onPress={() => setScreen("payment")} loading={false}/>
    {message ? <Text style={styles.error}>{message}</Text> : null}
  </Page>;

  if (screen === "payment" && loan) return <Page onBack={() => setScreen("schedule")}>
    <Text style={styles.eyebrow}>PAYMENT</Text><Text style={styles.title}>Record repayment</Text>
    <Text style={styles.subtitle}>This MVP uses a mock provider reference. Production payment reconciliation will sit behind an adapter.</Text>
    <Field label="Amount (₹)" value={paymentAmount} onChangeText={setPaymentAmount} keyboardType="decimal-pad"/>
    <Action label="Record payment" onPress={handlePayment} loading={loading}/>
    {message ? <Text style={styles.error}>{message}</Text> : null}
  </Page>;

  if (screen === "settlement" && settlement) return <Page onBack={() => setScreen("details")}>
    <Text style={styles.eyebrow}>SETTLEMENT STATEMENT</Text><Text style={styles.title}>Settled</Text>
    <View style={styles.card}><Row label="Principal" value={`₹${settlement.originalPrincipal}`}/><Row label="Interest" value={`₹${settlement.totalInterest}`}/><Row label="Total obligation" value={`₹${settlement.totalObligation}`}/><Row label="Total paid" value={`₹${settlement.totalPaid}`}/><Row label="Outstanding" value={`₹${settlement.outstanding}`}/><Row label="Date" value={settlement.settlementDate.slice(0,10)}/><Row label="Status" value={settlement.status}/></View>
    {message ? <Text style={styles.error}>{message}</Text> : null}
  </Page>;

  return <SafeAreaView style={styles.container}><View style={styles.content}>
    <Text style={styles.eyebrow}>TRUSTLEND</Text><Text style={styles.title}>Lend with clarity.</Text>
    <Text style={styles.subtitle}>Formalize a trusted-person loan with clear terms, repayment tracking, and settlement.</Text>
    <View style={styles.card}><Text style={styles.cardTitle}>MVP financial flow</Text><Text style={styles.flow}>Agreement → Acceptance → Repayment → Settlement</Text><Action label="Create a loan" onPress={() => {setMessage(""); setScreen("create");}} loading={false}/></View>
    <Text style={styles.note}>API: {getApiBaseUrl()}</Text><Text style={styles.note}>TrustLend does not calculate a trust score. It records factual repayment activity.</Text>
  </View></SafeAreaView>;
}

function Page({children,onBack}:{children:React.ReactNode;onBack:()=>void}) {
  return <SafeAreaView style={styles.container}><ScrollView contentContainerStyle={styles.content}><Pressable onPress={onBack}><Text style={styles.back}>‹ Back</Text></Pressable>{children}</ScrollView></SafeAreaView>;
}
function Action({label,onPress,loading}:{label:string;onPress:()=>void;loading:boolean}) {
  return <Pressable style={styles.button} onPress={onPress} disabled={loading}>{loading ? <ActivityIndicator color="#FFFFFF"/> : <Text style={styles.buttonText}>{label}</Text>}</Pressable>;
}
function Field({label,value,onChangeText,keyboardType}:{label:string;value:string;onChangeText:(v:string)=>void;keyboardType?:"default"|"decimal-pad"}) {
  return <View style={styles.field}><Text style={styles.label}>{label}</Text><TextInput style={styles.input} value={value} onChangeText={onChangeText} keyboardType={keyboardType}/></View>;
}
function Row({label,value}:{label:string;value:string}) { return <View style={styles.row}><Text style={styles.label}>{label}</Text><Text style={styles.value}>{value}</Text></View>; }

const styles=StyleSheet.create({
  container:{flex:1,backgroundColor:"#F7F8FA"}, content:{flexGrow:1,padding:24,justifyContent:"center"},
  eyebrow:{fontSize:12,fontWeight:"700",letterSpacing:2}, title:{fontSize:34,fontWeight:"800",marginTop:10},
  subtitle:{fontSize:17,lineHeight:25,marginTop:12,color:"#4B5563"}, card:{backgroundColor:"#FFFFFF",borderRadius:18,padding:20,marginTop:20},
  cardTitle:{fontSize:18,fontWeight:"700"}, flow:{marginTop:12,lineHeight:22,color:"#374151"}, field:{marginTop:20},
  label:{fontSize:13,color:"#6B7280",fontWeight:"600"}, input:{marginTop:7,backgroundColor:"#FFFFFF",borderRadius:12,padding:14,fontSize:16},
  option:{marginTop:10,fontSize:15,fontWeight:"600"}, hint:{marginTop:8,color:"#6B7280",lineHeight:19},
  row:{flexDirection:"row",justifyContent:"space-between",paddingVertical:10,borderBottomWidth:1,borderBottomColor:"#EEF0F3"},
  value:{fontSize:14,fontWeight:"600",maxWidth:"60%",textAlign:"right"}, button:{marginTop:16,padding:15,borderRadius:12,backgroundColor:"#111827"},
  buttonText:{color:"#FFFFFF",textAlign:"center",fontWeight:"700"}, back:{fontSize:16,fontWeight:"700",marginBottom:20},
  error:{marginTop:16,color:"#B91C1C",lineHeight:20}, note:{marginTop:20,fontSize:13,lineHeight:19,color:"#6B7280"}
});
