import java.util.ArrayList;

public class Tile
{
    Type type;
    //itt eltároljuk a szomszédokat (észak/dél/kelet/nyugat és kombinációi)
    Tile N; //felül
    Tile NE; //jobb felül
    Tile E; //jobb
    Tile SE; //jobb lenn
    Tile S; //lenn
    Tile SW; //bal lenn
    Tile W; //bal
    Tile NW; //bal fenn

    private double x1,x2,y1,y2;


    Tile()
    {
        type = Type.empty;
    }

    void set(Type t)
    {
        this.type=t;
    }

    void setcoords(double x1,double y1,double x2,double y2) //beállítja a koordinátákat ami alapján a négyzet rajzolva lett
    {
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
    }

    boolean inside(double x,double y) //megadja, hogy benne van-e az adott pont a négyzetben
    {
        if(this.x1<=x && this.x2>=x && this.y1<=y && this.y2>=y) return true; else return false;
    }
}
