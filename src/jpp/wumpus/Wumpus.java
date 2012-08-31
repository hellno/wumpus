package jpp.wumpus;

import java.io.IOException;
import java.util.List;

import user.Base;
import rose.world.Color;
import rose.control.InputTuple;
import rose.control.InputTuple.RobotPosition;

public class Wumpus extends Base {
	
	//ausgefuehrte Bewegungen werden im moves-array abgespeichert
	//0=moveForward
	//1=rotateLeft
	//2=rotateRight
	int[] moves = new int[100];
	
	//Blickrichtung
	String view = "east";
	//Kartenabstraktion [x][y], oben links = [0][0]
	//1=normales Feld
	//2=airDraft/Loch, wegen ungenauem Check hat '1' Prioritaet ueber '2'
	//3=Wump
	int[][] map = new int[20][20];
	int x,y = 0;
	int xMax, yMax=0;
	int id,creatureCount=0;
	int multi = 1;
	int[] wumpus = new int[2];
	int[] lastPosition = new int[2];
	int count = 0;
	
	//Testvariablen
	
	
	public void run() {
		//Wumpus in Karte speichern
		creatureCount=getInputTuple().robotPositions.size();
		if(creatureCount>1){
		wumpus[0] = getInputTuple().robotPositions.get(0).x;
		wumpus[1] = getInputTuple().robotPositions.get(0).y;
		map[wumpus[0]][wumpus[1]] = 3;
		System.out.println("Wumpus bei (" + wumpus[0] + "," + wumpus[1] + ")");
		}
		
		while(getInputTuple().east==true || (x==wumpus[0] && y==wumpus[1])){
			moveRight();
		}
		moveBack();
		xMax=x;
		moveTo(2,9);
		//System.out.println("xMax: " + xMax);

		surrounding();
		while((getInputTuple().color!=Color.YELLOW)){
			if(getInputTuple().airDraft==false)
				
				//Explorationsalgorithmus:
				//move auf neues Feld -> evtl. 3 weitere Möglichkeiten

				int left, right, up, down = 0;
				boolean l,r,u,d=false;
				//es kann sich nur eine Position geändert haben -> else if
				if(oldPosition[0]-x>0){
					//left
					right = map[x+1][y];
					up = map[x][y+1]; 
					down = map[x][y-1];
					sum = right+up+down;
					//Richtungen die einen Ausweg darstellen werden auf 'true' gesetzt
					if(right==1)
						r=true;
					if(up==1)
						u=true;
					if(down==1)
						d=true;

					if(sum==0){
						setMapField(0,x,y);
						moveLeft();
					}
					if(sum==1){
						//Suche nach Ausweg, der nicht die letzte Position ist
						if(r)
							moveRight();
						if(u)
							moveUp();
						if(d)
							moveDown();
					}
					if(sum==2){
						//Suche nach Auswegen und entscheide zufällig zwischen Ihnen
						int rand = Math.random();
						if(r&&u){
							if(rand>=0.5)
								turnRight();
							else
								turnUp();
						}
						if(r&&d){
							if(rand>=0.5)
								turnRight();
							else
								turnDown();
						}
						if(u&&d){
							if(rand>=0.5)
								turnUp();
							else
								turnDown();
						}
					}
					if(sum==3){
						//Suche nach Auswegen und entscheide zufällig zwischen Ihnen
						int rand = Math.random();
						if(rand>=0.66)
							turnRight();
						else if(rand>=0.33)
							turnUp();
						else 
							turnDown();
					}

				}	
				} 
				else if(oldPosition[0]-x<0){
					//right
					left = map[x-1][y];
					up = map[x][y+1]; 
					down = map[x][y-1];
					sum = left+up+down;
					//Richtungen die einen Ausweg darstellen werden auf 'true' gesetzt
					if(left==1)
						l=true;
					if(up==1)
						u=true;
					if(down==1)
						d=true;

					if(sum==0){
						setMapField(0,x,y);
						moveRight();
					}
					if(sum==1){
						//Suche nach Ausweg, der nicht die letzte Position ist
						if(l)
							moveLeft();
						if(u)
							moveUp();
						if(d)
							moveDown();
					}
					if(sum==2){
						//Suche nach Auswegen und entscheide zufällig zwischen Ihnen
						int rand = Math.random();
						if(l&&u){
							if(rand>=0.5)
								turnLeft();
							else
								turnUp();
						}
						if(l&&d){
							if(rand>=0.5)
								turnLeft();
							else
								turnDown();
						}
						if(u&&d){
							if(rand>=0.5)
								turnUp();
							else
								turnDown();
						}
					}
					if(sum==3){
						//Suche nach Auswegen und entscheide zufällig zwischen Ihnen
						int rand = Math.random();
						if(rand>=0.66)
							turnLeft();
						else if(rand>=0.33)
							turnUp();
						else 
							turnDown();
					}


				}	
				} 
				else if(oldPosition[1]-y>0){
					//up
					left = map[x-1][y];
					right = map[x+1][y];
					down = map[x][y-1];

					sum = left+right+down;
					//Richtungen die einen Ausweg darstellen werden auf 'true' gesetzt
					if(right==1)
						r=true;
					if(left==1)
						l=true;
					if(down==1)
						d=true;

					if(sum==0){
						setMapField(0,x,y);
						moveUp();
					}
					if(sum==1){
						//Suche nach Ausweg, der nicht die letzte Position ist
						if(r)
							moveRight();
						if(l)
							moveLeft();
						if(d)
							moveDown();
					}
					if(sum==2){
						//Suche nach Auswegen und entscheide zufällig zwischen Ihnen
						int rand = Math.random();
						if(r&&l){
							if(rand>=0.5)
								turnRight();
							else
								turnLeft();
						}
						if(r&&d){
							if(rand>=0.5)
								turnRight();
							else
								turnDown();
						}
						if(l&&d){
							if(rand>=0.5)
								turnLeft();
							else
								turnDown();
						}
					}
					if(sum==3){
						//Suche nach Auswegen und entscheide zufällig zwischen Ihnen
						int rand = Math.random();
						if(rand>=0.66)
							turnRight();
						else if(rand>=0.33)
							turnLeft();
						else 
							turnDown();
					}

				}	
				} 
				else if(oldPosition[1]-y<0){
					//down
					left = map[x-1][y];
					right = map[x+1][y];
					up = map[x][y+1]; 


					sum = right+up+left;
					//Richtungen die einen Ausweg darstellen werden auf 'true' gesetzt
					if(right==1)
						r=true;
					if(up==1)
						u=true;
					if(left==1)
						l=true;

					if(sum==0){
						setMapField(0,x,y);
						moveDown();
					}
					if(sum==1){
						//Suche nach Ausweg, der nicht die letzte Position ist
						if(r)
							moveRight();
						if(u)
							moveUp();
						if(l)
							moveLeft();
					}
					if(sum==2){
						//Suche nach Auswegen und entscheide zufällig zwischen Ihnen
						int rand = Math.random();
						if(r&&u){
							if(rand>=0.5)
								turnRight();
							else
								turnUp();
						}
						if(r&&l){
							if(rand>=0.5)
								turnRight();
							else
								turnLeft();
						}
						if(u&&l){
							if(rand>=0.5)
								turnUp();
							else
								turnLeft();
						}
					}
					if(sum==3){
						//Suche nach Auswegen und entscheide zufällig zwischen Ihnen
						int rand = Math.random();
						if(rand>=0.66)
							turnRight();
						else if(rand>=0.33)
							turnUp();
						else 
							turnLeft();
					}

				}
				
				//Ende Explorationsalgorithmus
				
				
		}
		printMap();
		
		done();
		
	}
	
	public void moveIt(){
		x = getInputTuple().robotPositions.get(1).x;
		y = getInputTuple().robotPositions.get(1).y;
		if(getInputTuple().airDraft==false)
			setMapFieldSurrounding(1,x,y);
		lastPosition[0]=x;
		lastPosition[1]=y;
		moveForward();
		//System.out.println("count: " + count);
		moves[count] = 0;
		count++;
		
	}
	
	public int[] surrounding(){
		int rechts, links, oben, unten;
		int[] target = new int[2];
		if(x>1)
			links = map[x-1][y];
		else
			links = 0;
		if(y>1)
			oben = map[x][y-1];
		else
			oben = 0;
		rechts = map[x+1][y];
		unten = map[x][y+1];
		
		
		System.out.println("Alte     Position (" + lastPosition[0] + "," + lastPosition[1] + ")");
		System.out.println("Aktuelle Position (" + x + "," + y + ")");
		System.out.println("links" + links +" oben" + oben +" unten" + unten +" rechts" + rechts);
		double rand; 
		boolean check = true;
		int maxDurchlauf=0;
		while(check || maxDurchlauf < 20){
			rand = Math.random();
			
			if(rand>=0.75 && links==1){
				target[0] = x-1;
				target[1] = y;
			}
			else if(rand>=0.5 && rechts==1){
				target[0] = x+1 ;
				target[1] = y ;
			}
			else if(rand>=0.25 && unten==1){
				target [0] = x ;
				target [1] = y+1 ;
			}
			else if(rand>=0 && oben==1){
				target [0] = x ;
				target [1] = y-1 ;
			}
			if(target[0]!=lastPosition[0] || target[1]!=lastPosition[1])
				check=false;
			maxDurchlauf++;
		}
		System.out.println("Neue Position : (" + (target[0]) + "," + (target[1]) + ")");
		
		return null;
	}

	public static void main(String[] args) throws IOException {
			Wumpus m = new Wumpus("localhost", 51160, 50);
			m.run();
	}

	public Wumpus(String hostname, int port, int retries) throws IOException {
		super(hostname, port, retries);
	}
	
	//Simple Wegsuche
	public void moveTo(int targetX, int targetY){
		System.out.println("moving to (" + targetX + "," + targetY + ")");
		while(targetX != x){
			x = getInputTuple().robotPositions.get(1).x;
			//System.out.println("in x schleife; x:" + x + " view: " + view);
			if(targetX > x)
				moveRight();
			else if(targetX < x)
				moveLeft();
		}
		while(targetY != y){
			//System.out.println("in y schleife");
			y = getInputTuple().robotPositions.get(1).y;
			if(targetY > y)
				moveDown();
			else if(targetY < y)
				moveUp();
		}
		
		
	}
	
	public void moveUp(){
		while(view!="north"){
			turnLeft();
		}
		moveIt();
	}
	public void moveDown(){
		while(view!="south"){
			turnLeft();
		}
		moveIt();
	}
	public void moveRight(){
		while(view!="east"){
			turnLeft();
		}
		moveIt();
	}
	public void moveLeft(){
		while(view!="west"){
			turnLeft();
		}
		moveIt();
	}
	
	
	public void moveBack(){
		//vor dem Weglaufen noch nach dem Wind schauen
		if(getInputTuple().airDraft==false)
			setMapFieldSurrounding(1,x,y);
		int turnCount = count;
		//umdrehen und moves in verkehrter Reihenfolge ausfuehren
		rotateLeft();
		rotateLeft();
		for(int i=0;i<=turnCount;i++){
			switch(moves[i]){
			case 0: moveForward(); break;
			case 1: rotateRight(); break;
			case 2: rotateLeft(); break;
			}
		}
		System.out.println("return to base");
		moveLeft();
		rotateLeft();
		rotateLeft();
		
		for(int i=0;i<moves.length;i++){
			moves[i]=0;
		}
		count=0;
		
	}
	
	private void setMapFieldSurrounding(int to, int x, int y){
		setMapField(to,x,y+1);
		setMapField(to,x,y-1);
		setMapField(to,x+1,y);
		setMapField(to,x-1,y);
	}
	
	private void setMapField(int to, int x, int y){
		//check ob ziel in grenzen und kein normales Feld ueberschreibt
		if(x>=0 && y>=0 && map[x][y]!=1)
			map[x][y]=to;
	}
	
	
	
	public void turnLeft(){
		rotateLeft();
		switch (view){
		case "north":view="west"; break;
		case "east": view="north"; break;
		case "south":view="east"; break;
		case "west" :view="south"; break;
		default: System.out.println("view: invalid direction!");
		}
		moves[count] = 1;
		count++;
	}
	
	public void turnRight(){
		rotateRight();
		switch (view){
		case "north":view="east"; break;
		case "east": view="south"; break;
		case "south":view="west"; break;
		case "west" :view="north"; break;
		default: System.out.println("view: invalid direction!");
		}
		moves[count] = 2;
		count++;
	}
	
	public void printMap(){
		for(int i=0;i<map.length;i++){
			for(int j=0;j<map[i].length;j++){
				System.out.print(map[j][i]+" ");
			}
			System.out.println("");
			
		}
		System.out.println("");
	}
	
	public void printArray(int[] array){
		for(int i=0;i<array.length;i++){
			System.out.print(array[i]+ " ");
		}
		System.out.println();
	}
	
	
	
}
/*switch(view){
case "east": if(getInputTuple().east==false) turnRight();
			 break;
case "north": if(getInputTuple().north==false) turnRight();
 			 break;
case "west": if(getInputTuple().west==false) turnRight();
 			 break;
case "south": if(getInputTuple().south==false) turnLeft();
			 break;
default: System.out.println("run(): switch(view) ERROR"); break;
}	
*/
