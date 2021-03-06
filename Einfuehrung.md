# Einführung #

Wir freuen uns euch endlich das neue Abrechnungsprogramm zur Verfügung zu stellen. Auf diesen Seiten wollen wir euch mit der Installation und Bedienung vertraut machen.

Wir haben uns bemüht das neue Programm dem alten so ähnlich wie möglich zu gestalten, um die Umstellung einfach zu machen.

Sollten Schwierigkeiten oder Fragen auftauchen, könnt ihr uns unter [Software@ak-rd.de](mailto:software@ak-rd.de). Häufig gestellte Fragen werden wir auf der [FAQ](FAQ.md) Seite beantworten.

Im [Changelog](Changelog.md) könnt ihr nachlesen, was sich von Version zu Version geändert hat.

<p align='center'>
<img width='80%' src='https://drk-ak-rettungsdienst-abrechnungsprogramm.googlecode.com/svn/wiki/img/MainWindow.png' /></p>

## Warum überhaupt ein neues Programm? ##
Diese Frage wurde uns seit der Vorstellung beim AK Wochenende häufig gestellt. Die beiden Hauptgründe sind:
  * Der Autor des alten Abrechnungsprogramms hat den AK verlassen und steht somit für Aktualisierungen nicht mehr zur Verfügung.
  * Das alte Programm benötigt das kostenpflichtige Programm Microsoft Excel und kann somit nicht von Jedem überall genutzt werden. Außerdem funktioniert es nicht mit allen Versionen.

Das neue Abrechnungsprogramm wurde in Java entwickelt und ist somit plattormunabhängig, d.h. es läuft auf jedem Betriebssystem für dass es eine Java Umgebung gibt.
Insbesondere wird es während der Entwicklung getestet auf Windows, MacOS und Linux.

# Kurzanleitung #
## Installation ##
Für jedes der oben genannten Betriebssysteme haben wir auf der Downloads Seite ein ZIP-Archiv bereitgestellt. Entpackt dieses an einen beliebigen Ort und startet dann das Programm durch Doppelklick auf _Abrechnungsprogramm.exe_ unter Windows oder _start.sh_ unter MacOS und Linux. Das wars auch schon.

## Was bleibt beim alten? ##
Eigentlich alles alles! Insbesondere beim nutzen der Wachenversion werden weiterhin zuerst die Stammdaten unter "Persönliche Daten" eingetragen und dann die Schichten im "Schichten" Tab gesammelt und die Abrechnung erstellt.
<p align='center'>
<img width='80%' src='https://drk-ak-rettungsdienst-abrechnungsprogramm.googlecode.com/svn/wiki/img/SchichtenTab.png' /></p>

## Was ist neu? ##
### Schichten ###
Neu im Schichten Tab sind die beiden unteren Menüs, über die man das angezeigte Jahr und den Monat auswählen kann. So könnt ihr z.B. schnell mal schauen, was ihr letzten Monat so verdient habt.
### Dienstplan auslesen ###
Über den Tab Dienstplan auslesen könnt ihr die PDF-Datei mit dem Monatsdienstplan automatisch auslesen lassen. Eure Dienste werden dann rausgesucht und aufgelistet. Die gefundenen Dienste können dann als iCal Datei exportiert werden oder direkt in einen Google Kalender eingetragen werden.
<p align='center'>
<img width='80%' src='https://drk-ak-rettungsdienst-abrechnungsprogramm.googlecode.com/svn/wiki/img/DPL_ausgelesen.png' /></p>
Dabei gibt es allerdings einen Haken: Das PDF Format ist nicht dazu gemacht maschinell ausgelesen zu werden und wir mussten ein bisschen tricksen, um diese Funktion zu ermöglichen. Den Aufmerksamen unter euch wird aufgefallen sein, dass die Dienstplanung unterschiedliche Formate herausgibt. Das auslesen funktioniert **nur** mit den Monatsdienstplänen, die die Zeile "Arbeitszeit pro Tag" enthalten.
<p align='center'>
<img width='50%' src='https://drk-ak-rettungsdienst-abrechnungsprogramm.googlecode.com/svn/wiki/img/DPL.png' /></p>

### Fragebogen ###
Wie im bisherigen Programm könnt ihr wieder eure Dienstplan-Wunsch-Abgaben erstellen. Einfach den Monat auswählen, die gewünschten Abgaben
anklicken und auf Ausgeben klicken. Ein PDF mit dem Namen Fragebogen\_MONAT\_JAHR.pdf erscheint im Ordner Fragebögen.

<p align='center'>
<img width='80%' src='https://drk-ak-rettungsdienst-abrechnungsprogramm.googlecode.com/svn/wiki/img/Fragebogen.png' /></p>

### Import/Export ###
Hier könnt ihr eure gesammelten Schichtdaten monatsweise exportieren, um sie dann z.B. auf die Wache mitzunehmen, um sie dort zu drucken.
<p align='center'>
<img width='80%' src='https://drk-ak-rettungsdienst-abrechnungsprogramm.googlecode.com/svn/wiki/img/ImportExport.png' /></p>

### Info/Update ###
Ein Problem des alten Programms war es, dass viele Versionen unterwegs waren und man nie genau wusste, welche jetzt die aktuelle ist.
Das neue Programm überprüft selbstständig, ob eine neue Version verfügbar ist und informiert darüber in der Status-Zeile unten im Fenster.
Darüber hinaus kann der Schicht-Datensatz im laufenden Programm aktualisiert werden, indem ihr im Info/Update Tab auf "Schicht-Update" klickt.

Das Programm selbst kann sich nicht automatisch updaten, sondern nur feststellen ob eine neue Version verfügbar ist. Um das Programm zu aktualisieren müsst ihr **nur** die Abrechnungsprogramm.exe (Windows), bzw. Abrechnungsprogramm.jar (Linux/MacOS) austauschen und **nicht** den _data_ Ordner. So bleiben eure gespeicherten Daten erhalten.