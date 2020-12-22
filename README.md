# Aranea :spider_web:

## Descriere :page_with_curl:

**Aranea** este un **crawler web** ce pune la dispoziția utilizatorilor, printr-o interfață în linie de comandă, o serie de operații utile descărcării locale a paginilor web și a procesării lor:
- executarea **crawling**-ului pe mai multe fire de execuție asupra unei serii de website-uri aflate la distanță
- **generarea de hărți** pentru website-urile descărcate
- **filtrarea** paginilor salvate local **după o anumită extensie**
- **căutarea unui șablon** în paginile salvate local
- **solicitarea ajutorului**.

Programul este scris în limbajul de programare Java, fiind rezultatul unui teme din cadrul cursului "*Ingineria Sistemelor de Programe*".

## Operații Disponibile :toolbox:

Operațiile disponibile sunt:
- `crawl`
- `list`
- `search`
- `interactive`
- `help`.

## Ușurarea Utilizării :gun:

Pentru a ușura folosirea acestui program, vă recomandăm să setați un alias printr-o comandă specifică sistemului dumneavoastră de operare:
- `doskey aranea="java -jar ABSOLUTE_PATH_TO/aranea.jar"` pentru Windows
- `alias aranea="java -jar ABSOLUTE_PATH_TO/aranea.jar"` pentru Linux și macOS.

De menționat este faptul că fișierul `aranea.jar` poate fi descărcat din secțiunea *Releases* a acestui repository.