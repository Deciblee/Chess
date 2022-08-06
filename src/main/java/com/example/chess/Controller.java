package com.example.chess;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;


class ImageStore{
    static Image[] W = new Image[17];
    static Image[] B = new Image[17];
    static Image empty;
    //int c = 0;

    public ImageStore(){
        try {
            //c++;
            //System.out.println("img get" + c);
            FileInputStream imgFile;
            imgFile = new FileInputStream("target/classes/img/W_1.png");
            W[1] = new Image(imgFile);
            for (int i = 2;i<9;i++){
                W[i] = W[1];
            }
            imgFile = new FileInputStream("target/classes/img/W_9.png");
            W[9] = new Image(imgFile);
            W[10] = W[9];
            imgFile = new FileInputStream("target/classes/img/W_11.png");
            W[11] = new Image(imgFile);
            W[12] = W[11];
            imgFile = new FileInputStream("target/classes/img/W_13.png");
            W[13] = new Image(imgFile);
            W[14] = W[13];
            imgFile = new FileInputStream("target/classes/img/W_15.png");
            W[15] = new Image(imgFile);
            imgFile = new FileInputStream("target/classes/img/W_16.png");
            W[16] = new Image(imgFile);

            imgFile = new FileInputStream("target/classes/img/B_1.png");
            B[1] = new Image(imgFile);
            for (int i = 2;i<9;i++){
                B[i] = B[1];
            }
            imgFile = new FileInputStream("target/classes/img/B_9.png");
            B[9] = new Image(imgFile);
            B[10] = B[9];
            imgFile = new FileInputStream("target/classes/img/B_11.png");
            B[11] = new Image(imgFile);
            B[12] = B[11];
            imgFile = new FileInputStream("target/classes/img/B_13.png");
            B[13] = new Image(imgFile);
            B[14] = B[13];
            imgFile = new FileInputStream("target/classes/img/B_15.png");
            B[15] = new Image(imgFile);
            imgFile = new FileInputStream("target/classes/img/B_16.png");
            B[16] = new Image(imgFile);
            imgFile = new FileInputStream("target/classes/img/empty.png");
            empty = new Image(imgFile);
        } catch (FileNotFoundException ex) {
            System.out.println("img error");
        }
    }

}

class Table extends ImageStore{
    private Chessman[][] chessman = new Chessman[9][9];
    private int player_side = 1;
    private Label winner;
    private boolean fullStop = false;

    class Chessman{
        private int ID = 0;
        private int positionX = 0;
        private int positionY = 0;
        private ImageView imgChess;

        public Chessman(int ID, int X, int Y){
            this.ID = ID;
            this.positionX = X;
            this.positionY = Y;
            Refresh();
        }

        public void setImgChess(ImageView IV){
            this.imgChess = IV;
            String store = (ID + "," + positionX + "," + positionY);

            this.imgChess.setOnDragDetected((MouseEvent event) -> {
                Dragboard db = imgChess.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(store);
                content.putImage(imgChess.getImage());
                db.setContent(content);
                event.consume();
            });

            this.imgChess.setOnDragOver(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    if (event.getGestureSource() != imgChess && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        //System.out.println("dnd done if in");
                        //System.out.println("string = " + event.getDragboard().getString());
                    }else{
                        //System.out.println("dnd done if out");
                    }
                    event.consume();

                }
            });

            this.imgChess.setOnDragEntered(event -> {
                //System.out.println("onDragEntered");
                String[] coordinates = event.getDragboard().getString().split(",");
                //System.out.println("img swap checking");
                //System.out.println("old x = "+Integer.parseInt(coordinates[1])+ " old y = " + Integer.parseInt(coordinates[2]));
                //System.out.println("new x = "+positionX+ " new y = " + positionY);
                if (event.getGestureSource() != this.imgChess && event.getDragboard().hasString() && (MovementCheck(Integer.parseInt(coordinates[1]),Integer.parseInt(coordinates[2]),this.positionX,this.positionY)))
                {
                    //System.out.println("img swap true");
                    //System.out.println("x = "+Integer.parseInt(coordinates[1])+ " y = " + Integer.parseInt(coordinates[2]));
                    this.imgChess.setImage(event.getDragboard().getImage());
                }

                event.consume();
            });

            this.imgChess.setOnDragExited(event -> {
                Refresh();
                event.consume();
            });

