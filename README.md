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
### 2.3 Paketstruktur-Regeln
---
### 2.3.1 Formale Definition der Paketstruktur
*Notation:*
- b[0]: Root-Namespace (Wurzel, immer Ebene 0)
Beispiel: com.example.pdc
- p[n]: Paket auf Ebene n (n ≥ 1)
Beispiel: com.example.pdc/rule/ (Ebene 1)
- a[n]: Abstraktion auf Ebene n (n ≥ 1)
Beispiel: com.example.pdc.Rule (Ebene 1)
- i[n]: Implementierung auf Ebene n (n ≥ 1)
Beispiel: com.example.pdc.rule.PackageRule (Ebene 1)
---
### 2.3.1 Paketstruktur Example
```
com.example.pdc/                # b[0] (Root-Namespace, Ebene 0)
├── App.java                    # a[1] (Abstraktion)
├── Rule.java                   # a[1] (Abstraktion)
├── app/                        # p[1] (Paket für App-Realisierungen)
│   ├── PDCApp.java             # i[1] (Implementierung von App)
│   └── ConsolePDCApp.java      # i[1] (Implementierung von App)
└── rule/                       # p[1] (Paket für Rule-Realisierungen)
    ├── PackageRule.java        # i[1] (Implementierung von Rule)
    ├── SubRule.java            # a[2] (Abstraktion)
    ├── subrule/                # p[2] (Paket für SubRule-Realisierungen)
    │   └── MySubRule.java      # i[2] (Implementierung von SubRule)
    └── CycleRule.java          # i[1] (Implementierung von Rule)

```

#### 2.3.2. Formale Regeln

**Regel 1:Paketentstehung** 
Pakete entstehen nur auf der Ebene von Abstraktionen und nur dann, wenn eine gleichnamige Abstraktion existiert:
```
∀n≥1:∃p[n]  ⟺  ∃a[n]∈b[k], wobei k=n−1\forall n \geq 1: \exists p[n] \iff \exists a[n] \in b[k], \text{ wobei } k = n-1∀n≥1:∃p[n]⟺∃a[n]∈b[k], wobei k=n−1
```
- b[0]: Root-Namespace (immer Ebene 0)
- a[n]: Abstraktion auf Ebene n (Interface oder abstrakte Klasse)
- p[n]: Paket auf Ebene n für Realisierungen von a[n]
```
**Regel 2: Namenskonventionen**
Der Paketname muss dem Namen der korrespondierenden Abstraktion entsprechen:
```
∀n≥1:Name(p[n])=Name(a[n]).toLowerCase()\forall n \geq 1: \text{Name}(p[n]) = \text{Name}(a[n]).\text{toLowerCase()}∀n≥1:Name(p[n])=Name(a[n]).toLowerCase()
```
**Beispiele:**
- Abstraktion (a[n])           --> Paket (p[n])
- com.example.pdc.Rule         --> com.example.pdc/rule/
- com.example.pdc.rule.SubRule --> com.example.pdc/rule/subrule/
---
**Regel 3: Paketinhalte**
Pakete dürfen nur Realisierungen ihrer gleichnamigen Abstraktion enthalten:
```
∀n≥1:∀i[n]∈p[n]:i[n]⪯a[n]\forall n \geq 1: \forall i[n] \in p[n]: i[n] \preceq a[n]∀n≥1:∀i[n]∈p[n]:i[n]⪯a[n]
```
- i[n]: Implementierung (konkrete Klasse) im Paket p[n]
- a[n]: Abstraktion auf Ebene n
- ⊑: "ist eine Realisierung von" (Implementierung, Vererbung oder Dekoration)
---
**Regel 4: Rekursive Struktur**
Die Regeln gelten rekursiv für alle Ebenen (n ≥ 1):
```
∀n≥1:Regeln 1-3 gelten fu¨r Ebene n\forall n \geq 1: \text{Regeln 1-3 gelten für Ebene } n∀n≥1:Regeln 1-3 gelten fu¨r Ebene n
```


## 4. Implementierung der Klassen




#### **2.3.1 Paketentstehung**
Pakete entstehen **ausschließlich** auf Ebene gleichnamiger Abstraktionen:
---
∃p[n]⟺∃a[n]∈b[0]
---
- b[0]: Root-Namespace (z. B. com.example.pdc)
- a[n]: Abstraktion im Root-Namespace (z. B. com.example.pdc.Rule)
- p[n]: Paket für Realisierungen (z. B. com.example.pdc.rule/)

#### **2.3.3 Paketinhalte**
Pakete dürfen **nur Realisierungen** ihrer gleichnamigen Abstraktion enthalten:
---
∀c ∈ p[n]: c ⊑ a[n]
---
- `⊑`: Implementierung, Vererbung oder Dekoration
- `c`: Klasse im Paket
- `a[n]`: Abstraktion des Pakets
- 
---
## **3. Paketstruktur**

com.example.pdc/
├── app/                    # Application Layer
│   ├── PDCApp.java          # Core Application
│   └── ConsolePDCApp.java   # Decorator für Konsolenausgabe
├── rule/                    # Regeln
│   ├── CycleRule.java        # Zyklenerkennung
│   ├── NamingRule.java       # Namenskonventionen
│   ├── PackageRule.java      # Regel-Interface
│   └── RealizationRule.java  # Realisierungsprüfung
├── Rule.java             # Repräsentiert ein Paket
└── App.java         # Repräsentiert ein Abstraktion


