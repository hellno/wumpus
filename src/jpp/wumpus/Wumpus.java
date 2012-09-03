package jpp.wumpus;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import user.Base;
import rose.world.Color;
import rose.control.InputTuple;
import rose.control.InputTuple.RobotPosition;



public class Wumpus extends Base {

	//abstrahierte Karten:
	//in mapValues werden waerend der Fahrt in jede 'sichere' Koordinate eine 1 gespeichert
	//travelMap speichert die Anzahl der Besuche des Roboters in der jeweiligen Koordinate
	int[][] mapValues = new int[20][20];
	boolean[][] checkedMap = new boolean[20][20];
	int[][] travelMap = new int[20][20];
	//Koordinaten, der aktuellen Position und der vergangenen Position und des Wumpus
	//zum ueberpruefen, dass bei mehreren auswahlmoeglichkeiten nicht zurueck gegangen wird
	int[] coordinates = new int[2];
	int[] lastCoordinates = new int[2];
	int[] wumpCoordinates = new int[2];
	
	//boolean, ob rechte Seite schon erforscht wurde, beschleunigt das Vorgehen auf manchen Maps
	boolean rightSideDiscovery=false;

	//Schwellwert fuer schon besuchte Felder
	int maxVisitCount = 2;
	//Array der besuchten Koordinaten in aufsteigender Reihenfolge
	RobotPosition[] travelCoordinates = new RobotPosition[1000000];
	//Anzahl der Laufbewegungen
	int moveCount=0;
	
	//Robot_ID, aendert sich, wenn Karte mit oder ohne Wumpus erstellt wird
	int ROBOT_ID;
	boolean wumpDead;
	
	//Werte in der Umgebung des akt Feldes im Uhrzeigersin
	int[] surroundingValues = new int[4];
	int[] surroundingTravelValues = new int[4];
	String view = "east";

