import java.util.ArrayList;

public class Node //bejárási gráfcsúcs
{
    Node parent; //eltároljuk a szülőt
    Table table; //csúcs a táblából áll


    Node(Table table)
    {
        parent = null;
        this.table = table;
    }
    Node(Node parent, Table table)
    {
        this.parent = parent;
        this.table = table;
    }
}
