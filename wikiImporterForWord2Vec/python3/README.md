# Dokumentation für Word2Vec-Importer in Python3

Die Importierung wird in 3 Schritten ausgeführt.

Im Schritt-1 werden die Parameter für Parser defeniert. 
Es wurden die Parameter deklariert, die in den nächsten Schritten benötigt werden, und die den folgenden Aufgaben laut Wikipedia entsprechen:
- Lese Siteinfo ab. Wir enthalten als Standardvorlagen, wenn externe Vorlagedatei geladen wird.
- Defeniere der Namespace, der für Moduldefinitionen verwendet wird. Dies ist der Name, der im Header siteinfo mit dem Namespace key = 828 verknüpft ist.
- Erkenne nur die Namespaces in Links --> w: Interne Links zur Wikipedia; wiktionary: Wiki-Wörterbuch; wikt: Abkürzung für Wiktionary
- Filtere "disambiguation pages"
- Tabellen aus dem Artikel löschen
- Defeniere, ob Links in der Ausgabe beibehalten werden sollen, ob Titel beibehalten werden soll, ob Listen beibehalten werden sollen
- Defeniere, ob HTML anstelle von Text ausgegeben werden soll
- Defeniere, ob JSON anstelle des XML-ähnlichen Standardausgabeformats geschrieben werden soll oder nicht
- Defeniere, ob Vorlagen erweitert werden sollen
- Defeniere, ob Doc-Inhalt zu entkommen ist
- Speicherung der Wikipedia-Artikelrevision
- Defeniere minimale erweiterte Textlänge zum Speichern des Dokuments
- Erstelle gemeinsame Objekte mit Vorlagen, Weiterleitungen und Cache
- Defeniere Regex zum Identifizieren von Disambig-Seiten


Im Schritt-2 werden die benötigten Funktionen defeniert. Als wichtig sind die folgenden zu betrachten:
- normalizeTitle(): Entfernen der führenden / nachfolgenden Whitespaces und Unterstrichen. Ersetzen Sequenzen von Leerzeichen und Unterstreichungszeichen durch ein einzelnes Leerzeichen. Wenn das Präfix einen bekannten Namespace bezeichnet, können optionale Whitespaces folgen, die entfernt werden sollten, um den kanonischen Seitennamen zu erhalten (z. B. "Category: Births" sollte "Category: Births" werden). Falls kein Namespace, nur Großbuchstaben Großbuchstaben. Wenn der Teil vor dem Doppelpunkt kein bekannter Namespace ist, dürfen wir das Leerzeichen nach dem Doppelpunkt (falls vorhanden) nicht entfernen, z. B. "3001: The_Final_Odyssey"! = "3001: The_Final_Odyssey". Um jedoch den kanonischen Seitennamen zu erhalten, müssen wir mehrere Leerzeichen zu einem zusammenführen, weil "3001: The_Final_Odyssey"! = "3001: The_Final_Odyssey".
- unescape(): Entfernt HTML- oder XML-Zeichenreferenzen und -Elemente aus einer Textzeichenfolge.
- subst(): Wir führen rekursiv Parameter-Substitutionen durch. Wir begrenzen auch die maximale Anzahl von Iterationen, um zu lange oder endlose Schleifen im Ofen zu vermeiden (im Falle einer fehlerhaften Eingabe). Parameterwerte werden Parametern in zwei (?) Pässen zugewiesen. Daher kann ein Parametername in einer Vorlage vom Wert eines anderen Parameters derselben Vorlage unabhängig von der Reihenfolge abhängen, in der sie im Vorlagenaufruf angegeben wurden, z. B. mit Vorlage: ppp mit "{{{{{{p }}}}}} ", {{ppp | p = q | q = r}} und gerade {{ppp | q = r | p = q}} ergibt r, aber mit Template: tvvv mit" {{{{ {{{{{p}}}}}}}}} ", {{tvvv | p = q | q = r | r = s}} ergibt s.
- templateArgs(): Der Parametername selbst könnte Vorlagen enthalten, z. B.: {{if: {{Ernenner14 |}}} | r | d}} 14 | 4 | {{{{subst |}}} AKTUELLES}}. Alle Teile in einem tplarg nach dem ersten (der Parameter default) werden ignoriert, und ein Gleichheitszeichen im ersten Teil wird als einfacher Text behandelt.
- subst(): Ersatzwert für Wiki zum Auswerten von Ausdrücken für Name und Standard und begrenzt die Substitution auf das Maximum
- Extractor..(): Eine Extraktionsaufgabe für einen Artikel
- extract(): Extrahiert die Daten aus Datei
- transform(): Verwandelt Wiki-Markup.
- transform1(): Text umwandeln, der "nowiki" nicht enthält
- wiki2text(): Filtert die Texte in tags
- clean(): Entfernt irrelevante Teile von Text
- expand(): Vorlagen werden häufig verschachtelt. Gelegentlich können Analysefehler dazu führen, dass die Vorlageneinfügung in eine Endlosschleife eintritt, beispielsweise beim Instanziieren
- templateParams(): Erstellt ein Wörterbuch mit Positions- oder Namensschlüssel für erweiterte Parameter.

Im Schritt-3 weden in Python-Notebook Foldendes ausgeführt:
- Es wird die Verbindung mit Cassandra Datenbank hergestellt
- Es wird die Tabelle erstellt falls die nicht existiert
- Dump wird extrahiert
- Die Texte weden in die Sätze tokenisiert 
- Die Sätze werden werden in Datebank zusamenn mit Artikel-ID gespeichert.