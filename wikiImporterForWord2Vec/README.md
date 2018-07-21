Autor: Akhmad Sadullaev

Diese Paser ist für den Ansatz in Word2Vec nötig.

Dieser extrahiert und bereinigt den Text aus einem Wikipedia-Datenbank-Dump und speichert die Ausgabe (und zwar jeden Satz aus jedem Dokument) in einem
Cassandra Datenbank in einem bestimmten Keyspace bzw. Tabelle.

Was befindet sich in den folgenden Ordnern:
python3 -> da ist der Parser in Python 3 implementiert. Status: Fertig
src -> da ist der Parser in Scala implementiert. Status: Nicht fertig. Problem: Es wird NLP für Scala benötigt, was im Moment nicht existiert. 

Die Dokumentation für Python und Scala bitte in entsprechendem Ordner zu lesen.