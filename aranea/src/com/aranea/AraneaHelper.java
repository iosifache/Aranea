package com.aranea;

/* Class printing to screen help messages */
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

FORMATUL FIȘIERULUI DE CONFIGURARE:
  Fișierul de tip CONFIG_FILE setează configurația inițială a programului,
  prin intermediul anumitor chei:
  - "download_dir", șir de caractere pentru directorul de descărcare
  - "log_file", șir de caractere pentru fișierul de jurnal
  - "log_level", întreg pentru prioritatea minimă a unui eveniment pentru a fi\s
    jurnalizat (opțional, implicit 0)
  - "is_sitemap_generated", boolean care indică dacă se vor genera hărți pentru\s
    website-urile descărcate (opțional, implicit "true")
  - "max_threads", întreg pentru numărul maxim de fire de execuție (opțional,
    implicit 1000)
  - "delay", numărul de secunde între două cereri consecutive către un server
    web țintă (opțional, implicit 1)
  - "allowed_extensions", șir de caractere pentru extensii ale fișierelor ce
     vor fi descărcate, separate prin virgulă (opțional, implicit "*")
  - "allowed_max_size", întreg pentru dimensiunea maximă, în octeți, avută de
    un fișier ce va fi descărcat (opțional, implicit 1000000000)
  - "allowed_pattern", șir de caractere pentru un șablon Regex ce trebuie să se
    regăsească într-un fișier pentru a fi descărcat (opțional, implicit "")
  - "skip_robotsdottxt_files", boolean care indică dacă fișierele menționate în
    robots.txt} nu vor fi descărcate (opțional, implicit "true").
  Conform cu formatul standard al unui fișier de proprietăți, pe care fișierele
  de configurare îl respectă, fiecărei chei îi corespunde o valoare. Perechile
  chei-valoare trebuiesc separate între ele prin linie nouă.
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

  /* Give help about using the program */
  static void requestGenericHelp(){
    System.out.print(banner);
    System.out.print(genericHelp);
  }

  /**
   * Give help about a specific command
   *
   * @param command Command
   */
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

  /* Function to exemplify the usage */
  private void exemplifyUsage(){
    AraneaHelper.requestGenericHelp();
    AraneaHelper.requestCommandHelp("crawl");
  }
}