            this.imgChess.setOnDragDropped(event -> {
                //System.out.println("onDragDropped");
                Dragboard db = event.getDragboard();
                boolean success = false;
                String[] coordinates = event.getDragboard().getString().split(",");
                if (db.hasString() && (MovementCheck(Integer.parseInt(coordinates[1]),Integer.parseInt(coordinates[2]),this.positionX,this.positionY))) {
                    //System.out.println("THIS.ID = " + this.ID+ "new x = "+positionX+ " new y = " + positionY);
                    Movement(Integer.parseInt(coordinates[1]),Integer.parseInt(coordinates[2]),positionX,positionY);
                    Refresh();
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            });

            this.imgChess.setOnDragDone(event -> {
                //System.out.println("onDragDone");
                if (event.getTransferMode() == TransferMode.MOVE) {
                    Zero();
                    Refresh();
                }
                event.consume();
            });
        }

        public void setID(int newID){
            this.ID = newID;
            Refresh();
        }

        public void Zero(){
            this.ID = 0;
            Refresh();
        }

        public boolean Move(int nextX, int nextY){
            //System.out.println("ID: "+ ID);
            //System.out.println("positionX: " + positionX + " |positionY: " + positionY + " |nextX : " + nextX + " |nextY: " + nextY);

            if ((positionX==nextX)&&(positionY==nextY)){return false;}
            if (Math.abs(ID)<9){
                if ((this.positionX==2||this.positionX==7)&&(nextY==positionY)){
                    if (((nextX-positionX)*this.ID==2*Math.abs(this.ID)) || ((nextX-positionX)*this.ID==Math.abs(this.ID))){
                        return true;
                    }
                }else if (((nextX-positionX)*this.ID==Math.abs(this.ID))&&(nextY==positionY)){
                    return true;
                }else {
                    return false;
                }
            }else if (Math.abs(ID)<11){
                if (((positionX==nextX)&&(positionY!=nextY))||((positionX!=nextX)&&(positionY==nextY))){
                    return true;
                }else {
                    return false;
                }
            }else if (Math.abs(ID)<13){
                if (((Math.abs(positionX-nextX)+Math.abs(positionY-nextY))==3)&&(positionY!=nextY)&&(positionX!=nextX)){
                    return true;
                }else {
                    return false;
                }
            }else if (Math.abs(ID)<15){
                if (((positionX-positionY)==(nextX-nextY))||((positionX+positionY)==(nextX+nextY))){
                    return true;
                }else {
                    return false;
                }
            }else if (Math.abs(ID)==15){
                if (((positionX-positionY)==(nextX-nextY))||((positionX+positionY)==(nextX+nextY))||(((positionX==nextX)&&(positionY!=nextY))||((positionX!=nextX)&&(positionY==nextY)))){
                    return true;
                }else {
                    return false;
                }
            }else if (Math.abs(ID)==16){
                if ((Math.abs(positionX-nextX)<=1)&&(Math.abs(positionY-nextY)<=1)){
                    return true;
                }else {
                    return false;
                }
            }
            //System.out.println("WTF wrong ID:"+ID);
            return false;
        }

        public boolean Kill(int nextX, int nextY){
            if (Math.abs(this.getID())<9) {
                if (((nextX-positionX)*this.ID==Math.abs(this.ID))&&(Math.abs(nextY-positionY)==1)){
                    return true;
                }else {
                    return false;
                }
            }else{
                return Move(nextX,nextY);
            }
        }

        public int getID(){
            return ID;
        }

        public int getPositionX(){
            return positionX;
        }

        public int getPositionY(){
            return positionY;
        }

