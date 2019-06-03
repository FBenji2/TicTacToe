import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Table
{
    private Tile[][] table;
    int size; //táblaméret
    int w; //mennyi kell zsinórban a nyeréshez
    Type next,first=Type.X;
    boolean won,started;
    GraphicsContext gc;
    ArrayList<Step> steps; //eltároljuk az eddigi lépéseket
    int nosteps; //eltároljuk, hogy eddig hány lépés volt (undo nélkül a steps mérete, de a stepsen belül ezzel fogunk mozogni)

    Table(int s,int w, GraphicsContext gc) //létrehozza a táblát csupa üresekkel és kirajzolja az ablakba
    {
        this.gc = gc;
        nosteps=-1;
        steps = new ArrayList<Step>();
        started=false;
        gc.setFill(Color.GRAY);
        gc.fillRect(0,0,600,600); //beállítunk egy szürke hátteret
        this.size = s;
        won = false;
        next = first;
        if(w>s) this.w = s; else this.w = w;
        table = new Tile[s][s]; //lefoglalunk S*S helyet
        for(int i=0;i<s;i++) //és mindegyiket inicializáljuk
        {
            for(int j=0;j<s;j++)
            {
                table[i][j] = new Tile();
            }
        }
        for(int i=0;i<s;i++) //beállítjuk a szomszédokat is
        {
            for(int j=0;j<s;j++)
            {
                if(i-1>=0)
                {
                    table[i][j].N = table[i-1][j];
                }
                if(i-1>=0 && j+1<s)
                {
                    table[i][j].NE=table[i-1][j+1];
                }
                if(j+1<s)
                {
                    table[i][j].E=table[i][j+1];
                }
                if(i+1<s && j+1<s)
                {
                    table[i][j].SE=table[i+1][j+1];
                }
                if(i+1<s)
                {
                    table[i][j].S=table[i+1][j];
                }
                if(i+1<s && j-1>=0)
                {
                    table[i][j].SW=table[i+1][j-1];
                }
                if(j-1>=0)
                {
                    table[i][j].W=table[i][j-1];
                }
                if(i-1>=0 && j-1>=0)
                {
                    table[i][j].NW=table[i-1][j-1];
                }
            }
        }
        this.print(gc);
    }

    Tile get(int i,int j) //i. sor j. elemével tér vissze
    {
        return table[i][j];
    }

    private void draw(int i,int j, Type t) //kirajzolunk egy adott helyre egy adott elemet (üres/X/O)
    {
        switch (t)
        {
            case empty:
                gc.fillRect((j+1)*(60/(1.1* size +0.1))+j*(600/(1.1* size +0.1)),
                            (i+1)*(60/(1.1* size +0.1))+i*(600/(1.1* size +0.1)),
                            600/(1.1* size +0.1),
                            600/(1.1* size +0.1));
                table[i][j].setcoords((j+1)*(60/(1.1* size +0.1))+j*(600/(1.1* size +0.1)),
                                      (i+1)*(60/(1.1* size +0.1))+i*(600/(1.1* size +0.1)),
                                      (j+1)*(60/(1.1* size +0.1))+j*(600/(1.1* size +0.1))+600/(1.1* size +0.1),
                                      (i+1)*(60/(1.1* size +0.1))+i*(600/(1.1* size +0.1))+600/(1.1* size +0.1));
                break;
            case O:
                draw(i,j,Type.empty);
                gc.setStroke(Color.DARKBLUE);
                gc.setLineWidth(80/(1.1* size +0.1));
                gc.strokeOval((j+1)*(60/(1.1* size +0.1))+j*(600/(1.1* size +0.1))+60/(1.1* size +0.1),
                              (i+1)*(60/(1.1* size +0.1))+i*(600/(1.1* size +0.1))+60/(1.1* size +0.1),
                              600/(1.1* size +0.1)-120/(1.1* size +0.1)
                             ,600/(1.1* size +0.1)-120/(1.1* size +0.1));
                break;
            case X:
                draw(i,j,Type.empty);
                gc.setStroke(Color.DARKRED);
                gc.setLineWidth(60/(1.1* size +0.1));
                gc.strokeLine((j+1)*(60/(1.1* size +0.1))+j*(600/(1.1* size +0.1))+60/(1.1* size +0.1),
                              (i+1)*(60/(1.1* size +0.1))+i*(600/(1.1* size +0.1))+60/(1.1* size +0.1),
                              (j+1)*(60/(1.1* size +0.1))+j*(600/(1.1* size +0.1))+60/(1.1* size +0.1)+600/(1.1* size +0.1)-120/(1.1* size +0.1),
                              (i+1)*(60/(1.1* size +0.1))+i*(600/(1.1* size +0.1))+60/(1.1* size +0.1)+600/(1.1* size +0.1)-120/(1.1* size +0.1));

                gc.strokeLine((j+1)*(60/(1.1* size +0.1))+j*(600/(1.1* size +0.1))+60/(1.1* size +0.1)+600/(1.1* size +0.1)-120/(1.1* size +0.1),
                              (i+1)*(60/(1.1* size +0.1))+i*(600/(1.1* size +0.1))+60/(1.1* size +0.1),
                              (j+1)*(60/(1.1* size +0.1))+j*(600/(1.1* size +0.1))+60/(1.1* size +0.1),
                              (i+1)*(60/(1.1* size +0.1))+i*(600/(1.1* size +0.1))+60/(1.1* size +0.1)+600/(1.1* size +0.1)-120/(1.1* size +0.1));
                break;
        }
    }

    Type step(int i,int j) //lépteti a dolgokat, jelzi azt is, hogy mi volt az utolsó hely és visszatér azzal aki nyert ha van ilyen
    {
        started = true;
        while(nosteps != steps.size()-1)
        {
            steps.remove(steps.size()-1);
        }
        nosteps++;
        gc.setFill(Color.YELLOWGREEN);
        set(i,j,next);
        steps.add(new Step(i,j,next));
        gc.setFill(Color.DARKGRAY);
        if(nosteps!=0) draw(steps.get(nosteps-1).i,steps.get(nosteps-1).j,steps.get(nosteps-1).t);
        if(next == Type.X) next = Type.O; else
        if(next == Type.O) next = Type.X;
        return whowon();
    }

    void undo(GraphicsContext gc) //vissza tudunk egyet lépni vele
    {
        gc.setFill(Color.DARKGRAY);
        set(steps.get(nosteps).i,steps.get(nosteps).j,Type.empty);
        if(next == Type.X) next = Type.O; else
        if(next == Type.O) next = Type.X;
        nosteps--;
        if(nosteps == -1)
        {
            started=false;
            return;
        }
        gc.setFill(Color.YELLOWGREEN);
        draw(steps.get(nosteps).i,steps.get(nosteps).j,steps.get(nosteps).t);
    }

    void redo(GraphicsContext gc)
    {
        if(nosteps != -1)
        {
            gc.setFill(Color.DARKGRAY);
            draw(steps.get(nosteps).i,steps.get(nosteps).j,steps.get(nosteps).t);
        }
        if(next == Type.X) next = Type.O; else
        if(next == Type.O) next = Type.X;
        nosteps++;
        gc.setFill(Color.YELLOWGREEN);
        set(steps.get(nosteps).i,steps.get(nosteps).j,steps.get(nosteps).t);
    }

    private void print(GraphicsContext gc) //kirajzolja a tábla jelenlegi állását
    {
        gc.setFill(Color.DARKGRAY);
        for(int i = 0; i< size; i++)
        {
            for(int j = 0; j< size; j++)
            {
                draw(i,j,table[i][j].type);
            }
        }
    }

    void set(int i,int j,Type t) //adott pozícióban lévő elemet átállít és kirajzolja és beállítja a szomszéd tömböket
    {
        table[i][j].set(t);
        draw(i,j,t);
    }
    void clear() //minden elemet üresre állít
    {
        nosteps=-1;
        steps = new ArrayList<Step>();
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
            {
                table[i][j].set(Type.empty);
            }
        won = false;
        started = false;
        next = first;
        print(gc);
    }

    void print()
    {
        for(int i = 0; i< size; i++)
        {
            for(int j = 0; j< size; j++)
            {
                switch(table[i][j].type)
                {
                    case X:
                        System.out.print("X");
                        break;
                    case empty:
                        System.out.print("E");
                        break;
                    case O:
                        System.out.print("O");
                        break;
                }
            }
            System.out.println();
        }
        System.out.println();

    }

    void setsize(int s)
    {
        started=false;
        nosteps=-1;
        steps = new ArrayList<Step>();
        gc.setFill(Color.GRAY);
        gc.fillRect(0,0,600,600); //beállítunk egy szürke hátteret
        this.size = s;
        won = false;
        next = first;
        table = new Tile[s][s]; //lefoglalunk S*S helyet
        for(int i=0;i<s;i++) //és mindegyiket inicializáljuk
        {
            for(int j=0;j<s;j++)
            {
                table[i][j] = new Tile();
            }
        }
        for(int i=0;i<s;i++) //beállítjuk a szomszédokat is
        {
            for(int j=0;j<s;j++)
            {
                if(i-1>=0)
                {
                    table[i][j].N = table[i-1][j];
                }
                if(i-1>=0 && j+1<s)
                {
                    table[i][j].NE=table[i-1][j+1];
                }
                if(j+1<s)
                {
                    table[i][j].E=table[i][j+1];
                }
                if(i+1<s && j+1<s)
                {
                    table[i][j].SE=table[i+1][j+1];
                }
                if(i+1<s)
                {
                    table[i][j].S=table[i+1][j];
                }
                if(i+1<s && j-1>=0)
                {
                    table[i][j].SW=table[i+1][j-1];
                }
                if(j-1>=0)
                {
                    table[i][j].W=table[i][j-1];
                }
                if(i-1>=0 && j-1>=0)
                {
                    table[i][j].NW=table[i-1][j-1];
                }
            }
        }
        this.print(gc);
    }

    Type whowon() //megadja, hogy ki nyert. végigmegyünk minden elemen amivel még nyerhetünk
    {
        won = false;
        for(int i = 0; i< size; i++)
        {
            for(int j = 0; j< size; j++)
            {
                if(table[i][j].type!=Type.empty && (i+w<= size || j+w<= size))
                {
                    Type winner = table[i][j].type; //az aktuális vizsgált elemtől indulunk el különböző irányokba
                    Tile horiz = table[i][j];
                    Tile vert = table[i][j];
                    Tile ldiag = table[i][j];
                    Tile udiag = table[i][j];

                    int hwon = 0;
                    int vwon = 0;
                    int dlwon = 0;
                    int duwon = 0;
                    boolean hdone = false;
                    boolean vdone = false;
                    boolean ldone = false;
                    boolean udone = false;
                    for (int k = 0; k < w; k++) {
                        if (winner == horiz.type && !hdone) {
                            hwon++;
                            if (horiz.E != null) horiz = horiz.E;
                            else hdone = true;
                        } else hwon = 0;
                        if (winner == vert.type && !vdone) {
                            vwon++;
                            if (vert.S != null) vert = vert.S;
                            else vdone = true;
                        } else vwon = 0;
                        if (winner == ldiag.type && !ldone) {
                            dlwon++;
                            if (ldiag.SE != null) ldiag = ldiag.SE;
                            else ldone = true;
                        } else dlwon = 0;
                        if (winner == udiag.type && !udone) {
                            duwon++;
                            if (udiag.NE != null) udiag = udiag.NE;
                            else udone = true;
                        } else duwon = 0;
                    }
                    if (hwon == w || vwon == w || dlwon == w || duwon == w)
                    {
                        won=true;
                        return table[i][j].type;
                    }
                }
            }
        }
        return Type.empty;
    }

    void changefirst()
    {
        if(first == Type.O) first = Type.X;
        else if(first == Type.X) first = Type.O;
        if(!started) next=first;
    }
}
