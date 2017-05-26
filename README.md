# Smokehouse

Zápočtový projekt napsaný v Java 8. Používá standardní knihovny, SQLite (pouze
serverová část) a naší knihovnu Yaclpplib pro parsování příkazové řádky.

## Kontext

Již tradičně s partou přátel pořádáme událost, při které udíme klobásy a sýry.
Není to jen tak, zvlášť pokud se svojí udírnou neumíte, a neznáte její
specifika. Oni přátelé jsou ovšem povětšinou elektrotechnici, takže přišli s řešením.

V udírně je nainstalovaná sada 18 teplotních čidel, která pravidelně snímají
teplotu. Jsou připojená přes sběrnici I2C do mikrokontroleru, který hodnoty
zobrazuje na malém displeji ze staré Nokie. Současně odesílá naměřené hodnoty
na sériový port.

Tam již většinou čeká převodník z RS232 na USB, připojený do prastarého
počítače, kterému rok od roku odchází více a více hardware. Na něm je
nainstalován program, který nejsme schopni rozchodit na žádném jiném stroji. Je
tedy jen otázkou času, kdy se nám tento vymakaný setup rozpadne.

## Smokehouse

Proto jsem si řekl, že jako zápočtový projekt tento program napíši znovu,
v Javě. Nezaměřil jsem se na napsání specificky tohoto řešení, koneckonců, to
by mohlo být na pár řádků v Perlu. Cílem bylo se i naučit Javu.

Pojmul jsem tedy Smokehouse jako generický framework pro komunikaci senzorů
a observerů v IoT. Smokehouse obsahuje implementaci síťového protokolu
založeného na multicastu, který se snaží data přenášet efektivně a on demand.

Framework vychází z toho, že aplikace poběží distribuovaně rozdělená na několik
částí - minimálně serverovou část běžící přímo u zdroje, nějaké GUI na stejném
stroji, možná stejné GUI na jiném stroji, data bude exportovat webový server,
notifikovat bude mobilní aplikace, ...

Základním cílem byla minimalizace kódu, který je specifický pro daný koncový bod.
Proto je kód vysoce konfigurovatelný a funkční části se do sebe skládají jako lego.

Než ale popíšu strukturu kódu, hodí se umět ho alespoň zkompilovat.

## Jak s kódem zacházet?

V projektu je ANT `build.xml`, v něm několik cílů:

```bash
ant build               Zkompiluje třídy, vytvoří balíčky serveru a klienta
ant doc                 Vygeneruje javadoc dokumentaci
```

## Demonstrační aplikace

Ve frameworku jsou naprogramované dvě aplikace - server poskytující data
a jednoduchý CLI klient.

### Serverová část

Nachází se (po zkompilování) v balíku `smokehouse-server.jar`, pro pohodlnější
spouštění existuje wrapper skript `server.js`. Parametry a nápovědu lze vypsat pomocí přepínače `--help`:

```bash
$ ./server.sh --help
Default options
  --help               Print a usage on standard output and exit successfully.

Options for the server
  --db                 A SQLite database to store the values.
  --replay             The logfile to read and simulate.
  --replay-speedup=R   The log will be replayed R times faster (default 1)

Options for the API
  --max-packet-size    Maximum packet size (B, default 1500)
  --iface, -i          The network interface to listen on (default is the oif for group address)
  --listen=[host][:port], -l The multicast group (and port) to listen on (default 239.42.84.18:42424)
```

Protože málokdo z nás má doma udírnu a reálný zdroj dat, tento server je pouze
demonstrátor, přehrávající data z logů, které vyprodukovala předchozí generace
software. Lze spustit například takto (vzorová data jsou součástí repozitáře):

```bash
$ ./server.sh --replay logs/170429_klobasy.log
```

Tím ovšem zajímavý výstup končí, nadále již server pouze vypisuje logovací hlášky.

Server v tuto chvíli nemá implementovánu funkcionalitu čtení ze sériového
portu, protože jsem daný hardware neměl u sebe. Návrh je ovšem takový, aby
integrace takového ovladače byla co nejjednodušší.

## Klientská část

Pokud už běží server, je možné spustit klienta. Ten ke svému spuštění
nepotřebuje žádné argumenty:

```bash
$ ./client.sh
```

Za předpokladu, že máte na svém stroji správně nastavený multicast, a server
běží, měl by se objevit výpis hodnot senzorů:

