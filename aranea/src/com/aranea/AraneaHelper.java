package com.aranea;

public class AraneaHelper {

  static String banner = """
                                           \\_______/
                                       `.,-'\\_____/`-.,'
                                        /`..'\\ _ /`.,'\\
 ___  ___  ___  _  _  ___  ___         /  /`.,' `.,'\\  \\
/   \\| _ \\/   \\| \\| || __|/   \\       /__/__/     \\__\\__\\__
| - ||   /| - || .  || _| | - |       \\  \\  \\     /  /  /
|_|_||_|_\\|_|_||_|\\_||___||_|_|        \\  \\,'`._,'`./  /
                                        \\,'`./___\\,'`./
                                       ,'`-./_____\\,-'`.
                                           /       \\

""";

  static String genericHelp = """
DESCRIERE:
  Aranea este un crawler web ce pune la dispoziția utilizatorilor, printr-o
  interfață în linie de comandă, o serie de operații utile descărcării locale a
  paginilor web și a procesării lor:
  - executarea crawling-ului pe mai multe fire de execuție asupra unei serii
  de website-uri aflate la distanță
  - generarea de hărți pentru website-urile descărcate
  - filtrarea paginilor salvate local după o anumită extensie
  - căutarea unui șablon în paginile salvate local
  - solicitarea ajutorului.

OPERAȚII DISPONIBILE
  Operațiile disponibile sunt:
  - "crawl"
  - "list"
  - "search"
  - "interactive"
  - "help".

AJUTOR SUPLIMENAR:
  Utilizați "aranea help COMMAND" pentru vizualizarea mai multor detalii despre
  o anumită comandă.

UȘURAREA UTILIZĂRII:
  Pentru a ușura folosirea acestui utilitar, vă recomandăm să setați un alias
  printr-o comandă specifică sistemului dumneavoastră de operare:
  - "doskey aranea=java -jar ABSOLUTE_PATH_TO/aranea.jar" pentru Windows
  - "alias aranea=java -jar ABSOLUTE_PATH_TO/aranea.jar" pentru Linux și macOS.
""";

  static String crawlHelp = """
COMENZI:
  crawl URLS_FILE CONFIG_FILE

UTILIZARE:
  Descarcă conținutului unor website-uri listate într-un fișier (URLS_FILE),pe
  baza unui alt fișier, de configurare (CONFIG_FILE).
""";


  static String listHelp = """
COMENZI:
  list EXTENSION

UTILIZARE:
  Filtrează paginile salvate local, aparținând unor website-uri, după extensie
  (EXTENSION).
""";

  static String searchHelp = """
COMENZI:
  search PATTERN

UTILIZARE:
  Caută un șablon (PATTERN) în paginile descărcate local, aparținând unor
  website-uri.
""";

  static String interactiveHelp = """
COMENZI:
  interactive

UTILIZARE:
  Utilizează utilizarul în modul interactiv.
""";

  static String helpHelp = """
COMENZI:
  help
  help COMMAND

UTILIZARE:
  Solicită ajutorul cu privire la utilizarea programului sau a unei comenzi
  anume (COMMAND).
""";

  static void requestGenericHelp(){
    System.out.print(banner);
    System.out.print(genericHelp);
  }

  static void requestCommandHelp(String command){
    System.out.print(banner);
    switch (command){
      case "crawl" -> System.out.print(crawlHelp);
      case "list" -> System.out.print(listHelp);
      case "search" -> System.out.print(searchHelp);
      case "interactive" -> System.out.print(interactiveHelp);
      case "help" -> System.out.print(helpHelp);
      default -> System.out.print(genericHelp);
    }
  }

  private void exemplifyUsage(){
    AraneaHelper.requestGenericHelp();
    AraneaHelper.requestCommandHelp("crawl");
  }

}