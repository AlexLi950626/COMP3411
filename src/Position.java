/**
 * position used to store col and row
 */

public class Position implements Cloneable {
    private int row;
    private int col;

    /**
     * another constructor used to take getRow and getCol input and make new coordinates
     * @param row
     * @param col
     */
    public Position(int row, int col){
        this.row = row;
        this.col = col;
    }

    /**
     * typical setter set the row
     * @param row
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * typical setter set the col
     * @param col
     */
    public void setCol(int col){
        this.col = col;
    }

    /**
     * typical getter get the row
     * @return value of row
     */
    public int getRow(){
        return row;
    }

    /**
     * typical getter get the col value
     * @return value of col
     */
    public int getCol(){
        return col;
    }

    /**
     * check if two position object is the same
     * @param compare
     * @return true if objects are equal
     */
    @Override
    public boolean equals(Object compare){
        if(compare == this){
            return true;
        } else {
            if(compare.getClass().equals(this.getClass())){
                Position x = (Position) compare;
                return x.row == this.row && x.col == this.col;
            } else {
                return false;
            }
        }
    }

    @Override
    /**
     * return string format of the object
     */
    public String toString(){
        return row + "," + col;
    }

    @Override
    /**
     * return cloned object
     */
    public Position clone(){
        return new Position(this.getRow(), this.getCol());
    }

}
