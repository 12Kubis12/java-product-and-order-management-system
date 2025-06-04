# Spring Boot zadanie starter pack
## Street of Code - Java kurz
### Popis systému
Aplikačný server umožňuje spravovať produkty a objednávky.
Webové rozhranie (API), ako aj objekty, ktoré sa používajú na komunikáciu,
sú zadefinované v zadanej špecifikácii.

Špecifikácia API je dostupná na adrese: https://app.swaggerhub.com/apis-docs/JAHICJAKUB/Street_of_Code_Spring_Boot_zadanie/1.0.0

Systém umožňuje vytvárania a odoberanie produktov. Ďalej umožňuje
úpravu existujúcich produktov, ako aj navýšenie stavu produktov na sklade.

Systém umožňuje vytváranie a vymazávanie objednávok. Do objednávok je možné
primárne pridávať produkty, ktoré sú dostupné na sklade. Pre prípad nedostatku 
tovaru na sklade bude odpoveďou chybový kód 400.

Systém umožňuje zaplatenie objednávky. Po zaplatení objednávky sa zmení jej stav
na `paid`. Do zaplatenej objednávky nie je možné pridávať produkty.

### Automatizované testy
Systém obsahuje automatizované integračné testy, ktoré sú umiestnené v priečinku `src/test/java`.
Testy testujú funkčnosť API. Nie je potrebné pridávať ďalšie testy ani ich nijak meniť.

Ak všetky testy prechádzajú, tak je aplikácia správne naimplementovaná.