	public void run() {
		//wenn vorhanden, Wumpus in Karte zeichnen
		if((getInputTuple().robotPositions.get(0).x == 0) && (getInputTuple().robotPositions.get(0).y==0)){
			wumpDead=true;
			ROBOT_ID=0;
			wumpCoordinates[0]=100000;
			wumpCoordinates[1]=100000;
		}
		else{
			wumpDead=false;
			ROBOT_ID=1;
			wumpCoordinates[0] = getInputTuple().robotPositions.get(0).x;
			wumpCoordinates[1] = getInputTuple().robotPositions.get(0).y;
			System.out.println("Wump bei (" + wumpCoordinates[0] + "|" + wumpCoordinates[1] + ")");
			setMapFieldValue(0,wumpCoordinates[0],wumpCoordinates[1]);
			setMapFieldBool(true,wumpCoordinates);
		}
		//Startfeldbesuche manuell um 2 erhoehen, damit am Anfang andere Felder 'attraktiver' sind
		setMapTravelValue(0,0);
		setMapTravelValue(0,0);
		
		//aktuelle Elemente in Karte zeichnen
		setMapFieldSurrounding(getCoordinates());
		setMapFieldValue(1, getCoordinates()[0], getCoordinates()[1]);
		System.out.println("ROBOT_ID: "+ ROBOT_ID);
		
	
		//zusaetzliche Erforschung vor normalem Ablauf
		while(getInputTuple().airDraft==false && getInputTuple().east==true && checkedMap[getInputTuple().robotPositions.get(ROBOT_ID).x+1][0]==false){
			moveIt();
		}
		if(!(getInputTuple().robotPositions.get(ROBOT_ID).y==0 && getInputTuple().robotPositions.get(ROBOT_ID).x==2)){ 
			turnDown();
			moveIt();
			}
		turnRight();
		if(!(getInputTuple().robotPositions.get(ROBOT_ID).y==1 && getInputTuple().robotPositions.get(ROBOT_ID).x==1) 
				&& (!(getInputTuple().robotPositions.get(ROBOT_ID).y==1 && getInputTuple().robotPositions.get(ROBOT_ID).x==2))){
				moveIt();
				moveIt();
			
		}
		while(getInputTuple().airDraft==false && getInputTuple().east==true && getInputTuple().stench==false && checkedMap[getInputTuple().robotPositions.get(ROBOT_ID).x+1][0]==false){
			moveIt();
		}

		turnDown();
		while(getInputTuple().airDraft==false && getInputTuple().south==true && getInputTuple().stench==false && checkedMap[0][getInputTuple().robotPositions.get(ROBOT_ID).y+1]==false){
			moveIt();
		}
		
		//Aktionsschleife
		while(!(getInputTuple().color.equals(Color.YELLOW))){
			
			RobotPosition myRobot = getInputTuple().robotPositions.get(ROBOT_ID);
			int[] temp = new int[2];
			temp[0] = myRobot.x;
			temp[1] = myRobot.y;
			
			//aktuelle Werte der Umgebung und Koordinaten setzen
			setMapFieldSurrounding(temp);
			setCoordinates(temp);
			
			//Weg fuer Heimreise abkuerzen, bisheriges Rumgeirre "vergessen"
			if(coordinates[0]==0 && coordinates[1]==0){
				moveCount=0;
			}
			
			
			//Kartenraender erreicht
			if(getInputTuple().east==false){
				checkedMap[coordinates[0]+1][coordinates[1]]=true;
				if(!rightSideDiscovery){
					turnDown();
					while(getInputTuple().airDraft==false && getInputTuple().stench==false && getInputTuple().south==true && checkedMap[getInputTuple().robotPositions.get(ROBOT_ID).x][getInputTuple().robotPositions.get(ROBOT_ID).y+1]==false){
						moveIt();
						checkedMap[coordinates[0]+1][coordinates[1]]=true;
					}
					rightSideDiscovery=true;
				}
			}
			if(getInputTuple().south==false){
				checkedMap[coordinates[0]][coordinates[1]+1]=true;
			}
			
			surroundingValues = getSurroundingValues(getInputTuple().robotPositions.get(ROBOT_ID));
			surroundingTravelValues = getSurroundingTravelValues(getInputTuple().robotPositions.get(ROBOT_ID));
			
			//unnauffaellig, aber hier gehts ab
			int sum = sum(surroundingValues);
			if(sum>=1){
				goSomewhere(sum);
			}
			
			if(sum==0){
				System.out.println("We're LOST!");
				goHome();
			}
			if(moveCount>2000){
				goHome();
				moveCount=0;
			}
			
		}
		
		System.out.println("Gold gefunden! -> Heimreise");
		printMap();
		goHome();
		done();
	}
	//"intelligente" Wegsuche
	//mit Umkreissuche nach ungefaehrlichen Koordinaten
	//mit bevorzugten, weniger besuchten Koordinaten
	private void goSomewhere(int sum){
		boolean chosen = false;
		int tryCount = 0;
		while(!chosen){
			//nur ein Ausgang, Zufallsverfahren zu langwierig
			if(sum==1){
				if(surroundingValues[0]==1){
					turnUp();
					moveIt();
					chosen=true;
				}
				else if(surroundingValues[1]==1){
					turnRight();
					moveIt();
					chosen=true;
				}
				else if(surroundingValues[2]==1){
					turnDown();
					moveIt();
					chosen=true;
				}
				else if(surroundingValues[3]==1){
					turnLeft();
					moveIt();
					chosen=true;
				}
				chosen=true;
			}
			if(!chosen){
				double rand = Math.random();
				if(rand>=0.75){
					if(surroundingValues[0]==1 && !(lastCoordinates[0]==coordinates[0] && lastCoordinates[1] == (coordinates[1]-1))){
						if(surroundingTravelValues[0]<maxVisitCount || tryCount>3){
							turnUp();
							moveIt();
							chosen=true;
						}
					}
				} else if(rand>=0.5){
					if(surroundingValues[1]==1 && !(lastCoordinates[0]==(coordinates[0]+1) && lastCoordinates[1] == coordinates[1])){
						if(surroundingTravelValues[1]<maxVisitCount || tryCount>3){
							turnRight();
							moveIt();
							chosen=true;
							}
					}
				} else if(rand>=0.25){
					if(surroundingValues[2]==1 && !(lastCoordinates[0]==coordinates[0] && lastCoordinates[1] == (coordinates[1]+1))){
						if(surroundingTravelValues[2]<maxVisitCount || tryCount>3){
							turnDown();
							moveIt();
							chosen=true;
						}
					}
					
				} else if(rand>=0){
					if(surroundingValues[3]==1 && !(lastCoordinates[0]==(coordinates[0]-1) && lastCoordinates[1] == coordinates[1])){
						if(surroundingTravelValues[3]<maxVisitCount || tryCount>3){
							turnLeft();
							moveIt();
							chosen=true;
							}
					}
					
				}
			}
			tryCount++;
		}
	}
	
