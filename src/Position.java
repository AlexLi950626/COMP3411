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
        return (this.getRow()+"+"+this.getRow()).equals(compare);
    }

    /**
     * return the hash value of this object
     * @return the hash value of the object
     */
    @Override
    public int hashCode(){
        return (this.getRow()+"+"+this.getRow()).hashCode();
    }
}
