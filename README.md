# 💳 Payment System

Aplikacja w języku **Java 17**, która przetwarza zamówienia (`Order`) oraz metody płatności (`PaymentMethod`), obliczając końcowy koszt po zastosowaniu promocji i limitów płatności.

---

## 🚀 Technologie

- Java 17
- Maven
- JUnit 5

---

## ▶️ Uruchamianie aplikacji

### Budowa projektu

```bash
mvn clean package
java -jar target/app.jar orders.json paymentmethods.json
```

> Gdzie:
> - orders.json – ścieżka do pliku z listą zamówień
> - paymentmethods.json – ścieżka do pliku z listą metod płatności

Jeśli argumenty nie zostaną podane, aplikacja wypisze instrukcję użycia.

## 📁 Przykładowe dane wejściowe

### 📦 orders.json
```json 
[
    {
        "id": "ORDER1",
        "value": "100.00",
        "promotions": ["PUNKTY"]
    },
    {
        "id": "ORDER2",
        "value": "200.00",
        "promotions": ["BosBankrut"]
    }
]
```

### 💳 **paymentmethods.json**

```json
[
  {
    "id": "PUNKTY",
    "discount": "15",
    "limit": "100.00"
  },
  {
    "id": "BosBankrut",
    "discount": "5",
    "limit": "200.00"
  }
]
```

## 💡 **Logika promocji**
- Jeśli całe zamówienie zostanie opłacone jedną metodą – stosowany jest rabat tej metody
- Jeśli co najmniej 10% wartości zostanie opłacone punktami (PUNKTY) – dodatkowy rabat 10%
- Jeśli całe zamówienie zostanie opłacone punktami – stosowany jest rabat przypisany do metody "PUNKTY" (zamiast 10%)

## 🧪 Testowanie
Projekt zawiera testy jednostkowe napisane w JUnit 5.

Aby uruchomić testy:
```bash
mvn test
```