# DSV/DSVA
Team: Babin Denys

## Zadani
V této práci jsem implementoval chatovací systém. Při práci jsem použil následující parametry:
- Chat
- ME
- Lamport
- JavaRMI


## Loggs
Hlavní protokolování probíhá v souboru all_logs.txt. Dále jsou výstupy také v konzoli. Do konzole se zobrazují pouze změny logických hodin a zprávy od uživatelů. Hlavním souborem pro protokolování je LoggingToFile.java, který obsahuje odkaz na soubor all_logs.txt. Existují dvě nastavení cesty k souboru, v závislosti na lokálním testování nebo testování na vzdáleném serveru:

- FileHandler fileHandler = new FileHandler("org/example/all_logs.txt", false);
- FileHandler fileHandler = new FileHandler("src/main/java/org/example/all_logs.txt", false);

## Spouštění chatu

1. Pro spuštění chatu je třeba stáhnout archiv. 
2. Přejděte do složky src/main/java z terminálu. 
3. Spusťte příkaz java org.example.Node -myPort PORT -n NAME. 
4. Tímto způsobem jsme spustili prvního uživatele. 
5. Připojte jakéhokoli dalšího uživatele pomocí příkazu: java org.example.Node -myPort MY_PORT -n NSME -ip IP_OF_USER_IN_CHAT -p PORT_OF_USER_WITH_IP. 
6. Používejte chat - Pište.
7. Exit - ukonči prohram


## Presentace
Vzhledem k obtížím s instalací virtuálních strojů na MacBook M1 s macOS verze 14.2.1 mohu nabídnout 2 možnosti prezentace práce:

1. Spustit různé uzly lokálně na svém zařízení.
2. Spustit kód na dvou svých serverech s otevřenými porty a ukázat komunikaci mezi nimi.


## Popis
Toto programové řešení představuje distribuovaný chatovací systém, který využívá Java RMI (Remote Method Invocation) pro komunikaci mezi uzly a implementuje algoritmus Lamportových hodin pro synchronizaci akcí v distribuovaném prostředí. Zde je krátký popis hlavních funkcí a komponent vašeho systému:

### 1. Interakce uživatele s programem
   Uživatelé interagují s programem prostřednictvím příkazové řádky, kde mohou zadávat různé příkazy.
   Když uživatel zadá příkaz "exit", program provede ukončovací rutinu, která zahrnuje informování ostatních uzlů o odchodu uživatele z chatu.
### 2. Co si pamatují uzly
   Každý uzel uchovává seznam známých adres (knownAddresses), což jsou adresy ostatních uzlů v síti.
   Uzly si také pamatují svou logicalClock (Lamportovu hodinu), která je používána pro určení pořadí událostí.
   Uzly mají frontu požadavků (requestQueue), která obsahuje požadavky, jež je třeba zpracovat.
   pendingRepliesMap uchovává informace o tom, od kterých uzlů se čeká na odpověď.
### 3. CommunicationHub
   CommunicationHub slouží jako centrální bod pro komunikaci s ostatními uzly pomocí Java RMI.
   Poskytuje metodu getRMIProxy, která umožňuje získat proxy objekt pro vzdálené volání metod na jiných uzlech.
### 4. Implementace algoritmu Lamportových hodin
   Algoritmus Lamportových hodin je implementován pro udržení konzistence pořadí událostí mezi uzly.
   Při každé komunikaci nebo při příjmu zprávy se logicalClock inkrementuje, což zajišťuje, že každá událost má unikátní časové razítko.
   Při přijetí požadavku od jiného uzlu se logicalClock nastavuje na maximální hodnotu mezi aktuálním časem a časem, který byl přijat s požadavkem, a poté se inkrementuje.
   Odpovídající dotazy k daným typům úloh 
### 5. Obnova Topologie:
   Když uzel vstoupí do sítě nebo z ní odejde, aktualizuje se seznam známých adres všech uzlů. To je zajištěno přes metodu join a handleExit.
   Spouštění Implementovaného Algoritmu: Algoritmus Lamportových hodin se spouští automaticky při každé komunikaci mezi uzly, ať už při posílání zpráv, odpovídání na požadavky nebo při synchronizaci času mezi uzly,
   řešení poskytuje robustní základ pro distribuovanou komunikaci s přihlédnutím k časové synchronizaci a zajištěním konzistence stavu mezi různými uzly v síti.