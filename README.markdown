Die Wumpus-Welt
==============
Vorbereitung
------------
Die Geschichte

Sie sind ein Roboter in einer gefährlichen Welt, dem auferlegt wurde einen Goldschatz zu finden und diesen zum Ausgang zurück zu bringen.<br>
Ha, einfach, möchte man meinen. Leider ist die Welt nicht ohne Gefahren. In dieser Welt gibt es bodenlose Löcher und in diese sollte man nicht hineinfallen. <br>
Ausserdem gibt eines Wumpus in dieser Welt. Dieser schläft zwar, aber sollten Sie auf sein Feld fahren und ihn dadurch aufwecken, werden Sie in Stücke gerissen. <br>
Sie müssen also einen Weg finden, die Welt zu erkunden bis Sie das Gold gefunden haben und es zurück bringen, ohne dabei in ein Loch zu fallen oder den Wumpus aufzuwecken.<br>
Was die Sache noch schlimmer macht ist, dass Sie zunächst gar nicht wussten, dass diese Gefahren in dieser Welt lauern. 
Das heist Sie sind nicht mit den besten Sensoren ausgestattet um die Aufgabe zu lösen. <br>
Ihre Luftzugsensoren nehmen allerdings einen Luftzug war, jedes mal wenn Sie in der Nahe eines Loches stehen (Ein Loch ist direkt östlich/nördlich/westlich/südlich von Ihnen). <br>
Und auch der Wumpus ist auf ähnliche Art erkennbar. Dieser zeichnet sich allerdings durch einen distinkten Gestank aus.
Einen kleinen weiteren Vorteil haben Sie noch. <br>
Sie haben einen Pfeil dabei, der beim Aufprall explodiert. <br>
Diesen können benutzen, um den Wumpus zu töten und somit eventuell blockierte Wege frei zu machen.<br><br>

So ziehet hinaus! Findet das Gold! Sterbet nicht!<br>
<br>
Technische Hinweise und Tipps, die die Geschichte kaputt machen<br>
---------------------------------------------------------------
<br>
Auf dem Feld sind Löcher Schwarz dargestellt, das Gold ist Gelb dargestellt, und der Wumpus ist der rot hinterlegte Roboter.

RoSE unterstützt mehr Methoden und gibt mehr Informationen, als Sie brauchen. <br>
Die Mal-Methode oder das "Start Argument" Feld des InputTuple ist für Sie zum Beispiel uninteressant.
Sie starten immer oben links und schauen immer nach Osten.<br>
Ziel ist es über das Goldfeld zu fahren und dann zurück zum Ausgang zu fahren.<br>
Um das Gold zu erkennen, müssen Sie im InputTuple das Feld color abfragen und vergleichen, ob es mit color.YELLOW übereinstimmt. An den InputTuple zu kommen können Sie die Funktion user.Base.getInputTuple() benutzen. <br>
Ihr Roboter hat eine Aufhebautomatik. Wenn Sie auf ein neues Feld fahren, müssen Sie als erstes schauen, ob es das Goldfeld ist, weil es nach einer Runde vom Roboter augenommen wird und dann nicht mehr erkennbar ist.<br>
Um zu checken, ob Sie in der nähe eines Wumpus oder Loches sind, können Sie im InputTuple die Felder stench und airDraft abfragen.<br>
Wenn Sie den Wumpus erschiessen wollen, seien Sie sicher, dass Sie wissen wo er sich befindet und, dass Sie in die richtige Richtung schauen. Sie haben nur einen Versuch!<br>
Diese Aufgabe fordert relativ wenig spezielle Java/OOP-Kentnisse. Sie brauchen kaum Verberbung/Polymorphie/... . Es hilft aber bei der Lösung der Aufgabe vermutlich sehr, seine Collections zu kennen.<br>
Diese Aufgabe fordert ziemlich viel selbständiges Denken und Problemlösen ohne viele Angaben in der Aufgabenstellung. Diskutieren Sie Lösungsansetze mit Ihren Kommilitonen! Zögern Sie auch nicht, in den Tutorien nach groben Lösungsansätzen oder Hilfestellungen zu fragen.<br>
Es kann sich lohnen, für die Navigation in dem bereits erforschten Bereich Weg-Suche Algorithmen wie A* zu kennen. Auf der Seite finden Sie in der rechten Spalte andere Wegsuchealgorithmen.<br>
Das zu lösende Problem ist gross. Zögern Sie nicht spezialisierte Klassen zu erstellen, die Teilprobleme für Sie lösen. Zum Beispiel eine, die den Status der erforschten Welt hällt, oder die Wegsuche für Sie durchführt.<br>
Es gibt nur einen Wumpus. Nutzen Sie diese Tatsache um zu erschliessen, wo der Wumpus ist.<br>
