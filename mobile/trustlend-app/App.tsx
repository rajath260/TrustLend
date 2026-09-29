import React from "react";
import { SafeAreaView, StyleSheet, Text, View, Pressable } from "react-native";

export default function App() {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.eyebrow}>TRUSTLEND</Text>
        <Text style={styles.title}>Lend with clarity.</Text>
        <Text style={styles.subtitle}>
          Formalize a trusted-person loan with clear terms, repayment tracking,
          and settlement.
        </Text>

        <View style={styles.card}>
          <Text style={styles.cardTitle}>MVP financial flow</Text>
          <Text style={styles.flow}>Agreement → Acceptance → Repayment → Settlement</Text>
          <Pressable style={styles.button}>
            <Text style={styles.buttonText}>Create a loan</Text>
          </Pressable>
        </View>

        <Text style={styles.note}>
          TrustLend does not calculate a trust score. It records factual
          repayment activity.
        </Text>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#F7F8FA" },
  content: { flex: 1, padding: 24, justifyContent: "center" },
  eyebrow: { fontSize: 12, fontWeight: "700", letterSpacing: 2 },
  title: { fontSize: 34, fontWeight: "800", marginTop: 10 },
  subtitle: { fontSize: 17, lineHeight: 25, marginTop: 12, color: "#4B5563" },
  card: { backgroundColor: "#FFFFFF", borderRadius: 18, padding: 20, marginTop: 28 },
  cardTitle: { fontSize: 18, fontWeight: "700" },
  flow: { marginTop: 12, lineHeight: 22, color: "#374151" },
  button: { marginTop: 20, padding: 15, borderRadius: 12, backgroundColor: "#111827" },
  buttonText: { color: "#FFFFFF", textAlign: "center", fontWeight: "700" },
  note: { marginTop: 20, fontSize: 13, lineHeight: 19, color: "#6B7280" }
});