	//moving back to last position
	private void goBack(){
		////System.out.println("goBack()");
		//setMapFieldBool(true,getCoordinates());
		
		
		if(lastCoordinates[0]==coordinates[0] && lastCoordinates[1]>coordinates[1]){
			//nach unten laufen
			turnDown();
			moveIt();
			moveIt();
		}
		if(lastCoordinates[0]==coordinates[0] && lastCoordinates[1]<coordinates[1]){
			//nach oben laufen
			turnUp();
			moveIt();
			moveIt();
		}
		if(lastCoordinates[0]>coordinates[0] && lastCoordinates[1]==coordinates[1]){
			//nach rechts laufen
			turnRight();
			moveIt();
			moveIt();
		}
		if(lastCoordinates[0]<coordinates[0] && lastCoordinates[1]==coordinates[1]){
			//nach links laufen
			turnLeft();
			moveIt();
			moveIt();
		}
		
	}

	
	private void goHome(){
		
		//Koordinaten aus travelMoves in verkehrter Reihenfolge nach hause laufen
		//System.out.println("moves nach Hause: " + moveCount);
		
		for(int i=moveCount-1;i>=0;i--){
			//System.out.println("i: " + i);
			moveToCoordinate(travelCoordinates[i],true);
		}
		
		
	}
	
	private void moveIt(){
		
		int myTargetY = wumpCoordinates[0]-1;
		System.out.println("Pos.: (" + getInputTuple().robotPositions.get(ROBOT_ID).x + "|" + getInputTuple().robotPositions.get(ROBOT_ID).y + ")" 
				+ " wump bei (" + wumpCoordinates[0] + "|" + wumpCoordinates[1] + ")" 
				+ " zielplatz ist (" + wumpCoordinates[0] + "|" + (wumpCoordinates[1]-1) + ")" );
	
		int[] inFrontOfTheWumpus = {wumpCoordinates[0], wumpCoordinates[1]-1};
		if(Arrays.equals(coordinates,inFrontOfTheWumpus)){
			turnDown();
			shoot();
			System.out.println("Geschossen!");
			ROBOT_ID--;
		}
		//neue Koordinaten setzen
		setLastCoordinates(getCoordinates());
		setMapTravelValue(getCoordinates()[0],getCoordinates()[1]);
		travelCoordinates[moveCount] = getInputTuple().robotPositions.get(ROBOT_ID);
		moveCount++;
		//bewegen
		moveForward();
		
		//Wumpus erreicht
		
		
		
	}
	
	private int sum(int[] array){
		int temp=0;
		for(int i=0;i<array.length;i++){
			
			temp+=array[i];
		}
		return temp;
	}
	
	private void moveToCoordinate(RobotPosition targetCoordinates, boolean wayHome){
		int myX=getInputTuple().robotPositions.get(ROBOT_ID).x;
		int myY=getInputTuple().robotPositions.get(ROBOT_ID).y;
		
		/*//System.out.println("realCoordinates    (" + myX + "|" + myY +")");	
		//System.out.println("targetCoordinates  (" + targetCoordinates.x + "|" + targetCoordinates.y +")");
		*/
		if(myX==targetCoordinates.x && myY==targetCoordinates.y){
			//System.out.println("Hier bin ich doch schon!");
		}
		//System.out.println();
		if(wayHome){
			if(targetCoordinates.x-myX==1){
				turnRight();
				moveForward();
				//System.out.println("moveRight()");
			}
			else if(targetCoordinates.y-myY==1){
				turnDown();
				moveForward();
				//System.out.println("moveDown()");
			}
			else if(myX-targetCoordinates.x==1){
				turnLeft();
				moveForward();
				//System.out.println("moveLeft()");
			}
			else if(myY-targetCoordinates.y==1){
				turnUp();
				moveForward();
				//System.out.println("moveUp()");
			}
		} else {
			if(targetCoordinates.x-myX==1){
				turnRight();
				moveIt();
				//System.out.println("moveRight()");
			}
			else if(targetCoordinates.y-myY==1){
				turnDown();
				moveIt();
				//System.out.println("moveDown()");
			}
			else if(myX-targetCoordinates.x==1){
				turnLeft();
				moveIt();
				//System.out.println("moveLeft()");
			}
			else if(myY-targetCoordinates.y==1){
				turnUp();
				moveIt();
				//System.out.println("moveUp()");
			}
		}
			
		
	}
	
