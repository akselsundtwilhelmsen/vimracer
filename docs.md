# Del 1: Beskrivelse av appen
Vi har laget en applikasjon der målet er å redigere en tekst etter en fasit.
Dette gjøres ved hjelp av vim-kommandoer.
Brukeren ser to tekstbokser der den ene er en fasittekst og den andre er en litt endret versjon av denne teksten.
Til høyre er et panel med knapper som starter og avslutter en runde, knapper som bytter fasittekst, en tekstboks til å skrive inn navn, en poengtavle, en tastetrykksteller, og en stoppeklokke.
I den redigerbare teksten er det en farget bokstav, dette er pekeren.
Dersom en linje er farget grå betyr det at denne linjen tilsvarer linjen ved samme linjenummer i fasiten.
Når brukeren har redigert teksten slik at den tilsvarer fasitteksten vil navn, tid, og antall tastetrykk lagres i en fil.
Poenget med spillet er å redigere teksten enten så fort som mulig, eller ved hjelp av så få tastetrykk som mulig.

# Del 2: Diagram
TODO

# Del 3: Spørsmål
## 1: Hvilke deler av pensum er dekket i prosjektet?
I programmet er det mange eksempel på delegering.
Vi også har dekket arv.
Vim.java arver fra TextWindow.java.
På denne måten slipper man å programmere metoder for å oppbevare og endre tekst to ganger.

## 2: Hvilke deler av pensum er ikke dekket i prosjektet?
Den eneste gangen vi har implementert et grensesnitt er i klassen VimCommandList der vi implementerer Iterable.
Vi har aldri implementert et egendefinert grensesnitt, som er en del av pensum.

## 3: Hvordan forholder koden seg til MVC-prinsippet?
Visningsdelen vår er stort sett i add.fxml-filen.
Den blir påvirket av enkelte metoder i kontrolleren Controller.java.
Kontrolleren vår oppfyller ikke MVC-prinsippets regler perfekt ettersom kontrolleren inneholder logikk som riktignok burde vært i modelldelen [1].
Controller.populateTextFlow(TextFlow textFlow, int maxLineLength) regner ut \<Text\>-objektene som gjør opp den redigerbare delen av teksten.
Dette gjøres for å kunne fargelegge forskjellige individuelle deler av teksten.
Det hadde selvfølgelig vært mulig å flytte til modelldelen av programmet men ettersom den jobber med fxml-objekter er det praktisk å ha den i kontrolleren.

## 4: Hvordan har dere gått frem for å teste programmet?
Vi har testet programmet både ved hjelp av main-metoder i individuelle klasser, og ved å kjøre programmet.
Hvis en en metode skulle testes, ble det ofte gjort ved å interragere med metoden slik som en bruker ville gjort.
Det vil si at vi testet ved å bruke den som den ville blitt brukt i programmet.
Vi har brukt både debugger og System.out.println(); for å holde styr på verdier gjennom programmets kjøring.

### Referanser
[1]: [Model-View-Controller. *Wikipedia*.](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)

[2]: [Vim. *Man page*.](https://manpages.org/vim)