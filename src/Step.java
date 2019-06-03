public class Step
{
    int i,j; //eltároljuk, hogy hova került
    Type t; //micsoda

    Step(int i,int j,Type t)
    {
        this.i=i;
        this.j=j;
        this.t=t;
    }
    void print()
    {
        System.out.println("i: " + i + " j: " + j + " Type: " + t);
    }
}
