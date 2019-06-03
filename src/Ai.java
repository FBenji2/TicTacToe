import java.util.ArrayList;

public class Ai
{

    ArrayList<ArrayList<Node>> graph = new ArrayList<>();

    Ai(Table table) //konstruktorban megadjuk a táblát és az alapján felépítjük a check tömböt is
    {
        graph.add(new ArrayList<>());
        graph.get(0).add(new Node(null,table));
    }

    ArrayList<ArrayList<Integer>> wheretostep(Table table) //megadja, hogy a táblán melyik üres helyekre kell onnan lépni
    {
        ArrayList<ArrayList<Integer>> ret = new ArrayList<>();
        for(int i=0;i<table.size;i++)
        {
            for(int j=0;j<table.size;j++)
            {
                if(table.get(i,j).type == null)
                {
                    if(table.get(i,j).N.type != Type.empty ||
                       table.get(i,j).NE.type != Type.empty ||
                       table.get(i,j).E.type != Type.empty ||
                       table.get(i,j).SE.type != Type.empty ||
                       table.get(i,j).S.type != Type.empty  ||
                       table.get(i,j).SW.type != Type.empty ||
                       table.get(i,j).W.type != Type.empty ||
                       table.get(i,j).NW.type != Type.empty)
                    {
                        ret.add(new ArrayList<>());
                        ret.get(ret.size()-1).add(i);
                        ret.get(ret.size()-1).add(j);
                    }
                }
            }
        }
        return ret;
    }

    void build(int steps)
    {
        for(int l=0;l<steps;l++) {
            graph.add(new ArrayList<>());
            for (int i = 0; i < graph.get(graph.size() - 1).size(); i++) {
                ArrayList<ArrayList<Integer>> wheretocheck = wheretostep(graph.get(graph.size() - 1).get(i).table);
                for (int j = 0; j < wheretocheck.size(); j++) {
                    Table s = graph.get(graph.size() - 1).get(i).table;
                    s.step(wheretocheck.get(j).get(0), wheretocheck.get(j).get(1));
                    graph.get(graph.size() - 1).add(new Node(s));
                }
            }
        }
    }
}