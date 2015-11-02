# lce (loan cut equilibrium)
A library for computing debt cuts in  financial graphs leading to global zero-equity.

First of all, you need a graph:
```java
final Graph input = new Graph("Your funky graph.");
```

Next, add some nodes to it:
```java
final Node u = new Node("u");
final Node v = new Node("v");
input.add(u);
input.add(v);
```

Create contracts:
```java
Contract k_u = ContractFactory.newContract("k_u")
                              .withPrincipal(2.0)
                              .withInterestRateSelector(0.1)
                              .withCompoundingPeriods(3.0)
                              .withTimestamp(-1.0);
        
Contract k_v = ContractFactory.newContract("k_v")
                              .withPrincipal(1.0)
                              .withInterestRateSelector(0.12)
                              .withContinuousCompounding()
                              .withTimestamp(0.0);
```

Link the nodes with contracts:
```java
u.addDebtor(v, c_u);
v.addDebtor(u, c_v);
// You may assign more than one contract per edge.
```

Specify the time points at which the contracts may be cut:
```java
final TimeAssignment ta = new TimeAssignment();
ta.put(u, c_v, 3.1); // Contract c_v is cut at 3.1.
ta.put(v, c_u, 2.5); // Contract c_u is cut at 2.5.
```

Instantiate and set your equilibrial debt cut finder:
```java
final EquilibrialDebtCutFinder finder = new DefaultEquilibrialDebtCutFinder();
input.setDebtCutFinder(finder);
```

Choose the time point at which the graph should attain equilibrium:
```java
final double eqTime = 5.0;
```

Find the debt cuts leading to equilibrium:
```java
final DebtCutAssignment dca = input.findEquilibrialDebtCuts(eqTime, ta);
```

Apply the debt cuts to input graph and obtain another:
```java
final Graph output = input.applyDebtCuts(dca, ta);
```

Check for equilibrium:
```java
System.out.println("Is in equilibrium at " + eqTime + ": " + 
                   output.isInEquilibriumAt(eqTime));
```

See for yourself:
```java
System.out.println(output.describe(eqTime));
```

The sum of all debt cuts:
```java
System.out.println("Debt cut sum: " + dca.sum());
```        
