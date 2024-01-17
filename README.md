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
6. Používejte chat.


## Presentace
Vzhledem k obtížím s instalací virtuálních strojů na MacBook M1 s macOS verze 14.2.1 mohu nabídnout 2 možnosti prezentace práce:

1. Spustit různé uzly lokálně na svém zařízení.
2. Spustit kód na dvou svých serverech s otevřenými porty a ukázat komunikaci mezi nimi.