        public void Refresh(){
            try {
                if (ID>0){
                    imgChess.setImage(W[Math.abs(ID)]);
                }else if (ID<0){
                    imgChess.setImage(B[Math.abs(ID)]);
                }else {
                    imgChess.setImage(empty);
                }
            }
            catch(NullPointerException e) {

            }
        }

    }

    public Table() {
        for (int i = 1; i <= 16; i++) {
            if (i < 9) {
                //System.out.println("i = " + i);
                chessman[2][i] = new Chessman(i, 2, i);
                chessman[7][i] = new Chessman(-i, 7, i);
            } else if (i == 9) {
                chessman[1][1] = new Chessman(i, 1, 1);
                chessman[8][1] = new Chessman(-i, 8, 1);
            } else if (i == 10) {
                chessman[1][8] = new Chessman(i, 1, 8);
                chessman[8][8] = new Chessman(-i, 8, 8);
            } else if (i == 11) {
                chessman[1][2] = new Chessman(i, 1, 2);
                chessman[8][2] = new Chessman(-i, 8, 2);
            } else if (i == 12) {
                chessman[1][7] = new Chessman(i, 1, 7);
                chessman[8][7] = new Chessman(-i, 8, 7);
            } else if (i == 13) {
                chessman[1][3] = new Chessman(i, 1, 3);
                chessman[8][3] = new Chessman(-i, 8, 3);
            } else if (i == 14) {
                chessman[1][6] = new Chessman(i, 1, 6);
                chessman[8][6] = new Chessman(-i, 8, 6);
            } else if (i == 15) {
                chessman[1][4] = new Chessman(i, 1, 4);
                chessman[8][4] = new Chessman(-i, 8, 4);
            } else if (i == 16) {
                chessman[1][5] = new Chessman(i, 1, 5);
                chessman[8][5] = new Chessman(-i, 8, 5);
            }
        }
        for (int i = 3; i < 7; i++) {
            for (int k = 1; k < 9; k++) {
                chessman[i][k] = new Chessman(0, i, k);
            }
        }
    }

    public void setLabel(Label label){
        winner = label;
    }

    public void FullRefresh(){
        for (int i = 1;i<9;i++){
            for (int k = 1; k<9; k++){
                chessman[i][k].Refresh();
            }
        }
    }

    public void ShowLog() {

        System.out.println("     1     2     3     4     5     6     7     8  ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + " ");
            for (int k = 1; k <= 8; k++) {
                System.out.printf("|%3d |", chessman[i][k].getID());
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public void setImg(int x,int y,ImageView IV){
        chessman[x][y].setImgChess(IV);
    }

    public boolean ObstacleCheck(int positionX, int positionY, int nextX, int nextY) {
        int ID = Math.abs(chessman[positionX][positionY].getID());
        //System.out.println("ID: "+ ID);
        //System.out.println("positionX: " + positionX + " |positionY: " + positionY + " |nextX : " + nextX + " |nextY: " + nextY);

        if ((positionX == nextX) && (positionY == nextY)) {
            return false;
        }
        if (ID < 9 || ID == 16 || ID == 11 || ID == 12) {
            //System.out.println("special");
            return true;
        } else if (ID < 11) {
            int start;
            int end;
            if (positionX == nextX) {
                if (positionY < nextY) {
                    start = positionY;
                    start++;
                    end = nextY;
                } else {
                    start = nextY;
                    start++;
                    end = positionY;
                }
                for (; start < end; start++) {
                    if (chessman[positionX][start].getID() != 0) {
                        return false;
                    }
                }
                return true;
            } else {
                if (positionX < nextX) {
                    start = positionX;
                    start++;
                    end = nextX;
                } else {
                    start = nextX;
                    start++;
                    end = positionX;
                }
                for (; start < end; start++) {
                    if (chessman[start][positionY].getID() != 0) {
                        return false;
                    }
                }
                return true;
            }
        } else if (ID < 15) {
            int directionX = 1;
            int directionY = 1;
            if (positionX > nextX) {
                directionX = -1;
            }
            if (positionY > nextY) {
                directionY = -1;
            }
            positionX += directionX;
            positionY += directionY;
            while (positionX != nextX || positionY != nextY) {
                //System.out.println("directionX: " + directionX + "directionY: " + directionY + "positionX: " + positionX + " |nextX: " + nextX + " |positionY : " + positionY + " |nextY: " + nextY);
                if (chessman[positionX][positionY].getID() != 0) {
                    return false;
                }
                positionX += directionX;
                positionY += directionY;
            }
            return true;
        } else if (ID == 15) {
            if (positionX == nextX || positionY == nextY) {
                int start;
                int end;
                if (positionX == nextX) {
                    if (positionY < nextY) {
                        start = positionY;
                        start++;
                        end = nextY;
                    } else {
                        start = nextY;
                        start++;
                        end = positionY;
                    }
                    for (; start < end; start++) {
                        if (chessman[positionX][start].getID() != 0) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    if (positionX < nextX) {
                        start = positionX;
                        start++;
                        end = nextX;
                    } else {
                        start = nextX;
                        start++;
                        end = positionX;
                    }
                    for (; start < end; start++) {
                        if (chessman[start][positionY].getID() != 0) {
                            return false;
                        }
                    }
                    return true;
                }
            } else {
                int directionX = 1;
                int directionY = 1;
                if (positionX > nextX) {
                    directionX = -1;
                }
                if (positionY > nextY) {
                    directionY = -1;
                }
                positionX += directionX;
                positionY += directionY;
                while (positionX != nextX || positionY != nextY) {
                    //System.out.println("directionX: " + directionX + "directionY: " + directionY + "positionX: " + positionX + " |nextX: " + nextX + " |positionY : " + positionY + " |nextY: " + nextY);
                    if (chessman[positionX][positionY].getID() != 0) {
                        return false;
                    }
                    positionX += directionX;
                    positionY += directionY;
                }
                return true;
            }
        } else {
            //System.out.println("WTF wrong ID:" + chessman[positionX][positionX].getID());
            return false;
        }
    }

    public boolean MovementCheck(int oldX, int oldY, int newX, int newY) {
        boolean approvalMove, approvalKill, approval;
        if (player_side * chessman[oldX][oldY].getID() <= 0) {
            //System.out.println("Wrong chessman: " + chessman[oldX][oldY].getID());
            approvalMove = false;
            approvalKill = false;
        } else if (chessman[newX][newY].getID() == 0) {
            approvalMove = chessman[oldX][oldY].Move(newX, newY);
            approvalKill = false;
        } else if (chessman[newX][newY].getID() * chessman[oldX][oldY].getID() < 0) {
            approvalKill = chessman[oldX][oldY].Kill(newX, newY);
            approvalMove = false;
        } else {
            approvalMove = false;
            approvalKill = false;
        }

        if (approvalMove || approvalKill) {
            approval = ObstacleCheck(oldX, oldY, newX, newY);
        } else {
            approval = false;
        }

        //System.out.println("approvalMove: " + approvalMove);
        //System.out.println("approvalKill: " + approvalKill);
        //System.out.println("approval: " + approval);

        return approval;
    }

    public void Movement(int oldX, int oldY, int newX, int newY){

        boolean approval = MovementCheck(oldX, oldY, newX, newY);
        //System.out.println("old ID = " + chessman[oldX][oldY].getID()  + "   x = " + oldX + "   y = " + oldY);
        //System.out.println("new ID = " + chessman[newX][newY].getID()  + "   x = " + newX + "   y = " + newY);
        //System.out.println("approval = " + approval);
        Chessman temp_chessman;

        if (approval&&!fullStop) {
            temp_chessman = new Chessman(chessman[oldX][oldY].getID(), chessman[oldX][oldY].getPositionX(), chessman[oldX][oldY].getPositionY());
            chessman[oldX][oldY].Zero();
            if (Math.abs(chessman[newX][newY].getID()) == 16) {
                EndGame(player_side, newX, newY, temp_chessman.getID());
            }else {
                //System.out.println("player swap");
                chessman[newX][newY].setID(temp_chessman.getID());
                if (player_side == 1) {
                    player_side = -1;
                } else {
                    player_side = 1;
                }
            }
        }

        FullRefresh();

    }

    public void EndGame(int winner_side, int newX, int newY, int ID) {
        if (winner_side == 1) {
            winner.setText("player 1 wins");
            //System.out.println("player 1 wins");
            chessman[newX][newY].setID(ID);
            FullRefresh();
            fullStop = false;
            //ShowLog();
            //System.exit(0);

        } else if (winner_side == -1) {
            winner.setText("player 2 wins");
            //System.out.println("player 2 wins");
            chessman[newX][newY].setID(ID);
            FullRefresh();
            fullStop = true;
            //ShowLog();
            //System.exit(0);
        } else {
            //System.out.println("False call");
        }
    }
}


public class Controller implements Initializable {

    @FXML
    public SplitPane rootPane;
    @FXML
    public ImageView backGround;
    @FXML
    public GridPane cellGridPane;
    @FXML
    public Label winner;



    public void initialize(URL location, ResourceBundle resources) {

        try {
            FileInputStream imgFile = new FileInputStream("img/BG.png");
            Image image = new Image(imgFile);
            backGround.setImage(image);
        }
        catch(FileNotFoundException ex) {
        }

        Table table = new Table();

        for (int i = 0 ;i<8;i++){
            for (int k = 0 ;k<8;k++){
                ImageView imageView = new ImageView();
                cellGridPane.add(imageView,k,i);
                imageView.setPickOnBounds(true);
                table.setImg((i+1),(k+1),imageView);
            }
        }

        table.setLabel(winner);

        //table.ShowLog();
        table.FullRefresh();

    }

}