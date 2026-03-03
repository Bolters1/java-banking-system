Design Patterns

Factory Pattern (AccountFactory)
Am folosit Factory Pattern prin clasa AccountFactory pentru a respecta principiul Open/Closed (OCP). În loc să instanțiez direct obiectele de tip cont (BusinessAccount, ClassicAccount, SavingsAccount) în logica principală a aplicației, acest factory se ocupă de crearea lor. Astfel, dacă pe viitor va fi nevoie să adăugăm un nou tip de cont, logica de bază rămâne neatinsă, fiind necesară doar extinderea în clasa factory.

Strategy Pattern
Acest pattern a fost aplicat în modulul de calculare a cashback-ului pentru a evita structurile masive și greu de întreținut de tip switch-case, respectând totodată Single Responsibility Principle (SRP). Interfața CashbackStrategy definește contractul de bază, iar clasele concrete (precum NrOfTransactionsStrategy sau SpendingThreshold) implementează algoritmii specifici. Asta face sistemul foarte ușor de extins: adăugarea unei noi reguli de cashback presupune pur și simplu crearea unei noi clase care implementează strategia, fără să modificăm codul deja existent.

Command Pattern
Pentru a gestiona varietatea mare de acțiuni pe care le poate face sistemul (adăugare de fonduri, creare de conturi, generare de rapoarte de business), am încapsulat fiecare acțiune într-o clasă separată de comandă. Interfața Command este implementată de aceste clase specifice. Acest lucru decuplează invoker-ul (sistemul care primește cererea) de receiver (logica efectivă care execută acțiunea), făcând codul mult mai modular și simplificând adăugarea de comenzi noi.

Singleton Pattern
Clasa Singleton este folosită pentru a menține o singură sursă de adevăr pe toată durata rulării aplicației. Aceasta asigură un acces global la resursele partajate care nu trebuie sub nicio formă duplicate, cum ar fi TransactionManager sau sistemul care gestionează output-ul, prevenind astfel inconsistențele la nivel de date.

Structura Proiectului

Proiectul este modularizat și organizat în mai multe pachete, separând clar responsabilitățile sistemului bancar:

main: Conține punctul de intrare în aplicație și gestionează inițializarea, inclusiv instanța Singleton pentru accesul global.

manager: Se ocupă de setup-ul inițial al sistemului și de gestionarea inputului/outputului.

service: Pachetul principal care conține logica de business și conectează diferitele module ale aplicației.

commands: Include interfețele și implementările concrete pentru acțiunile sistemului (adăugare fonduri, creare conturi, rapoarte).

users: Gestionează entitățile utilizatorilor și asocierile lor. Aici se află sub-pachetele "accounts" (pentru logica specifică conturilor de business, clasice și de economii) și "cards" (pentru cardurile fizice și cele de tip One-Time).

cashback: Conține interfața și implementările strategiilor de calcul pentru cashback.

commerciants: Gestionează datele și interacțiunile sistemului cu comercianții.

exchange: Responsabil pentru logica de schimb valutar.

split: Conține logica necesară pentru împărțirea plăților (split payment) între mai multe conturi.