```bash
          Top:  48.9, ( 48.9)
       Bottom:  54.7, ( 54.7)
     Together:  51.8, ( 51.8)


                 50.5      46.3      42.2
               ( 50.5)   ( 46.3)   ( 42.2)


             51.3      37.5      55.7
           ( 51.3)   ( 37.5)   ( 55.7)


         50.2      43.8      62.6
       ( 50.2)   ( 43.8)   ( 62.6)




                 50.4      53.0      57.0
               ( 50.4)   ( 53.0)   ( 57.0)


             47.2      53.0      61.1
           ( 47.2)   ( 53.0)   ( 61.1)


         49.0      57.7      64.1
       ( 49.0)   ( 57.7)   ( 64.1)
```

Pokud máte rozumný terminál, měl by být dokonce barevný. Zobrazené hodnoty
odpovídají rozložení teplotních čidel v udírně - dvě vrstvy s devíti senzory
uspořádané do pravidelné 3x3 mřížky.

Server mezitím věrohodně přehrává záznam, čísla by se měla aktualizovat jednou
za 5 vteřin.

## Co se skrývá uvnitř?

Jak jsem již zmínil, něco jako sdílená hashovací tabulka pro hodnoty senzorů.
Každá hodnota, která se má replikovat ze zdroje na ostatní části aplikace, má
svůj klíč a vytvořenou instanci interface Node. Tento interface má dvě
implementace - LocalNode a RemoteNode. Framework se stará o to, aby hodnota
z LocalNode doputovala do všech RemoteNode se stejným klíčem.

S Nodes pracují samotné senzory. Tam už není nutné rozlišovat, kterou
implementaci Node zrovna senzor dostal, a jeho interface je ze všech částí
aplikace stejný.

Nad senzory sedí ještě statistické funkce, prozatím průměry a klouzavé průměry.
Jejich hodnota se také odesílá pomocí Node. Tím je zajištěno, že protokol
nemusí být spolehlivý, protože data pochází vždy ze zdroje (tedy jsou nejhůře
neaktuální, ale nikdy ne špatná).

Senzory a statistické funkce jsou obecně zdrojem hodnoty, mají společný
interface Source.

Většina částí aplikace spolu komunikuje pomocí návrhového vzoru Observer. Při
změně hodnoty nějakého zdroje jsou jiné komponenty notifikovány, a novou
hodnotu zpracují po svém. To je nesmírně přirozený způsob, jak s hodnotami
pracovat, a umožňuje velmi zajímavé aplikace v IoT.

Tady se ovšem ukázal problém. Pokud by byl Observer naimplementovaný přímočaře,
změna hodnoty každého senzoru vyvolá přepočítání všech závislých hodnot. Pokud
ovšem jedna dávka dat ze vstupu změní hodnotu postupně všech 18 senzorů, musela
se spousta hodnot přepočítávat několikrát. To je problém zejména kvůli
odesílání nových hodnot přes síť.

Framework to řeší tak, že používá smyčku událostí, a místo přímého volání
obsluhy notifikace observeru se pouze naplánuje událost. To má za následek, že
k vyhodnocení události dojde až poté, co je změněna hodnota všech senzorů.
Zároveň všechny další závislé hodnoty se "prohledají do šířky".

Díky observeru se velmi příjemně řeší i persistence dat. Nad senzor se posadí
observer, který každou novou hodnotu zapíše. Zatím v implementaci chybí druhá
část, totiž zpětné získávání dat.

## Síťová komunikace

Zaměřil jsem se na to, aby byla co nejefektivnější možná. Protokol proto není
samo-popisující, k jeho přečtení je potřeba mít vybudovanou stejnou sadu Nodes
na obou stranách. Výsledkem ovšem je, že protokol není závislý na konkrétním
rozložení (a složení) senzorů, a přesto potřebuje pouze 4B na teploměr.

Formát paketu je dynamický, skládá se ze zpráv. Zprávy jsou zatím dvou typů:
`Query` a `Value`. Typ zprávy je zakódován v prvním byte. Druhý byte určuje
klíč, tedy identifikátor Node. Následují data, která jsou specifická (i
velikostí) pro daný typ a Node.

 Může existovat několik senzorů, poskytujících hodnotu typu `double`, která
ovšem může jít zakódovat efektivněji se znalostí domény. Např. právě teplotu
v demonstrátoru kóduji jako 16-bit integer, reprezentující rozsah 0-255°C. Pro
enkódování a dekódování hodnot slouží obecně kodeky, který je pro Node
konfigurovatelný.
