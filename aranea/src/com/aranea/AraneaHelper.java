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
  Aranea este un crawler web ce pune la dispozitia utilizatorilor, printr-o
  interfata in linie de comanda, o serie de operatii utile descarcarii locale a
  paginilor web si a procesarii lor:
  - executarea crawling-ului pe mai multe fire de executie asupra unei serii
  de website-uri aflate la distanta
  - generarea de harti pentru website-urile descarcate
  - filtrarea paginilor salvate local dupa o anumita extensie
  - cautarea unui sablon in paginile salvate local
  - solicitarea ajutorului.

OPERATII DISPONIBILE
  Operatiile disponibile sunt:
  - "crawl"
  - "list"
  - "search"
  - "interactive"
  - "help".

AJUTOR SUPLIMENAR:
  Utilizati "aranea help COMMAND" pentru vizualizarea mai multor detalii despre
  o anumita comanda.

USURAREA UTILIZARII:
  Pentru a usura folosirea acestui utilitar, va recomandam sa setati un alias
  printr-o comanda specifica sistemului dumneavoastra de operare:
  - 'doskey aranea="java -jar ABSOLUTE_PATH_TO/aranea.jar $*"' pentru Windows
  - 'alias aranea="java -jar ABSOLUTE_PATH_TO/aranea.jar"' pentru Linux si macOS.
""";

  static String crawlHelp = """
COMENZI:
  crawl URLS_FILE CONFIG_FILE

UTILIZARE:
  Descarca continutului unor website-uri listate intr-un fisier (URLS_FILE),pe
  baza unui alt fisier, de configurare (CONFIG_FILE).

FORMATUL FISIERULUI DE CONFIGURARE:
  Fisierul de tip CONFIG_FILE seteaza configuratia initiala a programului,
  prin intermediul anumitor chei:
  - "download_dir", sir de caractere pentru directorul de descarcare
  - "log_file", sir de caractere pentru fisierul de jurnal
  - "log_level", intreg pentru prioritatea minima a unui eveniment pentru a fi\s
    jurnalizat (optional, implicit 0)
  - "is_sitemap_generated", boolean care indica daca se vor genera harti pentru\s
    website-urile descarcate (optional, implicit "true")
  - "max_threads", intreg pentru numarul maxim de fire de executie (optional,
    implicit 1000)
  - "delay", numarul de secunde intre doua cereri consecutive catre un server
    web tinta (optional, implicit 1)
  - "allowed_extensions", sir de caractere pentru extensii ale fisierelor ce
     vor fi descarcate, separate prin virgula (optional, implicit "*")
  - "allowed_max_size", intreg pentru dimensiunea maxima, in octeti, avuta de
    un fisier ce va fi descarcat (optional, implicit 1000000000)
  - "allowed_pattern", sir de caractere pentru un sablon Regex ce trebuie sa se
    regaseasca intr-un fisier pentru a fi descarcat (optional, implicit "")
  - "skip_robotsdottxt_files", boolean care indica daca fisierele mentionate in
    robots.txt} nu vor fi descarcate (optional, implicit "true")
  - "head_requests", boolean care indica daca cererile de tip HEAD sunt folosite
    pentru determinarea lungimii unei pagini, inainte de descarcarea acesteia
  - "max_downloaded_pages", intreg pentru numarul maxim de pagini ce vor fi
    descarcate.
  Conform cu formatul standard al unui fisier de proprietati, pe care fisierele
  de configurare il respecta, fiecarei chei ii corespunde o valoare. Perechile
  chei-valoare trebuiesc separate intre ele prin linie noua.
""";


  static String listHelp = """
COMENZI:
  list EXTENSION

UTILIZARE:
  Filtreaza paginile salvate local, apartinand unor website-uri, dupa extensie
  (EXTENSION).
""";

  static String searchHelp = """
COMENZI:
  search PATTERN

UTILIZARE:
  Cauta un sablon (PATTERN) in paginile descarcate local, apartinand unor
  website-uri.
""";

  static String interactiveHelp = """
COMENZI:
  interactive

UTILIZARE:
  Utilizeaza utilizarul in modul interactiv.
""";

  static String helpHelp = """
COMENZI:
  help
  help COMMAND

UTILIZARE:
  Solicita ajutorul cu privire la utilizarea programului sau a unei comenzi
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
