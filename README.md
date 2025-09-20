# Package Design Check (PDC)

**Ein OOP-konformes Tool zur Validierung von Paketstrukturen in Java-Projekten**

---

## **1. Fachliche Anforderungen**

### **1.1 Priorisierung der Fehler**
| Priorität | Fehlerart                     | Beispiel                                  |
|-----------|-------------------------------|-------------------------------------------|
| 🚨 KRITISCH | Zirkuläre Abhängigkeiten      | `A → B → A`                              |
| 🔴 HOCH     | Fehlende Domain-Pakete        | `Bill.java` ohne `com.example.billing.bill/` |
| 🔴 HOCH     | Pluralformen in Abstraktionen | `Rules.java` statt `Rule.java`           |
| 🟠 MITTEL   | Falsche Paketnamen           | `com.example.billing.Rules/` statt `rule/` |
| 🟠 MITTEL   | Leere Pakete                 | `com.example.billing.empty/` ohne Klassen |

---

### **1.2 Zyklenerkennung**
- **Direkte Zyklen**: `A → B → A`
- **Indirekte Zyklen**: `A → B → C → A`
- **Lösungsvorschläge pro Zyklus**:
  1. Extrahiere gemeinsame Schnittstelle
  2. Nutze Dependency Injection
  3. Restrukturiere Pakete nach Domain-Kontexten

---

## **2. Implementierungsanforderungen**

### **2.1 OOP-Namenskonventionen**
- **Keine "-er" Suffixes** (z. B. nicht `Validator`, sondern `Validation`)
- **Keine statischen Methoden** (Verhalten in Objekten gekapselt)
- **Immutable Objects** (Domain-Objekte sind unveränderlich)
- **Keine "Client"-Suffixes** (z. B. nicht `PackageClient`, sondern `Package`)

**Quellen**:
- [Seven Virtues of Good Objects](https://www.yegor256.com/2014/11/20/seven-virtues-of-good-object.html)
- [Objects End With "er"](https://www.yegor256.com/2015/03/09/objects-end-with-er.html)

---

### **2.2 Dependency Injection ohne Container**
- **Manuelle DI** (keine Frameworks wie Spring)
- **Composable Decorators** für erweiterbare Funktionalität
- **Vertikale Dekoration** für Domain-Logik
- **Horizontale Dekoration** für technische Aspekte

**Quellen**:
- [DI Containers Are Evil](https://www.yegor256.com/2014/10/03/di-containers-are-evil.html)
- [Composable Decorators](https://www.yegor256.com/2015/02/26/composable-decorators.html)

---

### **2.3 Paketstruktur-Regeln**

#### **2.3.1 Paketentstehung**
Pakete entstehen **ausschließlich** auf Ebene gleichnamiger Abstraktionen:

