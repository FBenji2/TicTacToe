import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application
{

    public static void main(String [] args)
    {
       launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(false);
        primaryStage.setTitle("Tic tac toe");
        Group root = new Group();
        Canvas canvas = new Canvas(600, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        Table table = new Table(3,5,gc); //generáljuk a táblát és kirajzoljuk
        Ai ai = new Ai(table);
        vbox.getChildren().addAll(canvas,hbox);

        Label cursize = new Label("Size: " + table.size);
        Label curwin = new Label("Consecutive needed: " + table.w);
        Label whowon = new Label();

        Button clear = new Button("Clear");
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                table.clear();
                whowon.setText("");
            }
        });
        Button minussize = new Button("-");
        minussize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                whowon.setText("");
                if(table.size >1) table.size--;
                if(table.w>table.size) table.w=table.size;
                curwin.setText("Consecutive needed: " + table.w);
                cursize.setText("Size: " + table.size);
                table.setsize(table.size);
            }
        });
        Button plussize = new Button("+");
        plussize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                whowon.setText("");
                table.size++;
                cursize.setText("Size: " + table.size);
                table.setsize(table.size);
            }
        });
        Button minuswin = new Button("-");
        minuswin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!table.started) {
                    if (table.w > 1) {
                        table.w--;
                        curwin.setText("Consecutive needed: " + table.w);
                        switch (table.whowon()) {
                            case empty:
                                whowon.setText("");
                                break;
                            case X:
                                whowon.setText("X won!");
                                break;
                            case O:
                                whowon.setText("O won!");
                                break;
                        }

                    }
                }
            }
        });
        Button pluswin = new Button("+");
        pluswin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!table.started) {
                    if (table.w < table.size) {
                        table.w++;
                        curwin.setText("Consecutive needed: " + table.w);
                        switch (table.whowon()) {
                            case empty:
                                whowon.setText("");
                                break;
                            case X:
                                whowon.setText("X won!");
                                break;
                            case O:
                                whowon.setText("O won!");
                                break;
                        }
                    }
                }
            }
        });
        Button undo = new Button("Undo");
        undo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(table.nosteps>=0) table.undo(gc);
                switch (table.whowon()) {
                    case empty:
                        whowon.setText("");
                        break;
                    case X:
                        whowon.setText("X won!");
                        break;
                    case O:
                        whowon.setText("O won!");
                        break;
                }
            }
        });
        Button redo = new Button("Redo");
        redo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (table.nosteps+1 < table.steps.size()) table.redo(gc);
                switch (table.whowon()) {
                    case empty:
                        whowon.setText("");
                        break;
                    case X:
                        whowon.setText("X won!");
                        break;
                    case O:
                        whowon.setText("O won!");
                        break;
                }
            }
        });
        Button changefirst = new Button(table.first + " starts.");
        changefirst.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!table.started)
                {
                    table.changefirst();
                    changefirst.setText(table.first + " starts.");
                }
            }
        });

        hbox.getChildren().addAll(clear,minussize,cursize,plussize,minuswin,curwin,pluswin,undo,redo,changefirst,whowon);

        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!table.won)
                {
                    for(int i = 0; i<table.size; i++)
                        for(int j = 0; j<table.size; j++)
                        {
                            if(table.get(i,j).inside(event.getSceneX(),event.getSceneY()) && table.get(i,j).type == Type.empty)
                            {
                                switch (table.step(i,j))
                                {
                                    case empty:
                                        if(table.nosteps == table.size *table.size -1) whowon.setText("Tie!");
                                        else whowon.setText("");
                                        break;
                                    case X:
                                        whowon.setText("X won!");
                                        break;
                                    case O:
                                        whowon.setText("O won!");
                                        break;
                                }
                                switch (table.whowon())
                                {
                                    case empty:
                                        whowon.setText("");
                                        break;
                                    case X:
                                        whowon.setText("X won!");
                                        break;
                                    case O:
                                        whowon.setText("O won!");
                                        break;
                                }
                                break;
                            }
                        }
                }
            }
        });


        root.getChildren().add(vbox);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