	private int[] getSurroundingValues(RobotPosition myCoordinates){
		int links = 0;
		int rechts =0;
		int oben = 0;
		int unten = 0;
		//testen, ob koordinaten auf der map sind und ob diese schon markiert sind
		if(myCoordinates.x!=0 && !checkedMap[myCoordinates.x-1][myCoordinates.y])
			links= mapValues[myCoordinates.x-1][myCoordinates.y];
		if(myCoordinates.y!=0 && !checkedMap[myCoordinates.x][myCoordinates.y-1])
			oben = mapValues[myCoordinates.x][myCoordinates.y-1];
		if(!checkedMap[myCoordinates.x+1][myCoordinates.y])
			rechts = mapValues[myCoordinates.x+1][myCoordinates.y];
		if(!checkedMap[myCoordinates.x][myCoordinates.y+1])
			unten = mapValues[myCoordinates.x][myCoordinates.y+1];
			
		int[] temp = {oben,rechts,unten,links};
		if(myCoordinates.x==9 && myCoordinates.y==9){
		System.out.println("Werte: oben " + temp[0] + 
				" rechts " + temp[1] + 
				" unten " + temp[2] + 
				" links " + temp[3]);
		System.out.println("(" + myCoordinates.x + "|" + myCoordinates.y + ")");  
		printMap();
		}
		return temp;
	}
	
	private int[] getSurroundingTravelValues(RobotPosition myCoordinates){
		int links = 0;
		int rechts =0;
		int oben = 0;
		int unten = 0;
		//testen, ob koordinaten auf der map sind und ob diese schon markiert sind
		if(myCoordinates.x!=0 && !checkedMap[myCoordinates.x-1][myCoordinates.y])
			links= travelMap[myCoordinates.x-1][myCoordinates.y];
		if(myCoordinates.y!=0 && !checkedMap[myCoordinates.x][myCoordinates.y-1])
			oben = travelMap[myCoordinates.x][myCoordinates.y-1];
		if(!checkedMap[myCoordinates.x+1][myCoordinates.y])
			rechts = travelMap[myCoordinates.x+1][myCoordinates.y];
		if(!checkedMap[myCoordinates.x][myCoordinates.y+1])
			unten = travelMap[myCoordinates.x][myCoordinates.y+1];
		int[] temp = {oben,rechts,unten,links};
		
		/*//System.out.println("Travel: oben " + temp[0] + 
				" rechts " + temp[1] + 
				" unten " + temp[2] + 
				" links " + temp[3]);
		*/
		return temp;
	
	}
	
	private void turnRight(){
		switch(view){
		case("north"):
			rotateRight();
			break;
		case("west"):
			rotateRight();
			rotateRight();
			break;
		case("south"):
			rotateLeft();
			break;
		}
		view="east";
		
	}
	
	private void turnLeft(){
		switch(view){
		case("north"):
			rotateLeft();
			break;
		case("east"):
			rotateRight();
			rotateRight();
			break;
		case("south"):
			rotateRight();
			break;
		}
		view="west";
		
	}
	private void turnUp(){
		switch(view){
		case("west"):
			rotateRight();
			break;
		case("east"):
			rotateLeft();
			break;
		case("south"):
			rotateLeft();
			rotateLeft();
			break;
		}
		view="north";
		
	}
	
	private void turnDown(){
		switch(view){
		case("north"):
			rotateRight();
			rotateRight();
			break;
		case("west"):
			rotateLeft();
			break;
		case("east"):
			rotateRight();
			break;
		}
		view="south";
		
	}
	
	private int[] getCoordinates(){
		return coordinates;
	}
	
	private void setCoordinates(int[] newCoordinates){
		coordinates[1] = newCoordinates[1];
		coordinates[0] = newCoordinates[0];
		
		////System.out.println("Neue Position ist (" + coordinates[0] + "|" + coordinates[1] + ")");
		
	}
	
