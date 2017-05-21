public class Position {
    private int row;
    private int col;

    /**
     * return new position object
     * -1 means not valid
     */
    public Position(){
        row = -1;
        col = -1;
    }

    /**
     * another constructor used to take getRow and getCol input and make new coordinates
     * @param row
     * @param col
     */
    public Position(int row, int col){
        this.row = row;
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }
    public void setCol(int col){
        this.col = col;
    }
    public int getRow(){
        return row;
    }
    public int getCol(){
        return col;
    }
}
