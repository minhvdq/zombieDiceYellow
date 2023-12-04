public class ZombieDice {
    //final double smallNumber = 1e-14;
    //final int maxScore = 20;

    //calculate pDiceRoll to get brain brains and sg shotguns out of dice dice left
    static double rollDice(double[][][] pDiceRoll, int dice, int brain, int sg) {
        if (dice == 0 && brain == 0 && sg == 0) {
            pDiceRoll[dice][brain][sg] = 1.0;
        }
        if( dice < brain + sg){
            pDiceRoll[dice][brain][sg] = 0.0;
        }
        if (pDiceRoll[dice][brain][sg] == -1.0) {
            pDiceRoll[dice][brain][sg] = 0;
            pDiceRoll[dice][brain][sg] += (double) 1 / 3 * rollDice(pDiceRoll, dice - 1, brain, sg);
            if (brain > 0 ) {
                pDiceRoll[dice][brain][sg] += (double) 1 / 3 * rollDice(pDiceRoll, dice - 1, brain - 1, sg);
            }
            if (sg > 0 ) {
                pDiceRoll[dice][brain][sg] += (double) 1 / 3 * rollDice(pDiceRoll, dice - 1, brain, sg - 1);
            }
        }
        return pDiceRoll[dice][brain][sg];
    }

    //calculate pExceed at scoreDiff and sRolled number of shotgun rolled
    static double computepExceed( double[][][] pDiceRoll, double[][] pExceed, int scoreDiff, int sRolled, double change ){
        if( change <= 1e-14){
            //there will be a chance where player keeps rolling all dice with footstep
            //set it to 1 if the probability of it go smaller than 1e-14( a super small
            //number )  to avoid infinite loop.
            return 1.0;
        }
        System.out.println(change);
        if(sRolled >= 3){
            return 0.0;
        }
        if( scoreDiff < 0){
            return 1.0;
        }
        if( scoreDiff == 0){
            pExceed[scoreDiff][sRolled] = 1.0;
        }
        if( pExceed[scoreDiff][sRolled] == -1.0){
            pExceed[scoreDiff][sRolled] = 0;
            for( int sg = 0; sg <=3; sg ++){
                for( int b = 0; b <= 3 - sg; b ++){
                    double curChange = pDiceRoll[3][b][sg] * change;
                    pExceed[scoreDiff][sRolled] += pDiceRoll[3][b][sg] * computepExceed(pDiceRoll, pExceed, scoreDiff - b, sRolled + sg, curChange);
                }
            }
        }
        return pExceed[scoreDiff][sRolled];
    }

    static void valueIterate( double[][][][][] pWin, double[][][] pDiceRoll, double[][] pExceed )
    {
        double maxChange = 0.0;

        do{
            maxChange = 0.0;
            for( int curPlayer = 0; curPlayer < 2; curPlayer ++){
                for( int curScore = 0; curScore < 20; curScore ++){
                    for( int opScore = 0; opScore < 20; opScore ++){
                        for( int turnTotal = 0; turnTotal < 20; turnTotal ++ ){
                            for( int  sg = 0; sg <= 2; sg  ++){
                                double oldValue = pWin[curPlayer][curScore][opScore][turnTotal][sg];
                                double pHold = 0;
                                if( turnTotal == 0){
                                    pHold = 0.0;
                                }
                                else if( curPlayer == 1 && curScore + turnTotal < opScore && opScore >= 10){
                                    pHold = 0.0;
                                }
                                else if( curPlayer == 1 && curScore + turnTotal < opScore && opScore >= 10){
                                    pHold = 1.0;
                                }
                                else{
                                    pHold = 1 - computeProbWin(pWin, 1 - curPlayer, opScore, curScore + turnTotal, 0, 0);
                                }
                                double pRoll = 0.0;
                                if( curPlayer == 1 && opScore >= 10 && curScore + turnTotal < opScore){
                                    int scoreDiff = opScore - curScore - turnTotal;
                                    pRoll = pExceed[scoreDiff][sg];
                                }
                                else{
                                    for( int newSg = 0; newSg <= 2 - sg; newSg ++){
                                        for( int newB = 0 ; newB <= 3 - newSg; newB ++){
                                            pRoll += pDiceRoll[3][newB][newSg] * computeProbWin(pWin, curPlayer, curScore, opScore, turnTotal + newB, sg + newSg);
                                        }
                                    }
                                    for( int newSg = 3 - sg; newSg <= 3; newSg ++){
                                        for( int newB = 0 ; newB <= 3 - newSg; newB ++){
                                            pRoll += pDiceRoll[3][newB][newSg] * (1 - computeProbWin( pWin, 1 - curPlayer, opScore, curScore, 0, 0) );
                                        }
                                    }
                                }
                                pWin[curPlayer][curScore][opScore][turnTotal][sg] = pRoll >= pHold ? pRoll : pHold;
                                double curChange = Math.abs(oldValue - pWin[curPlayer][curScore][opScore][turnTotal][sg] );
                                maxChange = Math.max(maxChange, curChange);
                                //System.out.println(maxChange);
                            }
                        }
                    }
                }
            }
        }
        while(maxChange > 1e-14);
        System.out.println(maxChange);
    }

    static double computeProbWin( double[][][][][] pWin, int curPlayer, int curScore, int opScore, int turnTotal, int sg ){
        if( curPlayer == 0 && curScore >= 10){
            return curScore >= opScore ? 1.0 : 0.0;
        }
        if( curPlayer == 1 && curScore + turnTotal >= 10 && curScore + turnTotal > opScore){
            return 1.0;
        }
        if( curScore >= 20) curScore = 19;
        if( opScore >= 20) opScore = 19;
        if( curScore + turnTotal >= 20) turnTotal = 20 - 1 - curScore;
        return pWin[curPlayer][curScore][opScore][turnTotal][sg];
    }

    public static void main(String[] args) {
        int numDice = 3;
        double[][][] pDiceRoll = new double[numDice + 1][numDice + 1][numDice + 1];
        double[][] pExceed = new double[20 + 1][3];
        double[][][][][] pWin = new double[2][20][20][20][3];
        
        //initialize pDiceRoll with -1
        for (int i = 1; i <= numDice; i++) {
            for (int j = 0; j <= i; j++) {
                for (int k = 0; k <= i - j; k++) {
                    pDiceRoll[i][j][k] = -1.0;
                }
            }
        }

        //initialize pExceed with -1
        for( int i = 0; i < 20 + 1; i ++){
            for( int j = 0; j < 3; j ++){
                pExceed[i][j] = -1;
            }
        }
        // Initialize the array with 1.0
        /*
        for (int i = 0; i <= numDice; i++) {
            for (int j = 0; j <= numDice; j++) {
                pDiceRoll[0][i][j] = 1.0;
            }
        }
        */

        //calculate all pDiceRoll ( pOutcome )
        for (int i = 1; i <= numDice; i++) {
            for (int j = 0; j <= i; j++) {
                for (int k = 0; k <= i - j; k++) {
                    rollDice(pDiceRoll, i, j, k);
                }
            }
        }
        //* 

        //calculate all pExceed
        for( int i  = 0; i <= 20; i ++){
            for( int j = 0; j < 3; j ++){
                if( i == 0){
                    pExceed[i][j] = 1.0;
                }
                else{
                    pExceed[i][j] = computepExceed(pDiceRoll, pExceed, i, j, 1.0 );
                }
            }
        }
        //*/
        valueIterate(pWin, pDiceRoll, pExceed);
        System.out.println(pWin[0][2][0][0][0]);
        System.out.println(pExceed[8][2]);
        System.out.println(pDiceRoll[1][0][0]);
    }
}