	private void setLastCoordinates(int[] newCoordinates){
		lastCoordinates[1] = newCoordinates[1];
		lastCoordinates[0] = newCoordinates[0];
		
		////System.out.println("Alte Position war (" + lastCoordinates[0] + "|" + lastCoordinates[1] + ")");
	
	}
	
	private void setMapFieldSurrounding(int[] myCoordinates){
		//System.out.println("vor dem mapFieldSurroundings");
		
		if(getInputTuple().airDraft==false){
			
			setMapFieldValue(1,myCoordinates[0],myCoordinates[1]-1);
			setMapFieldValue(1,myCoordinates[0]-1,myCoordinates[1]);
			if(getInputTuple().south==true)
				setMapFieldValue(1,myCoordinates[0],myCoordinates[1]+1);
			if(getInputTuple().east==true)
				setMapFieldValue(1,myCoordinates[0]+1,myCoordinates[1]);
		}
		//System.out.println("mapFieldSurroundings gesetzt");
	}
	
	private void setMapFieldValue(int to, int x, int y){
		//check ob ziel in grenzen und kein normales Feld ueberschreibt
		if(x>=0 && y>=0 && mapValues[x][y]!=1)
			mapValues[x][y]=1;
	}
	
	private void setMapTravelValue(int x, int y){
		//check ob ziel in grenzen und erhoeht besuchsanzahl um eins
		if(x>=0 && y>=0)
			travelMap[x][y]++;
	}
	
	private void setMapFieldBool(boolean bool, int[] array){
		checkedMap[array[0]][array[1]]=bool;
	}

	//Simple Wegsuche
	public void moveTo(int targetX, int targetY){
		//System.out.println("moving to (" + targetX + "," + targetY + ")");
		
	}
	
	public void printMap(){
		for(int i=0;i<mapValues.length;i++){
			for(int j=0;j<mapValues[i].length;j++){
				System.out.print(mapValues[j][i]+" ");
			}
			System.out.println("");
			
		}
		System.out.println("");
		
		//checkedMap:
		for(int i=0;i<checkedMap.length;i++){
			for(int j=0;j<checkedMap[i].length;j++){
				if(checkedMap[j][i])
					System.out.print("t ");
				else
					System.out.print("f ");
			}
			System.out.println("");
			
		}
		System.out.println("");
		
		//travelMap:
		for(int i=0;i<travelMap.length;i++){
			for(int j=0;j<travelMap[i].length;j++){
				//System.out.print(travelMap[j][i]+" ");
			}
			System.out.println("");
			
		}
		System.out.println("");
		
		
	}
	
	public static void main(String[] args) throws IOException {
		Wumpus m = new Wumpus("localhost", 51160, 50);
		m.run();
}

	public Wumpus(String hostname, int port, int retries) throws IOException {
		super(hostname, port, retries);
	}
	/*
	 * Gewichteter Wegsuchalgorithmus, ohne Ueberarbeitung
	boolean equalTravel = false;
	int placesToGo = 0;
	int temp=0;
	for(int i = 0;i<4;i++){
		if(surroundingValues[i]==1)
			placesToGo++;
		temp+=surroundingTravelValues[i];
	}
	if(temp==surroundingTravelValues[0]/placesToGo)
		equalTravel=true;
	if(!equalTravel){
	
		int minPlace = findMinPlace(surroundingTravelValues);
		//System.out.println("minPlace: " + minPlace);
		switch(minPlace){
		case(0):
			if(surroundingValues[0]==1 && !(lastCoordinates[0]==coordinates[0] && lastCoordinates[1] == (coordinates[1]-1))){
				turnUp();
				moveIt();
				chosen=true;
			}
			break;
		case(1):
			if(surroundingValues[1]==1 && !(lastCoordinates[0]==(coordinates[0]+1) && lastCoordinates[1] == coordinates[1])){
				turnRight();
				moveIt();
				chosen=true;
			}
		break;
		case(2):
			if(surroundingValues[2]==1 && !(lastCoordinates[0]==coordinates[0] && lastCoordinates[1] == (coordinates[1]+1))){
				turnDown();
				moveIt();
				chosen=true;
			}
			break;
		case(3):
			if(surroundingValues[3]==1 && !(lastCoordinates[0]==(coordinates[0]-1) && lastCoordinates[1] == coordinates[1])){
				turnLeft();
				moveIt();
				chosen=true;
			}
			break;
		}
	}*/
	
}
